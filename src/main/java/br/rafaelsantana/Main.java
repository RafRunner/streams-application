package br.rafaelsantana;

import br.rafaelsantana.cache.InputHistory;
import br.rafaelsantana.kafka.MyKafkaConsumer;
import br.rafaelsantana.kafka.MyKafkaProducer;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Main {

    static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        InputHistory inputHistory = new InputHistory();
        IPStackService.IPStackClient client = IPStackService.buildClient();

        try (MyKafkaConsumer<IPStack> consumer = new MyKafkaConsumer<>(AppConfig.INPUT_TOPIC, IPStack.class.getName());
             MyKafkaProducer<IPStack> producer = new MyKafkaProducer<>()) {
            consumer.subscribe((record) -> {
                logger.info("Received record: " + record);
                Long offSet = record.offset();

                IPStack inputStack = record.value();
                if (inputStack == null) {
                    return;
                }

                logger.info("Record with offset %s value: %s".formatted(offSet, inputStack));

                if (inputHistory.shouldSendOutputMessage(inputStack)) {
                    try {
                        IPStack response = client.getIpInformation(inputStack.ip).get();
                        inputStack.completeWithApiResponse(response);

                        producer.sendRecord(
                                new ProducerRecord<>(AppConfig.OUTPUT_TOPIC, AppConfig.CLIENT_ID_CONFIG, inputStack));

                        logger.info("Record with offset %s sent to output stream with value %s".formatted(offSet, inputStack));

                        inputHistory.registerProcessedInput(inputStack);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.severe("Error while making request: %s\nError: %s".formatted(inputStack, e));
                    }
                }
                else {
                    logger.info("Record with offset %s not sent to output because it wasn't needed".formatted(offSet));
                }
            });
        }
    }
}