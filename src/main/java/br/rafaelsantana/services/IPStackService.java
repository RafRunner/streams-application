package br.rafaelsantana.services;

import br.rafaelsantana.builders.RetrofitBuilder;
import br.rafaelsantana.model.IPStack;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public class IPStackService {

    public interface IPStackClient {
        @GET("/{ip}")
        CompletableFuture<IPStack> getIpInformation(@Path("ip") String ip);
    }

    public static IPStackClient buildClient() {
        return RetrofitBuilder.build().create(IPStackClient.class);
    }
}
