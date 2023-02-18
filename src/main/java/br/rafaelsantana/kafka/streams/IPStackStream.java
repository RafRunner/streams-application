package br.rafaelsantana.kafka.streams;

import br.rafaelsantana.Constants;
import br.rafaelsantana.cache.InputHistory;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class IPStackStream {

    static final Logger logger = Logger.getLogger(IPStackStream.class.getName());

    private final InputHistory inputHistory;

    private final IPStackService.IPStackClient client;

    @Autowired
    public IPStackStream(
            InputHistory inputHistory,
            IPStackService.IPStackClient client
    ) {
        this.inputHistory = inputHistory;
        this.client = client;
    }

    public static final String COMPLETE_IPSTACK_TABLE = "complete_ipstacks";
    public static final String IPSTACK_BY_CLIENT_TABLE = "ipstacks_by_client";

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder, Constants constants) {
        KStream<String, IPStack> source = streamsBuilder.stream(constants.INPUT_TOPIC);

        KStream<String, IPStack> processedStream = source
                .mapValues(this::fillInformation)
                .filter(((key, ipStack) -> ipStack != null));

        processedStream
                .groupBy((key, ipStack) -> ipStack.ip)
                .reduce((ipStack1, ipStack2) -> ipStack1.timeStamp > ipStack2.timeStamp ? ipStack1 : ipStack2,
                        Materialized.as(COMPLETE_IPSTACK_TABLE));

        processedStream
                .groupBy((key, ipStack) -> ipStack.clientId)
                .reduce((ipStack1, ipStack2) -> ipStack1.timeStamp > ipStack2.timeStamp ? ipStack1 : ipStack2,
                        Materialized.as(IPSTACK_BY_CLIENT_TABLE));

        processedStream.to(constants.OUTPUT_TOPIC);
    }

    private IPStack fillInformation(IPStack inputStack) {
        logger.info("Received record: " + inputStack);
        if (inputStack == null) {
            logger.warning("Record ignored because it's value couldn't be parsed");
            return null;
        }

        if (inputHistory.shouldSendOutputMessage(inputStack)) {
            try {
                IPStack response = client.getIpInformation(inputStack.ip).get();
                inputStack.completeWithApiResponse(response);

                logger.info("Record sent to output stream with value %s".formatted(inputStack));

                inputHistory.registerProcessedInput(inputStack);

                return inputStack;
            } catch (InterruptedException | ExecutionException e) {
                logger.severe("Error while making request: %s\nError: %s".formatted(inputStack, e));
            }
        } else {
            logger.info("Record not sent to output because it wasn't needed");
        }

        return null;
    }
}
