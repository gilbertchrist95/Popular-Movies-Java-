package com.jogoler.android.tmdb.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class Trailers {
    public List<Trailer> getTrailers() {
        return trailers;
    }

    @SerializedName("results")
    public List<Trailer> trailers = new ArrayList<>();

    public Trailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
