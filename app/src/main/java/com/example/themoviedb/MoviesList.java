package com.example.themoviedb;

import java.util.List;

public class MoviesList {

    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "results=" + results +
                '}';
    }
}
