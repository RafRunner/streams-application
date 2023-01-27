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
    public IPStack getMostRecentCompleteStack(@PathVariable String ip) {
        ReadOnlyKeyValueStore<String, IPStack> store = getReadOnlyKeyValueStore(IPStackStream.COMPLETE_IPSTACK_TABLE);
        if (store == null) {
            return null;
        }
        return store.get(ip);
    }

    @GetMapping("/getByClient/{clientId}")
    public IPStack getMostRecentStackByClient(@PathVariable String clientId) {
        ReadOnlyKeyValueStore<String, IPStack> store = getReadOnlyKeyValueStore(IPStackStream.IPSTACK_BY_CLIENT_TABLE);
        if (store == null) {
            return null;
        }
        return store.get(clientId);
    }

    @PostMapping("/send")
    public void sendIpStack(@RequestBody IPStack ipStack) {
        ipStackProducer.sendIPStack(ipStack, constants.INPUT_TOPIC);
    }

    private ReadOnlyKeyValueStore<String, IPStack> getReadOnlyKeyValueStore(String tableName) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams == null) {
            return null;
        }
        return kafkaStreams
                .store(StoreQueryParameters.fromNameAndType(tableName, QueryableStoreTypes.keyValueStore()));
    }
}
