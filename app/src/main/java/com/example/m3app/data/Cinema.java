package com.example.m3app.data;

public class Cinema {
    private Integer cinemaid;
    private String cinemaname;
    private Integer postcode;

    public Cinema (Integer cinemaid) {
        this.cinemaid = cinemaid;
    }

    public Cinema(Integer cinemaid, String cinemaname, Integer postcode) {
        this.cinemaid = cinemaid;
        this.cinemaname = cinemaname;
        this.postcode = postcode;
    }

    public Integer getCinemaId() {
        return cinemaid;
    }

    public void setCinemaId(Integer cinemaid) {
        this.cinemaid = cinemaid;
    }

    public String getCinemaName() {
        return cinemaname;
    }

    public void setCinemaName(String cinemaname) {
        this.cinemaname = cinemaname;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

}
