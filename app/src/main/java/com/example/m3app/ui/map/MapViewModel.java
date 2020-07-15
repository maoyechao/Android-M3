package com.example.m3app.ui.map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {
    NetworkConnection networkConnection = new NetworkConnection();
    private MutableLiveData<List<LatLng>> addressListLiveData;
    private MutableLiveData<LatLng> uerPositionListLiveData;
    private Context context;
    private List<LatLng> addList;
    private LatLng uerPosition;

    public MapViewModel() {
        addressListLiveData = new MutableLiveData<>();
        uerPositionListLiveData = new MutableLiveData<>();
    }

    public void init(Context context){
        this.context = context;
        this.addList = new ArrayList<>();
    }
    public LiveData<List<LatLng>> getAddressListLiveData() {
        return addressListLiveData;
    }
    public LiveData<LatLng> getUerPositionLiveData() {
        return uerPositionListLiveData;
    }

    public void getUserAddProcessing(){
        SharedPreference sp = SharedPreference.getInstance(context);
        try {
            GetUserAddressTask addPersonTask = new GetUserAddressTask();
            addPersonTask.execute(sp.getString("address") + " "+ sp.getInt("postcode") + " Australia");
        } catch (Exception e) {
            Log.e("MapView","getAddProcessing error");
        }
    }

    private class GetUserAddressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getLatLng(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray resultArray = jsonObject.getJSONArray("results");
                if (resultArray.length()>0){
                    JSONObject firstAddObj = new JSONObject(resultArray.get(0).toString());
                    JSONObject firstResult = firstAddObj.getJSONObject("geometry");
                    LatLng latLng = new LatLng(firstResult.getDouble("lat"),firstResult.getDouble("lng"));
                    Log.i("mapModelView",latLng.latitude + " " + latLng.longitude);
                    uerPosition = latLng;
                    uerPositionListLiveData.setValue(uerPosition);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAddProcessing(){
        SharedPreference sp = SharedPreference.getInstance(context);
        try {
            for (int i = 0; i< sp.getInt("cinemaTotalCount"); i++) {
                JSONObject cinema = new JSONObject(sp.getString("cinema"+i));
                GetAddressTask addCinemaTask = new GetAddressTask();
                addCinemaTask.execute(cinema.getString("postcode") + " VIC Australia");
            }
        } catch (Exception e) {
            Log.e("MapView","getAddProcessing error");
        }
    }

    private class GetAddressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getLatLng(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray resultArray = jsonObject.getJSONArray("results");
                if (resultArray.length()>0){
                    JSONObject firstAddObj = new JSONObject(resultArray.get(0).toString());
                    JSONObject firstResult = firstAddObj.getJSONObject("geometry");
                    LatLng latLng = new LatLng(firstResult.getDouble("lat"),firstResult.getDouble("lng"));
                    Log.i("mapModelView",latLng.latitude + " " + latLng.longitude);
                    addList.add(latLng);
                    addressListLiveData.setValue(addList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
