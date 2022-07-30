package br.rafaelsantana.kafka.streams;

import br.rafaelsantana.AppConfig;
import br.rafaelsantana.kafka.GsonIPStackSerdes;
import br.rafaelsantana.model.IPStack;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;

import java.io.Closeable;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;

public class IPStackStream implements Closeable {

    static final Logger logger = Logger.getLogger(IPStackStream.class.getName());

    private final KafkaStreams streams;

    public IPStackStream(final String inputTopic, final String outputTopic, Function<IPStack, IPStack> action) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfig.CLIENT_ID_CONFIG);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GsonIPStackSerdes.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, IPStack> source = builder.stream(inputTopic);

        KStream<String, IPStack> processedStream = source
                .mapValues(action::apply)
                .filter(((key, ipStack) -> ipStack != null));

        processedStream.to(outputTopic);

        Topology topology = builder.build();
        logger.info("IPStack Topology created:\n" + topology.describe().toString());
        streams = new KafkaStreams(topology, props);
    }

    public KafkaStreams getStreams() {
        return streams;
    }

    @Override
    public void close() {
        streams.close();
    }
}
