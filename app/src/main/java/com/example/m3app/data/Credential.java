package com.example.m3app.data;

import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.Locale;

public class Credential {

    private Integer credentialid;
    private String username;
    private String passwordhash;
    private String signupdate;

    public Credential(Integer credentialId) {
        this.credentialid = credentialId;
    }

    public Credential(Integer credentialId, String username, String passwordHash, String signUpDate) {
        this.credentialid = credentialId;
        this.username = username;
        this.passwordhash = passwordHash;
        this.signupdate = signUpDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Credential(Integer credentialId, String username, String passwordHash, Date signUpDate) {
        this.credentialid = credentialId;
        this.username = username;
        this.passwordhash = passwordHash;
        this.signupdate = localToUTC(signUpDate);
    }

    public Integer getCredentialId() {
        return credentialid;
    }

    public void setCredentialId(Integer credentialId) {
        this.credentialid = credentialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordhash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordhash = passwordHash;
    }

    public String getSignUpDate() {
        return signupdate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signupdate = signUpDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String localToUTC(Date localDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
        return sdf.format(localDate);
    }
}
