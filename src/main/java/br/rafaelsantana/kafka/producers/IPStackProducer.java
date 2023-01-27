package br.rafaelsantana.kafka.producers;

import br.rafaelsantana.Constants;
import br.rafaelsantana.model.IPStack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@Component
public class IPStackProducer {

    static final Logger logger = Logger.getLogger(IPStackProducer.class.getName());

    @Autowired
    private KafkaTemplate<String, IPStack> kafkaTemplate;

    @Autowired
    private Constants constants;

    public SendResult<String, IPStack> sendIPStack(IPStack ipStack, String topicName) {
        try {
            logger.info("Sending IPStack: " + ipStack + " to topic: " + topicName);
            return kafkaTemplate.send(topicName, ipStack).get(constants.DEFAULT_TIMEOUT_KAFKA, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.severe("Error while sending record: " + e);
            return null;
        }
    }
}
