package com.jogoler.android.tmdb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_MOVIE_TABLE = "CREATE TABLE "+ MovieContract.MovieListEntry.TABLE_NAME+" ("+
                MovieContract.MovieListEntry._ID + " INTEGER PRIMARY KEY, "+
                MovieContract.MovieListEntry.COLUMN_ID + " INTEGER NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_TITLE+ " TEXT NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_POSTER_PATH+ " TEXT NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_RELEASE_DATE+ " TEXT NOT NULL, "+
                MovieContract.MovieListEntry.COLUMN_BACKDROP_PATH+ " TEXT NOT NULL "+
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
