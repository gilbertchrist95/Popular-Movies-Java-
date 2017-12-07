package com.jogoler.android.tmdb.task;

import android.os.AsyncTask;

import com.jogoler.android.tmdb.ApiUtils;
import com.jogoler.android.tmdb.BuildConfig;
import com.jogoler.android.tmdb.pojo.MovieService;
import com.jogoler.android.tmdb.pojo.Review;
import com.jogoler.android.tmdb.pojo.Reviews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Gilbert on 8/20/2017.
 */

public class FetchReviewTask extends AsyncTask<Long,Void,List<Review>> {

    private Listener listener;

    public interface Listener{
        void onReviewFetchFinished(List<Review> reviews);
    }

    public FetchReviewTask(Listener listener) {
        this.listener = listener;
    }


    @Override
    protected List<Review> doInBackground(Long... longs) {
        long movieId = longs[0];
        MovieService movieService = ApiUtils.getMovieService();
        Call<Reviews> reviewsCall = movieService.getReviewsMovie(movieId, BuildConfig.THE_MOVIE_DATABASE_API_KEY);

        try {
            Response<Reviews> response = reviewsCall.execute();
            Reviews reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if(reviews!=null){
            listener.onReviewFetchFinished(reviews);
        }else{
            listener.onReviewFetchFinished(new ArrayList<Review>());
        }
    }
}
