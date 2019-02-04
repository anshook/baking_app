package com.udacity.ak.bakingapp.utilities;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    private static Retrofit retrofit = null;
    private static retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory rxAdapter = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(rxAdapter)
                    .build();
        }
        return retrofit;
    }

    public static RestInterface getRestService()
    {
        return getRetrofit().create(RestInterface.class);
    }
}
