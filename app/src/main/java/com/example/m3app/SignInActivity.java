package com.example.m3app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class SignInActivity extends AppCompatActivity {

    private SignInViewModel signInViewModel;
    private EditText userName;
    private EditText password;
    private ProgressBar loadingBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        signInViewModel.initContext(this);
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loadingBar = findViewById(R.id.loading);
        Button btnSignin = (findViewById(R.id.signin));
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().trim().contains("@") && userName.getText().toString().trim().contains(".")) {
                    if (password.getText().toString().trim().length() >= 6) {
                        loadingBar.setVisibility(View.VISIBLE);
                        signInViewModel.singInProcess(userName.getText().toString().trim(), password.getText().toString().trim());
                        signInViewModel.getCinemaProcess();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "The password should longer than 6 char.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Use a email structure.",
                            Toast.LENGTH_LONG).show();
                }
//                signInViewModel.singInProcess(userName.getText().toString().trim(), password.getText().toString().trim());
//                signInViewModel.getCinemaProcess();
            } });
        signInViewModel.getSignInResultLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                if (i == 0) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else if (i == 1) {
                    loadingBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "Sign in fail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnSignUp = (findViewById(R.id.bt_sign_up));
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,
                        SignUpActivity.class);
                startActivity(intent);
            } });
    }
}
