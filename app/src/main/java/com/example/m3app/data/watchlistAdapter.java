package com.example.m3app.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m3app.R;

import java.util.List;

import static com.example.m3app.data.Converters.dateToTimestamp;

public class watchlistAdapter extends ArrayAdapter<Movie> {
    private int resourceId;
    private TextView moviewatchlistname;
    private TextView moviewatchreleasedate;
    private TextView movieadddate;

    public watchlistAdapter(@NonNull Context context, int resource, @NonNull List<Movie> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        moviewatchlistname = view.findViewById(R.id.moviewatchlistname);
        moviewatchreleasedate = view.findViewById(R.id.moviewatchreleasedate);
        movieadddate = view.findViewById(R.id.movieadddate);

        moviewatchlistname.setText(movie != null ? movie.getMovieName() : "No movie release date");
        moviewatchreleasedate.setText(movie != null ? movie.getReleaseDate().toString().substring(0,10)+" "+ movie.getReleaseDate().toString().substring(30): "No movie release date");
        movieadddate.setText(movie != null ? movie.getWatchDate().toString().substring(0,10) + " " + movie.getWatchDate().toString().substring(30) : "No movie name");
        return view;
    }
}
