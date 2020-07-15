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

import com.example.m3app.R;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<MovieInfo> {
    private int resourceId;
    private ImageView poster;
    private TextView moviereleasedate;
    private TextView moviename;

    public MovieAdapter(@NonNull Context context, int resource, @NonNull List<MovieInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo movie = getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        poster = view.findViewById(R.id.poster);
        moviereleasedate = view.findViewById(R.id.moviereleasedate);
        moviename = view.findViewById(R.id.moviename);

        poster.setImageBitmap(movie.getPoster());
        moviereleasedate.setText(movie != null ? movie.getMovieReleaseDate() : "No movie release date");
        moviename.setText(movie != null ? movie.getName() : "No movie name");
        return view;
    }

}
