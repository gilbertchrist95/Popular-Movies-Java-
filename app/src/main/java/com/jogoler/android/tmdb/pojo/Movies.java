package com.jogoler.android.tmdb.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class Movies {

    public List<Movie> getMovies() {
        return movies;
    }

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();



}
