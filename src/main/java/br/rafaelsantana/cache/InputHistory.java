package br.rafaelsantana.cache;

import br.rafaelsantana.AppConfig;
import br.rafaelsantana.model.IPStack;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InputHistory {

    Map<String, Map<String, IPStack>> historyPerClient = new HashMap<>();

    public void registerProcessedInput(IPStack ipStack) {
        getClientHistory(ipStack.clientId).put(ipStack.ip, ipStack);
    }

    public boolean shouldSendOutputMessage(IPStack ipStack) {
        IPStack lastRecord = getClientHistory(ipStack.clientId).get(ipStack.ip);
        return lastRecord == null ||
                Instant.now().getEpochSecond() - lastRecord.timeStamp > AppConfig.DEFAULT_CACHE_MAX_AGE;
    }

    private Map<String, IPStack> getClientHistory(String clientId) {
        if (!historyPerClient.containsKey(clientId)) {
            historyPerClient.put(clientId, new HashMap<>());
        }
        return historyPerClient.get(clientId);
    }
}
