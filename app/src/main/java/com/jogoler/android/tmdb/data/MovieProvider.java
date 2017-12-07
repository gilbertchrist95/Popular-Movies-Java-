package com.jogoler.android.tmdb.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.jogoler.android.tmdb.data.MovieContract.MovieListEntry.TABLE_NAME;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIE, TASK);
        return uriMatcher;
    }

    private static final int TASK = 100;
    private MovieDbHelper mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortBy) {
        Cursor returnCursor;
        final SQLiteDatabase database = mDatabase.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK: {
                returnCursor = database.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null, sortBy
                );
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return MovieContract.MovieListEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case TASK:
                long id = database.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = MovieContract.MovieListEntry.buildMovieUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowDeleted;
        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case TASK:
                rowDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDatabase.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowUpdated;

        switch (match) {
            case TASK:
                rowUpdated = database.update(TABLE_NAME, contentValues
                        , selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);

        }
        if(rowUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
