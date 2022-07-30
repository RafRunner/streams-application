package br.rafaelsantana;

import br.rafaelsantana.cache.InputHistory;
import br.rafaelsantana.kafka.streams.IPStackStream;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Main {

    static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {
        InputHistory inputHistory = new InputHistory();
        IPStackService.IPStackClient client = IPStackService.buildClient();

        IPStackStream ipStackStream = new IPStackStream(AppConfig.INPUT_TOPIC, AppConfig.OUTPUT_TOPIC, (ipStack) -> {
            logger.info("Received record: " + ipStack);
            if (ipStack == null) {
                logger.warning("Record ignored because it's value couldn't be parsed");
                return null;
            }

            if (inputHistory.shouldSendOutputMessage(ipStack)) {
                try {
                    IPStack response = client.getIpInformation(ipStack.ip).get();
                    ipStack.completeWithApiResponse(response);

                    logger.info("Record sent to output stream with value %s".formatted(ipStack));

                    inputHistory.registerProcessedInput(ipStack);

                    return ipStack;
                } catch (InterruptedException | ExecutionException e) {
                    logger.severe("Error while making request: %s\nError: %s".formatted(ipStack, e));
                }
            }
            else {
                logger.info("Record not sent to output because it wasn't needed");
            }

            return null;
        });

        ipStackStream.getStreams().start();
    }
}