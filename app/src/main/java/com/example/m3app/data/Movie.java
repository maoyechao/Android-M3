package com.example.m3app.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Movie {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "moviename")
    public String movieName;
    @ColumnInfo(name = "moviereleasedate")
    public Date releaseDate;
    @ColumnInfo(name = "watchdatetime")
    public Date watchDate;

    public Movie(int uid, String movieName, Date releaseDate, Date watchDate) {
        this.uid = uid;
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        this.watchDate = watchDate;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(Date watchDate) {
        this.watchDate = watchDate;
    }

}
