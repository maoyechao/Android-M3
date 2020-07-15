package com.example.m3app.ui.map;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.m3app.R;
import com.example.m3app.ui.SharedPreference;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.WeakHashMap;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapViewModel viewModel;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        viewModel.init(this);
        viewModel.getUserAddProcessing();
        viewModel.getAddProcessing();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        viewModel.getUerPositionLiveData().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                Marker marker = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))// color
                                .title("Marker for Current User Position")
                                .snippet("Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude)
                );
                marker.showInfoWindow();
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        });

        viewModel.getAddressListLiveData().observe(this, new Observer<List<LatLng>>() {
            @Override
            public void onChanged(List<LatLng> latLngs) {
                LatLng l = latLngs.get(latLngs.size()-1);
                SharedPreference sp = SharedPreference.getInstance(context);
                int cinemaNumber = latLngs.size()-1;
                try {
                    JSONObject cinema = new JSONObject(sp.getString("cinema"  + cinemaNumber));
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(l)
                            .title(cinema.getString("cinemaname"))
                            .snippet("Latitude: " + l.latitude + ", Longitude: " + l.longitude)
                    );
                    marker.showInfoWindow();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });

    }
}
