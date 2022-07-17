package br.rafaelsantana.builders;

import br.rafaelsantana.AppConfig;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private final static String BASE_URL = "http://api.ipstack.com/";
    private final static String API_KEY = AppConfig.API_KEY;

    private static Retrofit instance;

    public static Retrofit build() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientWithApiKeyAndHeaders())
                    .build();
        }
        return instance;
    }

    private static OkHttpClient clientWithApiKeyAndHeaders() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();

                    HttpUrl newUrl = originalRequest
                            .url()
                            .newBuilder()
                            .addQueryParameter("access_key", API_KEY)
                            .build();

                    Headers newHeaders = originalRequest
                            .headers()
                            .newBuilder()
                            .add("accept", "application/json")
                            .build();

                    Request request = originalRequest
                            .newBuilder()
                            .url(newUrl)
                            .headers(newHeaders)
                            .build();

                    return chain.proceed(request);
                }).build();
    }
}
