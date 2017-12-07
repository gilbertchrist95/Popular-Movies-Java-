package com.jogoler.android.tmdb;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jogoler.android.tmdb.adapter.MovieListAdapter;
import com.jogoler.android.tmdb.fragment.DetailMovieFragment;
import com.jogoler.android.tmdb.pojo.Movie;
import com.jogoler.android.tmdb.task.FetchMoviesTask;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.jogoler.android.tmdb.data.MovieContract.MovieListEntry.CONTENT_URI;
import static com.jogoler.android.tmdb.data.MovieContract.MovieListEntry.MOVIE_COLUMNS;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.Callbacks, LoaderManager.LoaderCallbacks<Cursor>,
        FetchMoviesTask.Listener {

    private static final String STATE_MOVIE = "STATE_MOVIE";
    private static final String STATE_SORT_BY = "STATE_SORT_BY";
    private static final int FAVORITE_MOVIE_LOADER = 0;

    //check twopane
    private boolean mTwoPane;

    private MovieListAdapter movieListAdapter;
    private FragmentRetain fragmentRetain;
    private String sortBy = FetchMoviesTask.MOST_POPULAR;

    RecyclerView mMovieRecyclerView;
//    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.movie_list);


        String TAG = FragmentRetain.class.getName();
        this.fragmentRetain = (FragmentRetain) getSupportFragmentManager().findFragmentByTag(TAG);

        if (this.fragmentRetain == null) {
            this.fragmentRetain = new FragmentRetain();
            getSupportFragmentManager().beginTransaction().add(this.fragmentRetain, TAG).commit();
        }

        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this, getResources()
                .getInteger(R.integer.grid_number_cols)));
        movieListAdapter = new MovieListAdapter(new ArrayList<Movie>(), this);
        mMovieRecyclerView.setAdapter(movieListAdapter);

        mTwoPane = findViewById(R.id.movie_detail_container) != null;

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getString(STATE_SORT_BY);
            if (savedInstanceState.containsKey(STATE_MOVIE)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(STATE_MOVIE);
                movieListAdapter.add(movies);
                findViewById(R.id.loading_indicator_pb).setVisibility(GONE);
                if (sortBy.equals(FetchMoviesTask.FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAVORITE_MOVIE_LOADER, null, this);
                }
                updateStateEmpty();
            }
        } else {
            fetchMovies(sortBy);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = movieListAdapter.getmMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(STATE_MOVIE, movies);
        }
        outState.putString(STATE_SORT_BY, sortBy);

        if (!sortBy.equals(FetchMoviesTask.FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAVORITE_MOVIE_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        switch (sortBy) {
            case FetchMoviesTask.MOST_POPULAR:
                menu.findItem(R.id.sort_by_popular).setCheckable(true);
                break;
            case FetchMoviesTask.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setCheckable(true);
                break;
            case FetchMoviesTask.FAVORITES:
                menu.findItem(R.id.sort_by_favorite).setCheckable(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                if (sortBy.equals(FetchMoviesTask.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIE_LOADER);
                }
                sortBy = FetchMoviesTask.MOST_POPULAR;
                fetchMovies(sortBy);
                item.setCheckable(true);
                break;
            case R.id.sort_by_top_rated:
                if (sortBy.equals(FetchMoviesTask.FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAVORITE_MOVIE_LOADER);
                }
                sortBy = FetchMoviesTask.TOP_RATED;
                fetchMovies(sortBy);
                item.setCheckable(true);
                break;
            case R.id.sort_by_favorite:
                sortBy = FetchMoviesTask.FAVORITES;
                fetchMovies(sortBy);
                item.setCheckable(true);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies(String sortBy) {
        if (!sortBy.equals(FetchMoviesTask.FAVORITES)) {
            findViewById(R.id.loading_indicator_pb).setVisibility(View.VISIBLE);
            FetchMoviesTask.TaskCompleteCommand command =
                    new FetchMoviesTask.TaskCompleteCommand(this.fragmentRetain);
            new FetchMoviesTask(command, sortBy).execute();

        } else {
            getSupportLoaderManager().initLoader(FAVORITE_MOVIE_LOADER, null, this);
        }
    }

    private void updateStateEmpty() {
        if (movieListAdapter.getItemCount() == 0) {
            if (sortBy.equals(FetchMoviesTask.FAVORITES)) {
                findViewById(R.id.empty_movie_container).setVisibility(View.GONE);
                findViewById(R.id.empty_favorite_container).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_movie_container).setVisibility(View.VISIBLE);
                findViewById(R.id.empty_favorite_container).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.empty_movie_container).setVisibility(View.GONE);
            findViewById(R.id.empty_favorite_container).setVisibility(View.GONE);
        }
    }

    @Override
    public void openMovie(Movie movie, int position) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailMovieFragment.STATE_MOVIE, movie);
            DetailMovieFragment detailMovieFragment = new DetailMovieFragment();
            detailMovieFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragmentRetain)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailMovieActivity.class);
            intent.putExtra(DetailMovieFragment.STATE_MOVIE, movie);
            startActivity(intent);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        findViewById(R.id.loading_indicator_pb).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                CONTENT_URI,
                MOVIE_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListAdapter.add(data);
        updateStateEmpty();
        findViewById(R.id.loading_indicator_pb).setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onFetchFinished(Command command) {
        if (command instanceof FetchMoviesTask.TaskCompleteCommand) {
            movieListAdapter.add(((FetchMoviesTask.TaskCompleteCommand) command).getmMovies());
            updateStateEmpty();
            findViewById(R.id.loading_indicator_pb).setVisibility(View.GONE);
        }
    }

    public static class FragmentRetain extends Fragment implements FetchMoviesTask.Listener {

        private boolean paused = false;
        private Command waitingCommand = null;

        public FragmentRetain() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            paused = true;
        }

        @Override
        public void onResume() {
            super.onResume();
            paused = false;
            if (waitingCommand != null) {
                onFetchFinished(waitingCommand);
            }
        }

        @Override
        public void onFetchFinished(Command command) {
            if (getActivity() instanceof FetchMoviesTask.Listener && !paused) {
                FetchMoviesTask.Listener listener = (FetchMoviesTask.Listener) getActivity();
                listener.onFetchFinished(command);
                waitingCommand = null;
            } else {
                waitingCommand = command;
            }
        }
    }
}
