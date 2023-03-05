package br.rafaelsantana.builders.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

@Component
public class IPStackClientBuilder {

    private static IPStackClient clientInstance;

    @Bean
    public static IPStackClient buildClient(Retrofit retrofit) {
        if (clientInstance == null) {
            clientInstance = retrofit.create(IPStackClient.class);
        }
        return clientInstance;
    }
}
