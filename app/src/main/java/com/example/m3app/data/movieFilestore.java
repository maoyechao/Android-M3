package com.example.m3app.data;

import java.util.Date;

public class movieFilestore {
    private Long movieID;
    private String movieName;
    private Date movieReleaseDate;
    private Date movieWatchDate;

    public movieFilestore() {}

    public Long getMovieID() { return movieID;}

    public String getMovieName() {
        return movieName;
    }

    public Date getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public Date getMovieWatchDate() {
        return movieWatchDate;
    }
}
