package com.jogoler.android.tmdb.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Gilbert on 8/20/2017.
 */

public class Review implements Parcelable, Comparable<Review>{

    /**
     * id : 55a58e46c3a3682bb2000065
     * author : Andres Gomez
     * content : The minions are a nice idea and the animation and London recreation is really good, but that's about it.

     The script is boring and the jokes not really funny.
     * url : https://www.themoviedb.org/review/55a58e46c3a3682bb2000065
     */

    private String id;
    private String author;
    private String content;
    private String url;

    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    @Override
    public int compareTo(@NonNull Review review) {
        return 0;
    }
}
