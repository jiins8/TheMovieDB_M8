package com.example.themoviedb;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "40393ca4b61602f8f494b457dbb75fdc";
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0MDM5M2NhNGI2MTYwMmY4ZjQ5NGI0NTdkYmI3NWZkYyIsInN1YiI6IjY1NGUzZmI3NDFhNTYxMzM2YTI1N2RmMCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.elZY2Vwx7IiwjFDj6zRyAPvbi3WLl9I64N3Un7od2ro";
    private static MovieDBInterface apiInterface;

    public static MovieDBInterface getClient() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiInterface = retrofit.create(MovieDBInterface.class);
        }
        return apiInterface;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}