package br.rafaelsantana.builders;

import br.rafaelsantana.AppConfig;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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

    private static File getCacheFile() {
        URL url = RetrofitBuilder.class.getClassLoader().getResource("httpCache/");
        File file;
        try {
            // We're in develop mode
            file = new File(url.toURI());
        } catch (Exception e) {
            // Production mode
            file = new File("./main/httpCache/");
        }
        return file;
    }

    private static OkHttpClient clientWithApiKeyAndHeaders() {
        File httpCacheDirectory = getCacheFile();
        int cacheSize = AppConfig.DEFAULT_CACHE_MAX_SIZE;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        return new OkHttpClient.Builder()
                .connectTimeout(AppConfig.DEFAULT_TIMEOUT_REQUESTS, TimeUnit.MILLISECONDS)
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
                })
                .addNetworkInterceptor(chain -> {
                    Response response = chain.proceed(chain.request());

                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(AppConfig.DEFAULT_CACHE_MAX_AGE, TimeUnit.SECONDS)
                            .build();

                    return response.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", cacheControl.toString())
                            .build();
                })
                .cache(cache)
                .build();
    }
}
