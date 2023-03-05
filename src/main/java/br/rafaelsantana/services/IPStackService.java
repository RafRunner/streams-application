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
        return getReadOnlyKeyValueStore(IPStackStream.COMPLETE_IPSTACK_TABLE)
                .map(store -> store.get(ip));
    }

    public Optional<IPStack> getMostRecentStackByClient(String clientId) {
        return getReadOnlyKeyValueStore(IPStackStream.IPSTACK_BY_CLIENT_TABLE)
                .map(store -> store.get(clientId));
    }

    private Optional<ReadOnlyKeyValueStore<String, IPStack>> getReadOnlyKeyValueStore(String tableName) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                kafkaStreams.store(
                        StoreQueryParameters.fromNameAndType(
                                tableName,
                                QueryableStoreTypes.keyValueStore())
                )
        );
    }
}
