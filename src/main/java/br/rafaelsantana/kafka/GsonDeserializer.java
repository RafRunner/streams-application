package br.rafaelsantana.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.logging.Logger;

public class GsonDeserializer<T> implements Deserializer<T> {

    static final Logger logger = Logger.getLogger(GsonDeserializer.class.getName());

    public static final String CONFIG_VALUE_CLASS = "value.deserializer.class";
    public static final String CONFIG_KEY_CLASS = "key.deserializer.class";
    private Class<T> cls;

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String configKey = isKey ? CONFIG_KEY_CLASS : CONFIG_VALUE_CLASS;
        String clsName = String.valueOf(configs.get(configKey));

        try {
            cls = (Class<T>) Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            logger.severe(("Failed to configure GsonDeserializer." +
                    "Did you forget to specify the '%s' property ?").formatted(configKey));
        }
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        try {
            return gson.fromJson(new String(bytes), cls);
        } catch (JsonSyntaxException e) {
            logger.warning("Malformed record value: %s\nError: %s".formatted(new String(bytes), e));
            return null;
        }
    }
}
