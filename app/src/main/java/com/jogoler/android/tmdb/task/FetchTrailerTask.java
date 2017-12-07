package com.jogoler.android.tmdb.task;

import android.os.AsyncTask;

import com.jogoler.android.tmdb.ApiUtils;
import com.jogoler.android.tmdb.BuildConfig;
import com.jogoler.android.tmdb.pojo.MovieService;
import com.jogoler.android.tmdb.pojo.Trailer;
import com.jogoler.android.tmdb.pojo.Trailers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class FetchTrailerTask extends AsyncTask<Long,Void,List<Trailer>> {


    public Listener listener;

    public interface Listener{
        void onTrailerFetchFinished(List<Trailer> trailers);
    }

    public FetchTrailerTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Long... longs) {
        long movieId = longs[0];
        MovieService movieService = ApiUtils.getMovieService();
        Call<Trailers> trailersCall = movieService.getTrailersMovie(movieId, BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<Trailers> response = trailersCall.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if(trailers!=null){
            listener.onTrailerFetchFinished(trailers);
        }else{
            listener.onTrailerFetchFinished(new ArrayList<Trailer>());
        }
    }
}
