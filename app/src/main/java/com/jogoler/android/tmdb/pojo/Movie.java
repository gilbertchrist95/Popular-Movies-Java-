package com.jogoler.android.tmdb.pojo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jogoler.android.tmdb.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class Movie implements Parcelable{

    public static final String TAG = Movie.class.getName();

    private long id;
    private String vote_average;
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;
    private String backdrop_path;

    protected Movie(Parcel in) {
        id = in.readLong();
        vote_average = in.readString();
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        backdrop_path = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path(Context context) {
        if (poster_path != null && !poster_path.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_poster) + poster_path;
        }
        return null;
    }

    public String getPosterpath(){
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date(Context context) {
        String inputPatter = "yyyy-dd-MM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputPatter, Locale.US);
        if(release_date!=null && !release_date.isEmpty()){
            try {
                Date date = simpleDateFormat.parse(release_date);
                return DateFormat.getDateInstance().format(date);
            } catch (ParseException e) {
                Log.e(TAG,"error to parse: "+release_date);
            }
        }else{
            release_date = "Release date is null";
        }
        return release_date;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getBackdrop_path(Context context) {
        if (backdrop_path != null && !backdrop_path.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_backdrop) +
                    backdrop_path;
        }
        return null;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }



    public Movie(long id, String vote_average, String title, String poster_path, String overview, String release_date, String backdrop_path) {
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.backdrop_path = backdrop_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(vote_average);
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(backdrop_path);
    }
}
