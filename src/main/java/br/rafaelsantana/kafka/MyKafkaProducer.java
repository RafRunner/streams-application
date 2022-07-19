package br.rafaelsantana.kafka;

import br.rafaelsantana.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class MyKafkaProducer<T> implements Closeable {

    static final Logger logger = Logger.getLogger(MyKafkaProducer.class.getName());

    private final KafkaProducer<String, T> producer;

    public MyKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "streams-application");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());

       producer = new KafkaProducer<>(props);
    }

    public RecordMetadata sendRecord(ProducerRecord<String, T> record) {
        try {
            return producer.send(record).get(AppConfig.DEFAULT_TIMEOUT_KAFKA, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.severe("Error while sending record: " + e);
            return null;
        }
    }

    @Override
    public void close() {
        producer.close();
    }
}
