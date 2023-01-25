package br.rafaelsantana.kafka.consumers;

import br.rafaelsantana.model.IPStack;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class IncompleteIPStackConsumer {
    static final Logger logger = Logger.getLogger(IncompleteIPStackConsumer.class.getName());

    @KafkaListener(topics = "${kafka.input-topic}", groupId = "${kafka.group-id-config}", containerFactory = "ipStackKafkaListenerContainerFactory")
    public void listenForIPStacks(IPStack ipStack) {
        logger.info("Record received by Consumer: " + ipStack);
    }
}
