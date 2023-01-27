package br.rafaelsantana;

import br.rafaelsantana.builders.RetrofitBuilder;
import br.rafaelsantana.kafka.GsonDeserializer;
import br.rafaelsantana.kafka.GsonIPStackSerdes;
import br.rafaelsantana.kafka.GsonSerializer;
import br.rafaelsantana.kafka.streams.IPStackStream;
import br.rafaelsantana.model.IPStack;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class AppConfig {

    @Bean
    Dotenv env() {
        return Dotenv.load();
    }

    @Bean
    Retrofit retrofit(Constants constants) {
        return RetrofitBuilder.build(constants, "http://api.ipstack.com/");
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig(@NotNull Constants constants) {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, constants.CLIENT_ID_CONFIG);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GsonIPStackSerdes.class.getName());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public ProducerFactory<String, IPStack> producerFactory(@NotNull Constants constants) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, constants.CLIENT_ID_CONFIG);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, IPStack> kafkaTemplate(Constants constants) {
        return new KafkaTemplate<>(producerFactory(constants));
    }

    @Bean
    public ConsumerFactory<String, IPStack> consumerFactory(@NotNull Constants constants) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, constants.CLIENT_ID_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, constants.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, constants.AUTO_OFFSET_RESET_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        props.put(GsonDeserializer.CONFIG_VALUE_CLASS, IPStack.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IPStack> ipStackKafkaListenerContainerFactory(
            ConsumerFactory<String, IPStack> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, IPStack> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    // Declaring and creating Kafka Topics
    @Bean
    NewTopic inputTopic(@NotNull Constants constants) {
        return TopicBuilder.name(constants.INPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic outputTopic(@NotNull Constants constants) {
        return TopicBuilder.name(constants.OUTPUT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic completeIPStackTableTopic() {
        return TopicBuilder.name(IPStackStream.COMPLETE_IPSTACK_TABLE)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic ipStackByClientIdTableTopic() {
        return TopicBuilder.name(IPStackStream.IPSTACK_BY_CLIENT_TABLE)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
