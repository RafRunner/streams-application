package br.rafaelsantana.services;

import br.rafaelsantana.model.IPStack;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

@Component
public class IPStackService {

    public interface IPStackClient {
        @GET("/{ip}")
        CompletableFuture<IPStack> getIpInformation(@Path("ip") String ip);
    }

    @Bean
    public static IPStackClient buildClient(Retrofit retrofit) {
        return retrofit.create(IPStackClient.class);
    }
}
