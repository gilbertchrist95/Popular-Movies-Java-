package com.jogoler.android.tmdb.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jogoler.android.tmdb.ApiUtils;
import com.jogoler.android.tmdb.BuildConfig;
import com.jogoler.android.tmdb.DetailMovieActivity;
import com.jogoler.android.tmdb.DetailMovieContract;
import com.jogoler.android.tmdb.R;
import com.jogoler.android.tmdb.adapter.DetailMovieAdapter;
import com.jogoler.android.tmdb.data.MovieContract;
import com.jogoler.android.tmdb.pojo.Movie;
import com.jogoler.android.tmdb.pojo.Review;
import com.jogoler.android.tmdb.pojo.Reviews;
import com.jogoler.android.tmdb.pojo.Trailer;
import com.jogoler.android.tmdb.pojo.Trailers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.jogoler.android.tmdb.data.MovieContract.MovieListEntry.CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMovieFragment extends Fragment implements DetailMovieContract.Adapter {


    public static final String STATE_MOVIE = "STATE_MOVIE";

    private Movie movie;
    private DetailMovieAdapter detailMovieAdapter;

    @BindView(R.id.list_item_recycler_view)
    RecyclerView listItemRecyclerView;

    @BindView(R.id.title_movie_text_view)
    TextView titleMovieTextView;
    @BindView(R.id.poster_movie_image_view)
    ImageView posterMovieTextView;
    @BindView(R.id.movie_overview_text_view)
    TextView overViewTextView;
    @BindView(R.id.movie_release_date_text_view)
    TextView releaseDateTextView;
    @BindView(R.id.movie_user_rating_text_view)
    TextView userRatingTextView;
    @BindViews({R.id.first_star, R.id.second_star, R.id.third_star, R.id.fourth_star, R.id.fifth_star})
    List<ImageView> ratingStarView;
    @BindView(R.id.mark_as_favorite_button)
    Button markAsFavoriteButton;
    @BindView(R.id.remove_from_favorites_button)
    Button removeFromFavoriteButton;


    public DetailMovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(STATE_MOVIE)) {
            movie = getArguments().getParcelable(STATE_MOVIE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null && activity instanceof DetailMovieActivity) {
            collapsingToolbarLayout.setTitle(movie.getTitle());
        }

        ImageView backDropImageView = ((ImageView) activity.findViewById(R.id.movie_poster_image_view));
        if (backDropImageView != null) {
            Picasso.with(activity)
                    .load(movie.getBackdrop_path(getContext()))
                    .into(backDropImageView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);
        ButterKnife.bind(this, rootView);

        titleMovieTextView.setText(movie.getTitle());
        releaseDateTextView.setText(movie.getRelease_date(getContext()));
        overViewTextView.setText(movie.getOverview());

        Picasso.with(getContext())
                .load(movie.getPoster_path(getContext()))
                .into(posterMovieTextView);

        upadateRatingBar();
        updateFavoriteButton();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        detailMovieAdapter = new DetailMovieAdapter(getContext(), this);
        listItemRecyclerView.setLayoutManager(layoutManager);
        listItemRecyclerView.setAdapter(detailMovieAdapter);
        fetchTrailersAndReviewsMovies();
        return rootView;
    }


    private void upadateRatingBar() {
        if (movie.getVote_average() != null && !movie.getVote_average().isEmpty()) {
            String rating = "TMDb: " + movie.getVote_average() + "/10";
            userRatingTextView.setText(rating);

            float userRating = Float.valueOf(movie.getVote_average());
            int partRating = (int) userRating / 2;

            for (int i = 0; i < partRating; i++) {
                ratingStarView.get(i).setImageResource(R.mipmap.ic_star_full);
                ratingStarView.get(i).getLayoutParams().width = 90;
                ratingStarView.get(i).requestLayout();
            }

            if (Math.round(userRating) > partRating) {
                ratingStarView.get(partRating).setImageResource(R.mipmap.ic_star_half);
                ratingStarView.get(partRating).getLayoutParams().width = 90;
                ratingStarView.get(partRating).requestLayout();
            }
            userRatingTextView.setVisibility(GONE);
        } else {
            userRatingTextView.setVisibility(GONE);
        }
    }

    private void updateFavoriteButton() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    removeFromFavoriteButton.setVisibility(View.VISIBLE);
                    markAsFavoriteButton.setVisibility(GONE);
                } else {
                    removeFromFavoriteButton.setVisibility(GONE);
                    markAsFavoriteButton.setVisibility(View.VISIBLE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        markAsFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAsFavorite();
            }
        });

        removeFromFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromFavorite();
            }
        });


    }

    private void removeFromFavorite() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(
                            CONTENT_URI, MovieContract.MovieListEntry.COLUMN_ID + " = " + movie.getId(), null
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void markAsFavorite() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (!isFavorite()) {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieListEntry.COLUMN_ID, movie.getId());
                    cv.put(MovieContract.MovieListEntry.COLUMN_TITLE, movie.getTitle());
                    cv.put(MovieContract.MovieListEntry.COLUMN_POSTER_PATH, movie.getPosterpath());
                    cv.put(MovieContract.MovieListEntry.COLUMN_OVERVIEW, movie.getOverview());
                    cv.put(MovieContract.MovieListEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
                    cv.put(MovieContract.MovieListEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
                    cv.put(MovieContract.MovieListEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
                    getContext().getContentResolver().insert(CONTENT_URI, cv);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updateFavoriteButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean isFavorite() {
        Cursor cursorMovie = getContext().getContentResolver().query(
                CONTENT_URI,
                new String[]{MovieContract.MovieListEntry.COLUMN_ID},
                MovieContract.MovieListEntry.COLUMN_ID + " = " + movie.getId(),
                null,
                null);
        if (cursorMovie != null && cursorMovie.moveToFirst()) {
            cursorMovie.close();
            return true;
        } else {
            return false;
        }
    }

    private void fetchTrailersAndReviewsMovies(){
        Observable.zip(getTrailersMovie(), getReviewsMovie(), new BiFunction<Response<Trailers>, Response<Reviews>, List>() {
            @Override
            public List apply(Response<Trailers> trailersResponse, Response<Reviews> reviewsResponse) throws Exception {
                List combineList = new ArrayList();
                List<Trailer> trailerList= trailersResponse.body().getTrailers();
                List<Review> reviewList = reviewsResponse.body().getReviews();
                combineList.add("Videos");
                combineList.addAll(trailerList);
                combineList.add("Reviews");
                combineList.addAll(reviewList);
                return combineList;
            }
        }).subscribe(new Observer<List>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List list) {
                detailMovieAdapter.add(list);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private Observable<Response<Reviews>> getReviewsMovie(){
        return ApiUtils.getMovieService().getReviewsMovie(movie.getId(), BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Response<Trailers>> getTrailersMovie(){
        return ApiUtils.getMovieService().getTrailersMovie(movie.getId(), BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getKey())));
    }

    @Override
    public void read(Review review, int positio) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
    }
}