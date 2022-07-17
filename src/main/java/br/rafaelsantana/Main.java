package br.rafaelsantana;

import br.rafaelsantana.kafka.MyKafkaConsumer;
import br.rafaelsantana.kafka.MyKafkaProducer;
import br.rafaelsantana.model.IPStack;
import br.rafaelsantana.services.IPStackService;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {
        Map<String, Map<String, IPStack>> inputHistory = new HashMap<>();
        IPStackService.IPStackClient client = IPStackService.buildClient();

        try (MyKafkaConsumer<IPStack> consumer
                     = new MyKafkaConsumer<IPStack>(AppConfig.INPUT_TOPIC, IPStack.class.getName());
             MyKafkaProducer<IPStack> producer = new MyKafkaProducer<IPStack>()) {
            consumer.subscribe((record) -> {
                IPStack inputStack = record.value();
                if (inputStack == null) {
                    return;
                }

                String clientId = inputStack.clientId;
                String ip = inputStack.ip;

                if (!inputHistory.containsKey(clientId)) {
                    inputHistory.put(clientId, new HashMap<>());
                }

                IPStack lastRecord = inputHistory.get(clientId).get(ip);
                boolean stale = lastRecord == null ||
                        Instant.now().getEpochSecond() - lastRecord.timeStamp > AppConfig.DEFAULT_CACHE_MAX_AGE;
                if (stale) {
                    try {
                        IPStack response = client.getIpInformation(ip).get();
                        inputStack.completeWithApiResponse(response);

                        producer.sendRecord(
                                new ProducerRecord<>(AppConfig.OUTPUT_TOPIC, AppConfig.CLIENT_ID_CONFIG, inputStack));

                        inputHistory.get(clientId).put(ip, inputStack);
                    } catch (InterruptedException | ExecutionException e) {
                        // TODO use logs
                        System.err.printf("Error while making request: %s\nError: %s", inputStack, e);
                    }
                }
            });
        }
    }
}