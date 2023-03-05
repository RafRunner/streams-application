import br.rafaelsantana.model.IPStack;

import java.time.Instant;

public class InputHistoryTest {

//    @Test
//    void newInputsShouldAllowForOutput() {
//        var inputHistory = new InputHistory();
//        var ipStack = sampleIPStack1();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//    }
//
//    @Test
//    void duplicateInputsShouldNotAllowForOutput() {
//        var inputHistory = new InputHistory();
//        var ipStack = sampleIPStack1();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//
//        inputHistory.registerProcessedInput(ipStack);
//
//        Assertions.assertFalse(inputHistory.shouldSendOutputMessage(ipStack));
//    }
//
//    @Test
//    void duplicateInputsFromDifferentClientsShouldAllowForOutput() {
//        var inputHistory = new InputHistory();
//        var ipStack = sampleIPStack1();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//
//        inputHistory.registerProcessedInput(ipStack);
//        ipStack = sampleIPStack2();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//    }
//
//    @Test
//    void duplicateInputsForDifferentIPsShouldAllowForOutput() {
//        var inputHistory = new InputHistory();
//        var ipStack = sampleIPStack2();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//
//        inputHistory.registerProcessedInput(ipStack);
//        ipStack = sampleIPStack3();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//    }
//
//    @Test
//    void duplicateInputsShouldAllowForOutputAfter30minutes() {
//        var inputHistory = new InputHistory();
//        var ipStack = sampleIPStack1();
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//
//        inputHistory.registerProcessedInput(ipStack);
//        ipStack.timeStamp -= (1800 + 10);
//
//        Assertions.assertTrue(inputHistory.shouldSendOutputMessage(ipStack));
//    }

    private IPStack sampleIPStack1() {
        return new IPStack("client1",
                Instant.now().getEpochSecond(),
                "127.0.0.1",
                12f,
                -10f,
                "Brazil",
                "Goiás",
                "Goiânia");
    }

    private IPStack sampleIPStack2() {
        return new IPStack("client2",
                Instant.now().getEpochSecond(),
                "127.0.0.1",
                12f,
                -10f,
                "Brazil",
                "Goiás",
                "Goiânia");
    }

    private IPStack sampleIPStack3() {
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
