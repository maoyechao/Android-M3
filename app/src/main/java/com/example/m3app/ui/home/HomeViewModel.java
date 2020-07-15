package com.example.m3app.ui.home;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class HomeViewModel extends ViewModel {

    NetworkConnection networkConnection = new NetworkConnection();
    private Context context;
    private MutableLiveData<String> getMoviesResultLiveData;
    private MutableLiveData<String> getPersonResultLiveData;

    public LiveData<String> getGetMoviesResultLiveData() {
        return getMoviesResultLiveData;
    }
    public LiveData<String> getPersonResultLiveData() {
        return getPersonResultLiveData;
    }

    public HomeViewModel() {
        SharedPreference sp = SharedPreference.getInstance(context);
        getMoviesResultLiveData = new MutableLiveData<>();
        getPersonResultLiveData = new MutableLiveData<>();
        try {
            GetMoviesTask getMoviesTask = new GetMoviesTask();
            getMoviesTask.execute(Integer.toString(sp.getInt("personid")));
        } catch (Exception e) {
            getMoviesResultLiveData.setValue("Error1");
        }
    }

    private class GetMoviesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getCurrentYearTop5MoviesByPersonID(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);
                //refreshcount init in SigninViewModel
                if (resultArray.length()>0 && sp.getInt("refreshcount") == 0){
                    for (int i = 0; i < resultArray.length(); i++) {
                        sp.putString("memoir"+i, resultArray.get(i).toString());
                    }
                }
                Date date = new Date();
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String todayDate = format.format(date);
                getPersonResultLiveData.setValue("Hello, " + sp.getString("firstname") + "!" +
                        System.getProperty("line.separator") + "Welcome to the: " +
                        System.getProperty("line.separator") + " My Movie Memoir." +
                        System.getProperty("line.separator") + "Today is: "+ todayDate
                );
                String text = "Top 5 movies watch for the current year:";
                for (int i = 0; i < 5; i++) {
                    JSONObject memoir = new JSONObject(sp.getString("memoir"+i));
                    String moviename = "Movie Name: " + memoir.getString("moviename") + "  ";
                    String moviereleasedate = "Movie ReleaseDate: "
                            + memoir.getString("moviereleasedate").substring(0,10) + " "
                            + memoir.getString("moviereleasedate").substring(24) + "  ";
                    String ratingscore = "Movie Score: " + memoir.getString("ratingscore");
                    text = text + System.getProperty("line.separator") + moviename + moviereleasedate + ratingscore;
                }
                getMoviesResultLiveData.setValue(text);
                sp.putInt("refreshcount", 1);
            } catch (JSONException e) {
                e.printStackTrace();
                getMoviesResultLiveData.setValue("Do not have movie memoir yet.");
            }
        }
    }
}