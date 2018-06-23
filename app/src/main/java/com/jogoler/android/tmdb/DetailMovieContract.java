package com.jogoler.android.tmdb;

import com.jogoler.android.tmdb.pojo.Review;
import com.jogoler.android.tmdb.pojo.Trailer;

/**
 * Created by Gilbert on 19/06/18.
 */

public class DetailMovieContract {

    public interface Adapter {
        void watch(Trailer trailer, int position);

        void read(Review review, int positio);
    }
}
