package com.jogoler.android.tmdb;

import com.jogoler.android.tmdb.pojo.Review;
import com.jogoler.android.tmdb.pojo.Trailer;

/**
 * Created by Gilbert on 19/06/18.
 */

public class DetailMovieContract {

    public interface MovieListener {
        void onWatch(Trailer trailer, int position);

        void onRead(Review review, int positio);
    }
}
