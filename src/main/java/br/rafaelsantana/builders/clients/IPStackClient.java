package br.rafaelsantana.builders.clients;


import br.rafaelsantana.model.IPStack;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public interface IPStackClient {

    @GET("/{ip}")
    CompletableFuture<IPStack> getIpInformation(@Path("ip") String ip);
}
