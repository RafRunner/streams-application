package br.rafaelsantana.controllers;

import br.rafaelsantana.Constants;
import br.rafaelsantana.kafka.producers.IPStackProducer;
import br.rafaelsantana.kafka.streams.IPStackStream;
import br.rafaelsantana.model.IPStack;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/ipstack")
public class IPStackController {

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @Autowired
    private IPStackProducer ipStackProducer;

    @Autowired
    private Constants constants;

    @GetMapping("/get/{ip}")
    public IPStack getCompleteStack(@PathVariable String ip) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams == null) {
            return null;
        }
        ReadOnlyKeyValueStore<String, IPStack> counts = kafkaStreams
                .store(StoreQueryParameters.fromNameAndType(IPStackStream.COMPLETE_IPSTACK_TABLE, QueryableStoreTypes.keyValueStore()));
        return counts.get(ip);
    }

    @PostMapping("/send")
    public void sendIpStack(@RequestBody IPStack ipStack) {
        ipStackProducer.sendIPStack(ipStack, constants.INPUT_TOPIC);
    }
}
