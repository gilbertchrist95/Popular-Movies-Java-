package com.jogoler.android.tmdb.task;

import android.os.AsyncTask;
import android.support.annotation.StringDef;

import com.jogoler.android.tmdb.ApiUtils;
import com.jogoler.android.tmdb.BuildConfig;
import com.jogoler.android.tmdb.Command;
import com.jogoler.android.tmdb.pojo.Movie;
import com.jogoler.android.tmdb.pojo.MovieService;
import com.jogoler.android.tmdb.pojo.Movies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class FetchMoviesTask extends AsyncTask<Void,Void,List<Movie>> {

    public static final String MOST_POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVORITES = "favorites";

    @StringDef({MOST_POPULAR,TOP_RATED,FAVORITES})
    public @interface SORT_BY{
    }

    private final TaskCompleteCommand mCommand;
    @SORT_BY
    String mSortBy = MOST_POPULAR;

    public FetchMoviesTask(TaskCompleteCommand mCommand, String mSortBy) {
        this.mCommand = mCommand;
        this.mSortBy = mSortBy;
    }

     public interface Listener{
        void onFetchFinished(Command command);
    }


    @Override
    protected List<Movie> doInBackground(Void... voids) {
        MovieService movieService = ApiUtils.getMovieService();
        Call<Movies> moviesCall = movieService.getMovie(mSortBy, BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<Movies> response = moviesCall.execute();
            Movies movies = response.body();
            return movies.getMovies();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if(movies!=null){
            mCommand.mMovies=movies;
        }else{
            mCommand.mMovies=new ArrayList<>();
        }
        mCommand.execute();
    }

    public static class TaskCompleteCommand implements Command {

        private FetchMoviesTask.Listener listener;
        private List<Movie> mMovies;

        public TaskCompleteCommand(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void execute() {
            listener.onFetchFinished(this);
        }

        public List<Movie> getmMovies() {
            return mMovies;
        }
    }
}
