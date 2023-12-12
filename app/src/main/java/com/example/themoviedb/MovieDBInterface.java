package com.example.themoviedb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBInterface {

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/popular")
    Call<MoviesList> getPopularMoviesList(
            @Query("api_key") String apiKey
    );

    @GET("movie/top_rated")
    Call<MoviesList> getTopRatedMoviesList(
            @Query("api_key") String apiKey
    );
}
