package com.example.m3app.data;

import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;


public class Person {
    private Integer personid;
    private String firstname;
    private String surname;
    private String gender;
    private String dob;
    private String address;
    private String state;
    private Integer postcode;
    private Credential credentialid;

    public Person(Integer personId) {
        this.personid = personId;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Person(Integer personId, String firstName, String surname, String gender, Date dob, String address, String personState, Integer postcode, Credential credential) {
        this.personid = personId;
        this.firstname = firstName;
        this.surname = surname;
        this.gender = gender;
        this.dob = localToUTC(dob);
        this.address = address;
        this.state = personState;
        this.postcode = postcode;
        this.credentialid = credential;
    }

    public Person(Integer personId, String firstName, String surname, String gender, String dob, String address, String personState, Integer postcode, Credential credential) {
        this.personid = personId;
        this.firstname = firstName;
        this.surname = surname;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.state = personState;
        this.postcode = postcode;
        this.credentialid = credential;
    }

    public Integer getPersonId() {
        return personid;
    }

    public void setPersonId(Integer personId) {
        this.personid = personId;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPersonState() {
        return state;
    }

    public void setPersonState(String personState) {
        this.state = personState;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    public Credential getCredentialId() {
        return credentialid;
    }

    public void setCredentialId(Credential credentialId) {
        this.credentialid = credentialId;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String localToUTC(Date localDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
        return sdf.format(localDate);
    }

}
