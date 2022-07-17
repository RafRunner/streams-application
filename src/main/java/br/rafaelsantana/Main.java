package br.rafaelsantana;

import br.rafaelsantana.cache.InputHistory;
import br.rafaelsantana.kafka.MyKafkaConsumer;
import br.rafaelsantana.kafka.MyKafkaProducer;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {
        InputHistory inputHistory = new InputHistory();
        IPStackService.IPStackClient client = IPStackService.buildClient();

        try (MyKafkaConsumer<IPStack> consumer = new MyKafkaConsumer<IPStack>(AppConfig.INPUT_TOPIC, IPStack.class.getName());
             MyKafkaProducer<IPStack> producer = new MyKafkaProducer<IPStack>()) {
            consumer.subscribe((record) -> {
                IPStack inputStack = record.value();
                if (inputStack == null) {
                    return;
                }

                if (inputHistory.shouldSendOutputMessage(inputStack)) {
                    try {
                        IPStack response = client.getIpInformation(inputStack.ip).get();
                        inputStack.completeWithApiResponse(response);

                        producer.sendRecord(
                                new ProducerRecord<>(AppConfig.OUTPUT_TOPIC, AppConfig.CLIENT_ID_CONFIG, inputStack));

                        inputHistory.registerProcessedInput(inputStack);
                    } catch (InterruptedException | ExecutionException e) {
                        // TODO use logs
                        System.err.printf("Error while making request: %s\nError: %s", inputStack, e);
                    }
                }
            });
        }
    }
}