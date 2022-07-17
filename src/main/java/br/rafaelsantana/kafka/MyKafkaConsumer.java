package br.rafaelsantana.kafka;

import br.rafaelsantana.AppConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class MyKafkaConsumer<T> implements Closeable {

    private final KafkaConsumer<String, T> kafkaConsumer;
    private final String topic;
    private Boolean isListening = false;

    public MyKafkaConsumer(String topic, String valueClassName) {
        Properties props = new Properties();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, AppConfig.CLIENT_ID_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, AppConfig.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AppConfig.AUTO_OFFSET_RESET_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        props.put(GsonDeserializer.CONFIG_VALUE_CLASS, valueClassName);

        kafkaConsumer = new KafkaConsumer<String, T>(props);
        this.topic = topic;
    }

    public void subscribe(Consumer<ConsumerRecord<String, T>> consumer) {
        kafkaConsumer.subscribe(List.of(topic));
        isListening = true;

        while (isListening) {
            ConsumerRecords<String, T> records = kafkaConsumer.poll(Duration.ofMillis(AppConfig.DEFAULT_TIMEOUT_KAFKA));
            for (ConsumerRecord<String, T> record : records) {
                consumer.accept(record);
            }
        }
    }

    public void unsubscribe() {
        kafkaConsumer.unsubscribe();
        isListening = false;
    }

    @Override
    public void close() {
        unsubscribe();
        kafkaConsumer.close();
    }
}
