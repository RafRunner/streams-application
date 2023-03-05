package br.rafaelsantana.cache;

import br.rafaelsantana.Constants;
import br.rafaelsantana.model.IPStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// TODO implement history on Redis so multiple instances can access it and it is persisted
@Component
public class OutputDecider {

    private final Constants constants;

    @Autowired
    public OutputDecider(Constants constants) {
        this.constants = constants;
    }

    Map<String, Map<String, IPStack>> historyPerClient = new HashMap<>();

    public void registerProcessedInput(IPStack ipStack) {
        getClientHistory(ipStack.clientId).put(ipStack.ip, ipStack);
    }

    public boolean shouldSendOutputMessage(IPStack ipStack) {
        IPStack lastRecord = getClientHistory(ipStack.clientId).get(ipStack.ip);
        return lastRecord == null ||
                Instant.now().getEpochSecond() - lastRecord.timeStamp > constants.DEFAULT_CACHE_MAX_AGE;
    }

    private Map<String, IPStack> getClientHistory(String clientId) {
        if (!historyPerClient.containsKey(clientId)) {
            historyPerClient.put(clientId, new HashMap<>());
        }
        return historyPerClient.get(clientId);
    }
}
