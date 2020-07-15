package com.example.m3app.ui.moviememoir;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.data.Cinema;
import com.example.m3app.data.Memoir;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MovieMemoirViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    NetworkConnection networkConnection = new NetworkConnection();
    private Context context;
    private MutableLiveData<String> getMemoirResultLiveData;
    private MutableLiveData<Integer> addMemoirResult;
    private MutableLiveData<Integer> addCinemaResult;
    private List<Memoir> listMemoir = new ArrayList<>();
    private Integer genreNumber;
    private MutableLiveData<Integer> disableProcessingBar;
    private List<Integer> genreNumberList = new ArrayList<>();
    private MutableLiveData<List<Memoir>> listMemoirLiveData;
    private String option = "5";
    private int count;
    private int publicCouunt;
    public static class Combine1{
        int score;
        String memoir;
        Combine1(int score,String memoir) {
            this.score = score;
            this.memoir = memoir;
        }
    }
    private List<Combine1> list = new ArrayList<>();


    public LiveData<List<Memoir>> getListMemoirLiveData() {
        return listMemoirLiveData;
    }

    public LiveData<String> getMemoirResultLiveData() {
        return getMemoirResultLiveData;
    }

    public LiveData<Integer> addMemoirResultLiveData() {
        return addMemoirResult;
    }

    public LiveData<Integer> addCinemaResultLiveData() {
        return addCinemaResult;
    }

    public LiveData<Integer> disableProcessingBar() {
        return disableProcessingBar;
    }

    public MovieMemoirViewModel() {
        count = 0;
        addMemoirResult = new MutableLiveData<>();
        getMemoirResultLiveData = new MutableLiveData<>();
        listMemoirLiveData = new MutableLiveData<>();
        addCinemaResult = new MutableLiveData<>();
        disableProcessingBar = new MutableLiveData<>();
    }

    public String setSortingOption(String sortingOption) {
        option = sortingOption;
        return option;
    }

    public void getMemoirProcessing() {
        SharedPreference sp = SharedPreference.getInstance(context);
        try {
            GetMemoirsTask getMemoirsTask = new GetMemoirsTask();
            getMemoirsTask.execute(Integer.toString(sp.getInt("personid")));
        } catch (Exception e) {
        }
    }

    private class GetMemoirsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMemoir(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            list = new ArrayList<>();
            listMemoir = new ArrayList<>();
            count = 0;
            publicCouunt = 0;
            genreNumber = 0;
            genreNumberList = new ArrayList<>();
            disableProcessingBar.postValue(0);
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);
                if (resultArray.length()>0){
                    sp.putInt("memoirTotalCount", resultArray.length());
                    if (option.equals("0")) {
                        for (int i = 0; i < resultArray.length(); i++) {
                            sp.putString("memoir" + i, resultArray.get(i).toString());
                            JSONObject jsonObject = new JSONObject(resultArray.get(i).toString());
                            getImageProcessing(jsonObject.getString("moviename"));
                        }
                    } else if (option.equals("1")) {
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(resultArray.get(i).toString());
                            int date = Integer.parseInt(jsonObject.getString("watchdatetime").substring(0,4)
                                    + jsonObject.getString("watchdatetime").substring(5,7)
                                    + jsonObject.getString("watchdatetime").substring(8,10));
                            Combine1 memoir = new Combine1(date,resultArray.get(i).toString());
                            list.add(memoir);
                        }
                        Collections.sort(list, new Comparator<Combine1>() {
                            @Override
                            public int compare(Combine1 o1, Combine1 o2) {
                                return o2.score - o1.score;
                            }
                        });
                        for (int i = 0; i < list.size(); i++) {
                            sp.putString("memoir" + i, list.get(i).memoir);
                            JSONObject jsonObject = new JSONObject(list.get(i).memoir);
                            getImageProcessing(jsonObject.getString("moviename"));
                        }
                    } else if (option.equals("2")) {
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(resultArray.get(i).toString());
                            int rating = Double.valueOf(jsonObject.getDouble("ratingscore")*2).intValue();
                            Combine1 memoir = new Combine1(rating,resultArray.get(i).toString());
                            list.add(memoir);
                        }
                        Collections.sort(list, new Comparator<Combine1>() {
                            @Override
                            public int compare(Combine1 o1, Combine1 o2) {
                                return o2.score - o1.score;
                            }
                        });
                        for (int i = 0; i < list.size(); i++) {
                            sp.putString("memoir" + i, list.get(i).memoir);
                            JSONObject jsonObject = new JSONObject(list.get(i).memoir);
                            getImageProcessing(jsonObject.getString("moviename"));
                        }
                    } else {
                        for (int i = 0; i < resultArray.length(); i++) {
                            sp.putString("memoir" + i, resultArray.get(i).toString());
                            JSONObject jsonObject = new JSONObject(resultArray.get(i).toString());
                            getMemoirDetailProcessing(jsonObject.getString("moviename"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getMemoirDetailProcessing(String movieName){
        GetMemoirDetailTask getMemoirDetailTask = new GetMemoirDetailTask();
        getMemoirDetailTask.execute(movieName);
    }

    private class GetMemoirDetailTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMemoirDetail(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreference sp = SharedPreference.getInstance(context);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (option.equals("3")) {
                    list.add(new Combine1(Double.valueOf(jsonObject.getDouble("vote_average")*10).intValue(),sp.getString("memoir" + publicCouunt)));
                    publicCouunt = publicCouunt + 1;
                    if (list.size() == sp.getInt("memoirTotalCount")) {
                        Collections.sort(list, new Comparator<Combine1>() {
                            @Override
                            public int compare(Combine1 o1, Combine1 o2) {
                                return o2.score - o1.score;
                            }
                        });
                        for (int i = 0; i < list.size(); i++) {
                            sp.putString("memoir" + i, list.get(i).memoir);
                            JSONObject jsonObject1 = new JSONObject(list.get(i).memoir);
                            getImageProcessing(jsonObject1.getString("moviename"));
                        }
                    }
                } else {
                    JSONArray genresList = jsonObject.getJSONArray("genres");
                    if (genresList.length()>0) {
                        for (int i = 0; i<genresList.length(); i++){
                            if (genresList.getJSONObject(i).getString("name").equals(option)) {
                                genreNumberList.add(genreNumber);
                                getImageProcessing(jsonObject.getString("original_title"));
                                break;
                            }
                        }
                    }
                    genreNumber = genreNumber + 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getImageProcessing(String movieName){
        GetImageTask getImageTask = new GetImageTask();
        getImageTask.execute(movieName);
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(String... params) {
            return networkConnection.getMemoirPooster(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            SharedPreference sp = SharedPreference.getInstance(context);
            if (option.equals("0") || option.equals("1") || option.equals("2") || option.equals("3")) {
                Memoir memoir = new Memoir(count);
                try {
                    JSONObject jsonObject = new JSONObject(sp.getString("memoir" + count));
                    JSONObject cinemaArray = jsonObject.getJSONObject("cinemaid");
                    memoir.setMovieName(jsonObject.getString("moviename"));
                    memoir.setMovieReleaseDate(jsonObject.getString("moviereleasedate"));
                    memoir.setWatchDateTime(jsonObject.getString("watchdatetime"));
                    memoir.setPostcode(cinemaArray.getInt("postcode"));
                    memoir.setComments(jsonObject.getString("comments"));
                    memoir.setRatingScore(jsonObject.getDouble("ratingscore"));
                    memoir.setPooster(result);
                    listMemoir.add(memoir);
                    count = count+1;
                    listMemoirLiveData.postValue(listMemoir);
                    if (count == sp.getInt("memoirTotalCount")) {
                        disableProcessingBar.postValue(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Memoir memoir = new Memoir(count);
                try {
                    JSONObject jsonObject = new JSONObject(sp.getString("memoir" + genreNumberList.get(count)));
                    JSONObject cinemaArray = jsonObject.getJSONObject("cinemaid");
                    memoir.setMovieName(jsonObject.getString("moviename"));
                    memoir.setMovieReleaseDate(jsonObject.getString("moviereleasedate"));
                    memoir.setWatchDateTime(jsonObject.getString("watchdatetime"));
                    memoir.setPostcode(cinemaArray.getInt("postcode"));
                    memoir.setComments(jsonObject.getString("comments"));
                    memoir.setRatingScore(jsonObject.getDouble("ratingscore"));
                    memoir.setPooster(result);
                    listMemoir.add(memoir);
                    count = count+1;
                    listMemoirLiveData.postValue(listMemoir);
                    if (count == genreNumberList.size()) {
                        disableProcessingBar.postValue(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void getAllMemoirProcessing() {
        try {
            GetAllMemoirsTask getMemoirsTask = new GetAllMemoirsTask();
            getMemoirsTask.execute();
        } catch (Exception e) {
        }
    }

    private class GetAllMemoirsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getAllMemoir();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray resultArray = new JSONArray(result);
                SharedPreference sp = SharedPreference.getInstance(context);
                if (resultArray.length()>0){
                    sp.putInt("allMemoirTotalCount", resultArray.length());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMemoirProcessing(Memoir memoir) {
        addMemoirProcessingTask addMemoirTask = new addMemoirProcessingTask();
        addMemoirTask.execute(memoir);
    }

    private class addMemoirProcessingTask extends AsyncTask<Memoir, Void, String> {
        @Override
        protected String doInBackground(Memoir... params) {
            return networkConnection.addMemoir(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("204"))
                addMemoirResult.setValue(0);
            else
                addMemoirResult.setValue(1);
        }
    }

    public void addCinemaProcessing(Cinema cinema) {
        addCinemaProcessingTask addCinemaTask = new addCinemaProcessingTask();
        addCinemaTask.execute(cinema);
    }

    private class addCinemaProcessingTask extends AsyncTask<Cinema, Void, String> {
        @Override
        protected String doInBackground(Cinema... params) {
            return networkConnection.addCinema(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("204"))
                addCinemaResult.setValue(0);
            else
                addCinemaResult.setValue(1);
        }
    }

}
