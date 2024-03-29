package br.rafaelsantana;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    @Autowired
    public Constants(
        Dotenv env,
        @Value(value = "${kafka.bootstrap-servers-config}") String bootstrapServersConfig
    ) {
        this.API_KEY = env.get("API_KEY");
        this.BOOTSTRAP_SERVERS_CONFIG = env.get("BOOTSTRAP_SERVERS_CONFIG", bootstrapServersConfig);
    }

    // Sensitive data that comes from a .env file
    public final String API_KEY;

    // Api Configs
    @Value(value = "${api.default-timeout-requests}")
    public Long DEFAULT_TIMEOUT_REQUESTS;
    @Value(value = "${api.default-cache-max-size}")
    public Integer DEFAULT_CACHE_MAX_SIZE;
    @Value(value = "${api.default-cache-max-age}")
    public Integer DEFAULT_CACHE_MAX_AGE;

    // Kafka configs
    @Value(value = "${kafka.client-id-config}")
    public String CLIENT_ID_CONFIG;
    @Value(value = "${kafka.group-id-config}")
    public String GROUP_ID_CONFIG;
    @Value(value = "${kafka.default-timeout-kafka}")
    public Integer DEFAULT_TIMEOUT_KAFKA;
    @Value(value = "${kafka.auto-offset-reset-config}")
    public String AUTO_OFFSET_RESET_CONFIG;

    public final String BOOTSTRAP_SERVERS_CONFIG;
    @Value(value = "${kafka.input-topic}")
    public String INPUT_TOPIC;
    @Value(value = "${kafka.output-topic}")
    public String OUTPUT_TOPIC;
}
