package br.rafaelsantana;

import br.rafaelsantana.kafka.MyKafkaConsumer;
import br.rafaelsantana.kafka.MyKafkaProducer;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        IPStackService.IPStackClient client = IPStackService.buildClient();

        try (MyKafkaConsumer<IPStack> consumer
                     = new MyKafkaConsumer<IPStack>(AppConfig.INPUT_TOPIC, IPStack.class.getName())) {
            consumer.subscribe((record) -> {
                IPStack inputStack = record.value();

                try {
                    IPStack response = client
                            .getIpInformation(inputStack.ip)
                            .get(AppConfig.DEFAULT_TIMEOUT_REQUESTS, TimeUnit.MILLISECONDS);
                    inputStack.completeWithApiResponse(response);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    // TODO use logs
                    System.err.printf("Error while making request: %s\nError: %s", inputStack, e);
                }

                try (MyKafkaProducer<IPStack> producer = new MyKafkaProducer<IPStack>()) {
                    producer.sendRecord(
                            new ProducerRecord<>(AppConfig.OUTPUT_TOPIC, AppConfig.CLIENT_ID_CONFIG, inputStack));
                }
            });
        }
    }
}