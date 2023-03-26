package br.rafaelsantana.cache;

import br.rafaelsantana.Constants;
import br.rafaelsantana.model.IPStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// TODO implement history on Redis or Kafka itself so multiple instances can access it and it is persisted
@Component
public class OutputDecider {

    private final Constants constants;

    private final Map<String, Map<String, IPStack>> historyPerClient = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public OutputDecider(Constants constants) {
        this.constants = constants;
    }

    public void registerProcessedInput(IPStack ipStack) {
        getClientHistory(ipStack.clientId).put(ipStack.ip, ipStack);

        executor.schedule(() -> {
            getClientHistory(ipStack.clientId).remove(ipStack.ip);
        }, constants.DEFAULT_CACHE_MAX_AGE, TimeUnit.SECONDS);
    }

    public boolean shouldSendOutputMessage(IPStack ipStack) {
        IPStack lastRecord = getClientHistory(ipStack.clientId).get(ipStack.ip);
        return lastRecord == null;
    }

    private Map<String, IPStack> getClientHistory(String clientId) {
        if (!historyPerClient.containsKey(clientId)) {
            historyPerClient.put(clientId, new ConcurrentHashMap<>());
        }
        return historyPerClient.get(clientId);
    }
}
