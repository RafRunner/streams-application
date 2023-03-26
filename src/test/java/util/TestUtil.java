package util;

import br.rafaelsantana.Constants;
import br.rafaelsantana.model.IPStack;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestUtil {

    public static Constants testConstants() {
        var constants = new Constants(Dotenv.configure().load(), "localhost:9092");
        constants.OUTPUT_TOPIC = "output-topic";
        constants.INPUT_TOPIC = "input-topic";
        constants.CLIENT_ID_CONFIG = "streams-application-test";

        return constants;
    }

    public static Map<IPStack, IPStack> getIncompleteToCompleteIpStacks() {
        var map = new LinkedHashMap<IPStack, IPStack>();
        map.put(makeIncomplete(sampleIPStack1()), sampleIPStack1());
        map.put(makeIncomplete(sampleIPStack2()), sampleIPStack2());
        map.put(makeIncomplete(sampleIPStack3()), sampleIPStack3());

        return map;
    }

    private static IPStack makeIncomplete(IPStack ipStack) {
        return new IPStack(ipStack.clientId, ipStack.timeStamp, ipStack.ip);
    }

    private static IPStack sampleIPStack1() {
        return new IPStack("client1",
                Instant.now().getEpochSecond(),
                "127.0.0.1",
                12f,
                -10f,
                "Brazil",
                "Goiás",
                "Goiânia");
    }

    private static IPStack sampleIPStack2() {
        return new IPStack("client2",
                Instant.now().getEpochSecond(),
                "127.0.0.1",
                12f,
                -10f,
                "Brazil",
                "Goiás",
                "Goiânia");
    }

    private static IPStack sampleIPStack3() {
        return new IPStack("client1",
                Instant.now().getEpochSecond(),
                "208.80.154.224",
                38.98372f,
                -77.38276f,
                "United States",
                "Virginia",
                "Herndon");
    }
}
