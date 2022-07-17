package br.rafaelsantana;

import br.rafaelsantana.builders.DotenvBuilder;
import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    private final static Dotenv ENV = DotenvBuilder.build();


    // API configs
    public static final String API_KEY = ENV.get("API_KEY");
    public static final Integer DEFAULT_TIMEOUT_REQUESTS = Integer.parseInt(ENV.get("DEFAULT_TIMEOUT_REQUESTS", "15000"));
    // 10 MiB
    public static final Integer DEFAULT_CACHE_MAX_SIZE = Integer.parseInt(ENV.get("DEFAULT_CACHE_MAX_SIZE", "10485760"));
    public static final Integer DEFAULT_CACHE_MAX_AGE = Integer.parseInt(ENV.get("DEFAULT_CACHE_MAX_AGE", "1800"));

    // Kafka configs
    public static final String CLIENT_ID_CONFIG = ENV.get("CLIENT_ID_CONFIG", "streams-application");
    public static final String GROUP_ID_CONFIG = ENV.get("GROUP_ID_CONFIG", "streams-application.group");
    public static final Integer DEFAULT_TIMEOUT_KAFKA = Integer.parseInt(ENV.get("DEFAULT_TIMEOUT_KAFKA", "15000"));
    public static final String AUTO_OFFSET_RESET_CONFIG = ENV.get("AUTO_OFFSET_RESET_CONFIG", "earliest");
    public static final String BOOTSTRAP_SERVERS_CONFIG = ENV.get("BOOTSTRAP_SERVERS_CONFIG", "localhost:9092");
    public static final String INPUT_TOPIC = ENV.get("INPUT_TOPIC", "ipstack.input");
    public static final String OUTPUT_TOPIC = ENV.get("OUTPUT_TOPIC", "ipstack.output");
}
