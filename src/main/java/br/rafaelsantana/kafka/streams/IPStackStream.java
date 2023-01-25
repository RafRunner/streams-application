package br.rafaelsantana.kafka.streams;

import br.rafaelsantana.Constants;
import br.rafaelsantana.cache.InputHistory;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class IPStackStream {

    static final Logger logger = Logger.getLogger(IPStackStream.class.getName());

    @Autowired
    private InputHistory inputHistory;

    @Autowired
    private IPStackService.IPStackClient client;

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder, Constants constants) {
        KStream<String, IPStack> source = streamsBuilder.stream(constants.INPUT_TOPIC);

        KStream<String, IPStack> processedStream = source
                .mapValues(this::fillInformation)
                .filter(((key, ipStack) -> ipStack != null));

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
