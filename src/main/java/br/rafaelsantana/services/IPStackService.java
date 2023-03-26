package br.rafaelsantana.services;

import br.rafaelsantana.kafka.streams.IPStackStream;
import br.rafaelsantana.model.IPStack;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IPStackService {

    private final StreamsBuilderFactoryBean factoryBean;

    @Autowired
    IPStackService(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    public Optional<IPStack> getMostRecentCompleteStack(String ip) {
        return Optional.ofNullable(getReadOnlyKeyValueStore(IPStackStream.COMPLETE_IPSTACK_TABLE).get(ip));
    }

    public Optional<IPStack> getMostRecentStackByClient(String clientId) {
        return Optional.ofNullable(getReadOnlyKeyValueStore(IPStackStream.IPSTACK_BY_CLIENT_TABLE).get(clientId));
    }

    private ReadOnlyKeyValueStore<String, IPStack> getReadOnlyKeyValueStore(String tableName) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams == null) {
            throw new RuntimeException("Kafka streams have not been started yet!");
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(tableName, QueryableStoreTypes.keyValueStore())
        );
    }
}
