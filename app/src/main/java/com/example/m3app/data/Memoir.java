package com.example.m3app.data;

import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Locale;

public class Memoir {
    private Integer memoirid;
    private String moviename;
    private String moviereleasedate;
    private String watchdatetime;
    private String comments;
    private Double ratingscore;
    private Cinema cinemaid;
    private Person personid;
    private Bitmap pooster;
    private int postcode;

    public Memoir(Integer memoirid) {
        this.memoirid = memoirid;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Memoir(Integer memoirid, String moviename, Date moviereleasedate, Date watchdatetime, String comments, Double ratingscore, Cinema cinemaid, Person personid) {
        this.memoirid = memoirid;
        this.moviename = moviename;
        this.moviereleasedate = localToUTC(moviereleasedate);
        this.watchdatetime = localToUTC(watchdatetime);
        this.comments = comments;
        this.ratingscore = ratingscore;
        this.cinemaid = cinemaid;
        this.personid = personid;
    }

    public Memoir(Integer memoirid, String moviename, String moviereleasedate, String watchdatetime, String comments, Double ratingscore, Cinema cinemaid, Person personid) {
        this.memoirid = memoirid;
        this.moviename = moviename;
        this.moviereleasedate = moviereleasedate;
        this.watchdatetime = watchdatetime;
        this.comments = comments;
        this.ratingscore = ratingscore;
        this.cinemaid = cinemaid;
        this.personid = personid;
    }

    public Integer getMemoirId() {
        return memoirid;
    }

    public void setMemoirId(Integer memoirid) {
        this.memoirid = memoirid;
    }

    public String getMovieName() {
        return moviename;
    }

    public void setMovieName(String moviename) {
        this.moviename = moviename;
    }

    public String getMovieReleaseDate() {
        return moviereleasedate;
    }

    public void setMovieReleaseDate(String moviereleasedate) {
        this.moviereleasedate = moviereleasedate;
    }

    public String getWatchDateTime() {
        return watchdatetime;
    }

    public void setWatchDateTime(String watchdatetime) {
        this.watchdatetime = watchdatetime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Double getRatingScore() {
        return ratingscore;
    }

    public void setRatingScore(Double ratingscore) {
        this.ratingscore = ratingscore;
    }

    public Cinema getCinemaId() {
        return cinemaid;
    }

    public void setCinemaId(Cinema cinemaid) {
        this.cinemaid = cinemaid;
    }

    public Person getPersonId() {
        return personid;
    }

    public void setPersonId(Person personid) {
        this.personid = personid;
    }

    public Bitmap getPooster() {
        return pooster;
    }

    public void setPooster(Bitmap pooster) {
        this.pooster = pooster;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String localToUTC(Date localDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
        return sdf.format(localDate);
    }
}
