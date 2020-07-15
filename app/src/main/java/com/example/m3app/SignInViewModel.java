package com.example.m3app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.data.Credential;
import com.example.m3app.data.Person;
import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SignInViewModel extends ViewModel {
    NetworkConnection networkConnection = new NetworkConnection();
    Person tempPerson = new Person(1);
    private MutableLiveData<Integer> signInResultLiveData;
    private Context context;

    public SignInViewModel() {
        signInResultLiveData = new MutableLiveData<>();
        signInResultLiveData.setValue(404);
    }

    public LiveData<Integer> getSignInResultLiveData() {
        return signInResultLiveData;
    }

    public void initContext(Context context) {
        this.context = context;
    }

    public void getCinemaProcess() {
        try {
            GetCinemaTask getCinemaTask = new GetCinemaTask();
            getCinemaTask.execute();
        } catch (Exception e) {
        }
    }

    private class GetCinemaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getCinema();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);
                if (resultArray.length()>0){
                    sp.putInt("cinemaTotalCount", resultArray.length());
                    for (int i = 0; i < resultArray.length(); i++) {
                        sp.putString("cinema" + i, resultArray.get(i).toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void singInProcess(String username, String password) {
        try {
            GetPersonTask getPersonTask = new GetPersonTask();
            getPersonTask.execute(username, password);
        } catch (Exception e) {
            signInResultLiveData.setValue(1);
        }
    }

    private class GetPersonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getPerson(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Credential tempCredential = new Credential(
                        jsonObject.getJSONObject("credentialid").getInt("credentialid"));
                tempPerson =
                        new Person(
                                jsonObject.getInt("personid"),
                                jsonObject.getString("firstname"),
                                jsonObject.getString("surname"),
                                jsonObject.getString("gender"),
                                convertToDate(jsonObject.getString("dob")),
                                jsonObject.getString("address"),
                                jsonObject.getString("state"),
                                jsonObject.getInt("postcode"),
                                tempCredential
                        );
                SharedPreference sp = SharedPreference.getInstance(context);
                sp.removeAll();
                sp.putInt("personid",tempPerson.getPersonId());
                sp.putString("firstname", tempPerson.getFirstName());
                sp.putString("surname", tempPerson.getSurname());
                sp.putString("gender", tempPerson.getGender());
                sp.putString("dob", tempPerson.getDob());
                sp.putString("address", tempPerson.getAddress());
                sp.putString("state", tempPerson.getPersonState());
                sp.putInt("postcode", tempPerson.getPostcode());
                sp.putInt("credentialid", jsonObject.getJSONObject("credentialid").getInt("credentialid"));

                //Control the homepage get top five movie list from server side only one times.
                sp.putInt("refreshcount", 0);
                if (jsonObject.getString("firstname").length() > 0){
                    signInResultLiveData.setValue(0);
                }
            } catch (JSONException e) {
                signInResultLiveData.setValue(1);
                e.printStackTrace();
            }
        }
    }

    private Date convertToDate(String str) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new Date(simpleDateFormat.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}