package com.jogoler.android.tmdb;
import com.jogoler.android.tmdb.pojo.MovieService;
import com.jogoler.android.tmdb.pojo.RetrofitClient;

/**
 * Created by Gilbert on 7/3/2017.
 */

public class ApiUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/";

    public static MovieService getMovieService(){
        return RetrofitClient.getClient(BASE_URL).create(MovieService.class);
    }
}
