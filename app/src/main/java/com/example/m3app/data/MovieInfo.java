package com.example.m3app.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable {
    private int id;
    private String name;
    private String moviereleasedate;
    private Bitmap poster;

    public MovieInfo(int id) {
        this.id = id;
    }

    public MovieInfo(int id, String name, String moviereleasedate, Bitmap poster) {
        this.id = id;
        this.name = name;
        this.moviereleasedate = moviereleasedate;
        this.poster = poster;
    }

    protected MovieInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        moviereleasedate = in.readString();
        poster = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMovieReleaseDate() {
        return moviereleasedate;
    }

    public void setMovieReleaseDate(String moviereleasedate) {
        this.moviereleasedate = moviereleasedate;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(moviereleasedate);
        dest.writeValue(poster);
    }
}
