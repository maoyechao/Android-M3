package com.example.m3app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.m3app.data.Credential;
import com.example.m3app.data.Person;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel signUpViewModel;
    private EditText first_name;
    private EditText surname;
    private RadioGroup radioGroupGender;
    private DatePicker DOB;
    private EditText address;
    private Spinner state;
    private EditText postcode;
    private EditText email;
    private EditText password;
    private Button signUp;
    private ProgressBar progressBar;
    private String gender = "M";
    private String dob;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        signUpViewModel.getEmailProcess();
        signUpViewModel.initContext(this);
        first_name = (findViewById(R.id.first_name));
        surname = (findViewById(R.id.surname));
        radioGroupGender = (findViewById(R.id.radio_button_gender));
        DOB = (findViewById(R.id.datepickerdob));
        address = (findViewById(R.id.address));
        state = (findViewById(R.id.spinner_state));
        postcode = (findViewById(R.id.postcode));
        email = (findViewById(R.id.email));
        password = (findViewById(R.id.password));
        progressBar = (findViewById(R.id.progressbar));
        signUp = (findViewById(R.id.signup));
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_male:
                        gender = "M";
                        break;
                    case R.id.radio_female:
                        gender = "F";
                        break;
                }
            }
        });
        DOB.init(1990, 4, 1, new DatePicker.OnDateChangedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
                dob = simpleDateFormat.format(calendar);
                Toast.makeText(getApplicationContext(),
                        "Date: " + dob,
                        Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                SharedPreference sp = SharedPreference.getInstance(context);
                int j = 0;
                for (int i = 0; i < sp.getInt("emailTotalCount"); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject(sp.getString("email"+i));
                        if (jsonObject.getString("username").equals(email.getText().toString().trim())) {
                            Toast.makeText(getApplicationContext(),
                                    "Email exist! Please enter the email again",
                                    Toast.LENGTH_SHORT).show();
                            j = 1;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (j == 0 && initPerson() != null) {
                        if (TextUtils.isEmpty(first_name.getText())) {
                            Toast.makeText(getApplicationContext(),
                                    "First Name is required",
                                    Toast.LENGTH_SHORT).show();
                            first_name.setError("First Name is required");
                        } else if (TextUtils.isEmpty(surname.getText())) {
                            Toast.makeText(getApplicationContext(),
                                    "SurName is required",
                                    Toast.LENGTH_SHORT).show();
                            surname.setError("SurName is required");
                        } else if (TextUtils.isEmpty(address.getText())) {
                            Toast.makeText(getApplicationContext(),
                                    "Address is required",
                                    Toast.LENGTH_SHORT).show();
                            address.setError("Address is required");
                        } else {
                            signUpViewModel.signUpProcessing(initPerson());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        signUpViewModel.getAddPersonResult().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setVisibility(View.GONE);
                if (integer == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Sign up succeed",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                } else if (integer == 1) {
                    Toast.makeText(getApplicationContext(),
                            "Sign up failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Person initPerson() throws JSONException {
        Person person = new Person(0);
        SharedPreference sp = SharedPreference.getInstance(context);
        int id= sp.getInt("emailTotalCount") + 1;
        if (postcode.getText().toString().trim().length() == 4) {
            if (email.getText().toString().trim().contains("@") && email.getText().toString().trim().contains(".")) {
                if (password.getText().toString().trim().length() >= 6) {
                    Credential credential = new Credential(id,
                            email.getText().toString().trim(),
                            getSHA256StrJava(password.getText().toString().trim()),
                            new Date());
                    person.setPersonId(id);
                    person.setFirstName(first_name.getText().toString().trim());
                    person.setSurname(surname.getText().toString().trim());
                    person.setGender(gender);
                    if (dob == null) {
                        person.setDob("1990-05-01T14:30:00+09:00");
                    } else {
                        person.setDob(dob);
                    }
                    person.setAddress(address.getText().toString().trim());
                    person.setPersonState(state.getSelectedItem().toString().trim());
                    person.setPostcode(Integer.parseInt(postcode.getText().toString().trim()));
                    person.setCredentialId(credential);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "The password should longer than 6 characters.",
                                Toast.LENGTH_LONG).show();
                    return null;
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Use a email structure.",
                        Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Postcode length should be 4.",
                    Toast.LENGTH_LONG).show();
            return null;
        }
        return person;
    }

    private String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String returnValue = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            returnValue = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
