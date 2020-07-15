package com.example.m3app.ui.moviesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieSearchViewModel extends ViewModel {
    NetworkConnection networkConnection = new NetworkConnection();
    private MutableLiveData<List<MovieInfo>> movieInfoList;
    private MutableLiveData<Bitmap> movieImageLiveData;
    private MutableLiveData<String> movieLiveData;
    private Bitmap poster;
    private Context context;
    private int count;
    private List<MovieInfo> movieList = new ArrayList<>();

    public MovieSearchViewModel() {
        count = 0;
        movieInfoList = new MutableLiveData<>();
        movieImageLiveData = new MutableLiveData<>();
        movieLiveData = new MutableLiveData<>();
    }

    public LiveData<List<MovieInfo>> getMovie() {
        return movieInfoList;
    }

    public void getMovieProcessing(String string){
        GetMovieTask getMovieTask = new GetMovieTask();
        getMovieTask.execute(string);
    }

    private class GetMovieTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMovie(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            movieList = new ArrayList<>();
            try {
                SharedPreference sp = SharedPreference.getInstance(context);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray resultArray = jsonObject.getJSONArray("results");
                if (resultArray.length()>0){
                    sp.putInt("searchmovieCount",resultArray.length());
                    count = 0;
                    for (int i = 0; i < 5 && i<sp.getInt("searchmovieCount"); i++){
                        sp.putString("searchmovie"+i, resultArray.get(i).toString());
                        JSONObject firstAddObj = new JSONObject(resultArray.get(i).toString());
                        String posterPath = firstAddObj.getString("poster_path");
                        getImageProcessing(posterPath);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getImageProcessing(String posterPath){
        GetImageTask getImageTask = new GetImageTask();
        getImageTask.execute(posterPath);
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(String... params) {
            return networkConnection.getMoviePoster(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            SharedPreference sp = SharedPreference.getInstance(context);
            try {
                JSONObject searchmovie = new JSONObject(sp.getString("searchmovie"+count));
                MovieInfo movie = new MovieInfo(count,
                        searchmovie.getString("original_title"),
                        searchmovie.getString("release_date"),
                        result);
                movieList.add(movie);
                poster = result;
                count = count+1;
                movieInfoList.postValue(movieList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}