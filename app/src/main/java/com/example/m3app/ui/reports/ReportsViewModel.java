package com.example.m3app.ui.reports;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;

public class ReportsViewModel extends ViewModel {

    NetworkConnection networkConnection = new NetworkConnection();
    private Context context;
    private MutableLiveData<String> getMemoirStartEndResultLiveData;
    private MutableLiveData<String> getMemoirByYearResultLiveData;


    public LiveData<String> getMemoirStartEndResultLiveData() {
        return getMemoirStartEndResultLiveData;
    }
    public LiveData<String> getMemoirByYearResultLiveData() {
        return getMemoirByYearResultLiveData;
    }

    public ReportsViewModel() {
        getMemoirStartEndResultLiveData = new MutableLiveData<>();
        getMemoirByYearResultLiveData = new MutableLiveData<>();
    }

    public void getMemoirByStartEndProcessing(String start,String end) {
        try {
            SharedPreference sp = SharedPreference.getInstance(context);
            GetMemoirByStartEndTask getMemoirByStartEndTask = new GetMemoirByStartEndTask();
            getMemoirByStartEndTask.execute(Integer.toString(sp.getInt("personid")),start, end);
        } catch (Exception e) {
            getMemoirStartEndResultLiveData.setValue("Error");
        }
    }

    public void getMemoirByYearProcessing(String year) {
        try {
            SharedPreference sp = SharedPreference.getInstance(context);
            getMemoirByYearTask getMemoirByYearTask = new getMemoirByYearTask();
            getMemoirByYearTask.execute(Integer.toString(sp.getInt("personid")),year);
        } catch (Exception e) {
            getMemoirByYearResultLiveData.setValue("Error");
        }
    }

    private class getMemoirByYearTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMemoirByYear(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);

                if (resultArray.length()>0){
                    sp.putInt("monthCount", resultArray.length());
                    for (int i = 0; i < resultArray.length(); i++) {
                        sp.putString("monthMovies" + i, resultArray.get(i).toString());
                    }
                    getMemoirByYearResultLiveData.setValue("got");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                getMemoirByYearResultLiveData.setValue("error");
            }
        }
    }

    private class GetMemoirByStartEndTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMemoirByStartEnd(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);

                if (resultArray.length()>0){
                    sp.putInt("postcodeCount", resultArray.length());
                    for (int i = 0; i < resultArray.length(); i++) {
                        sp.putString("postcodeMovies" + i, resultArray.get(i).toString());
                    }
                    getMemoirStartEndResultLiveData.setValue("got");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                getMemoirStartEndResultLiveData.setValue("error");
            }
        }
    }
}
