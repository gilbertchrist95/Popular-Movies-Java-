package com.jogoler.android.tmdb.pojo;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Gilbert on 7/3/2017.
 */

public interface MovieService {

    @GET("/3/movie/{sort_by}")
    Call<Movies> getMovie(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Observable<Response<Reviews>> getReviewsMovie(@Path("id") long id, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    Observable<Response<Trailers>> getTrailersMovie(@Path("id") long id, @Query("api_key") String apiKey);

}
