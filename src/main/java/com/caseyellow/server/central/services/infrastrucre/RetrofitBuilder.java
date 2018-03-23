package com.caseyellow.server.central.services.infrastrucre;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitBuilder {


    public static Retrofit.Builder Retrofit(String url) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                                          .readTimeout(130, TimeUnit.SECONDS)
                                                          .writeTimeout(130, TimeUnit.SECONDS)
                                                          .connectTimeout(130, TimeUnit.SECONDS)
                                                          .build();

        Retrofit.Builder retrofit = new Retrofit.Builder()
                                                .client(okHttpClient)
                                                .baseUrl(url);

        retrofit.addConverterFactory(GsonConverterFactory.create(buildLenientGson()));

        return retrofit;
    }

    private static Gson buildLenientGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();

        return gsonBuilder.create();
    }

}
