import br.rafaelsantana.Constants;
import br.rafaelsantana.builders.clients.IPStackClient;
import br.rafaelsantana.cache.OutputDecider;
import br.rafaelsantana.kafka.GsonDeserializer;
import br.rafaelsantana.kafka.GsonIPStackSerdes;
import br.rafaelsantana.kafka.GsonSerializer;
import br.rafaelsantana.kafka.streams.IPStackStream;
import br.rafaelsantana.model.IPStack;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import util.TestUtil;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class IPStackStreamTest {

    @Mock
    IPStackClient ipStackClient;

    private Constants constants = TestUtil.testConstants();
    private IPStackStream ipStackStream;
    private Topology topology;
    private Properties props;
    private GsonDeserializer<IPStack> gsonDeserializer;

    @BeforeEach
    void setup() {
        var outputDecider = new OutputDecider(constants);
        var builder = new StreamsBuilder();

        ipStackClient = Mockito.mock(IPStackClient.class);

        ipStackStream = new IPStackStream(outputDecider, ipStackClient);
        ipStackStream.buildPipeline(builder, constants);
        topology = builder.build();

        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, constants.CLIENT_ID_CONFIG);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, constants.BOOTSTRAP_SERVERS_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GsonIPStackSerdes.class.getName());

        gsonDeserializer = new GsonDeserializer<>();
        gsonDeserializer.configure(Map.of(GsonDeserializer.CONFIG_VALUE_CLASS, IPStack.class.getName()), false);
    }

    @Test
    void ipStacksHaveTheirInformationCompletedByStream() {
        var incompleteToCompleteStacks = TestUtil.getIncompleteToCompleteIpStacks();

        for (var entry : incompleteToCompleteStacks.entrySet()) {
            Mockito.when(ipStackClient.getIpInformation(entry.getKey().ip))
                    .thenReturn(CompletableFuture.completedFuture(entry.getValue()));
        }

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, props)) {
            TestInputTopic<String, IPStack> inputTopic = topologyTestDriver
                    .createInputTopic(constants.INPUT_TOPIC, new StringSerializer(), new GsonSerializer<>());

            TestOutputTopic<String, IPStack> outputTopic = topologyTestDriver
                    .createOutputTopic(constants.OUTPUT_TOPIC, new StringDeserializer(), gsonDeserializer);

            for (var entry : incompleteToCompleteStacks.entrySet()) {
                inputTopic.pipeInput(entry.getKey());
            }

            Assertions.assertArrayEquals(
                    incompleteToCompleteStacks.values().toArray(), outputTopic.readValuesToList().toArray(new IPStack[0])
            );
            var invocations = Mockito.mockingDetails(ipStackClient).getInvocations();
            Assertions.assertEquals(incompleteToCompleteStacks.size(), invocations.size());
        }
    }
}
