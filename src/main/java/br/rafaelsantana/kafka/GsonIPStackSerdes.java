package br.rafaelsantana.kafka;

import br.rafaelsantana.model.IPStack;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Map;

public class GsonIPStackSerdes extends Serdes.WrapperSerde<IPStack> {

    public GsonIPStackSerdes() {
        super(new GsonSerializer<>(), new GsonDeserializer<>());
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        setClassNameConfig(configs);
        super.configure(configs, isKey);
    }

    private <T> void setClassNameConfig(Map<String, T> configs) {
        configs.put(GsonDeserializer.CONFIG_VALUE_CLASS, (T) IPStack.class.getName());
    }
}
