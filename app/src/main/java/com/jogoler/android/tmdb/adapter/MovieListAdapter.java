package com.jogoler.android.tmdb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jogoler.android.tmdb.R;
import com.jogoler.android.tmdb.data.MovieContract;
import com.jogoler.android.tmdb.pojo.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {

    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void openMovie(Movie movie, int position);
    }

    public MovieListAdapter(ArrayList<Movie> mMovies, Callbacks mCallbacks) {
        this.mMovies = mMovies;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content,parent,false);
        Context context = view.getContext();

        int gridColsNumber = context.getResources()
                .getInteger(R.integer.grid_number_cols);

        view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                1.5f);

        return new MovieListViewHolder(view);
    }

    @Override
    public void onViewRecycled(MovieListViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp();
    }

    @Override
    public void onBindViewHolder(final MovieListViewHolder holder, final int position) {
        final Movie movie = mMovies.get(position);
        Context context = holder.mView.getContext();
        holder.mMovie = movie;
        holder.titleTextView.setText(movie.getTitle());
        String urlPoster = movie.getPoster_path(context);
        if (urlPoster == null) {
            holder.titleTextView.setVisibility(View.VISIBLE);
        }
        Picasso.with(context)
                .load(urlPoster)
                .into(holder.thumbnailImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(holder.mMovie.getId()!=movie.getId()){
                            holder.cleanUp();
                        }else{
                            holder.thumbnailImageView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError() {
                        holder.titleTextView.setVisibility(View.VISIBLE);
                    }
                });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.openMovie(movie,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public class MovieListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.thumbnail_image_view)
        ImageView thumbnailImageView;
        @BindView(R.id.title_text_view)
        TextView titleTextView;
        public Movie mMovie;

        public MovieListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }

        public void cleanUp() {
            final Context context = mView.getContext();
            Picasso.with(context).cancelRequest(thumbnailImageView);
            thumbnailImageView.setImageBitmap(null);
            titleTextView.setVisibility(View.GONE);
        }
    }

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieListEntry.COL_ID);
                String title = cursor.getString(MovieContract.MovieListEntry.COL_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieListEntry.COL_POSTER_PATH);
                String overview = cursor.getString(MovieContract.MovieListEntry.COL_OVERVIEW);
                String voteAverage = cursor.getString(MovieContract.MovieListEntry.COLE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MovieContract.MovieListEntry.COL_RELEASE_DATE);
                String backdropPath = cursor.getString(MovieContract.MovieListEntry.COL_BACKDROP_PATH);
                Movie movie = new Movie(id, voteAverage, title, posterPath, overview,releaseDate,backdropPath );
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getmMovies() {
        return mMovies;
    }
}
