package com.example.m3app.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.m3app.R;

import java.util.List;

public class MomoirAdapter extends ArrayAdapter {
    private int resourceId;
    private ImageView poster;
    private TextView moviereleasedate;
    private TextView moviename;
    private TextView memoirwatchdate;
    private TextView comments;
    private TextView postcode;
    private RatingBar scorerating;

    public MomoirAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Memoir memoir = (Memoir) getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        poster = view.findViewById(R.id.poster);
        moviereleasedate = view.findViewById(R.id.moviereleasedate);
        moviename = view.findViewById(R.id.moviename);
        memoirwatchdate = view.findViewById(R.id.movierwatchdate);
        comments = view.findViewById(R.id.comment);
        postcode = view.findViewById(R.id.cinemapostcode);
        scorerating = view.findViewById(R.id.memoir_scorerating);

        poster.setImageBitmap(memoir.getPooster());
        moviereleasedate.setText("Release date:         "+memoir.getMovieReleaseDate().substring(0,10));
        moviename.setText(memoir != null ? memoir.getMovieName() : "No movie name");
        memoirwatchdate.setText("Watch date:            " +memoir.getWatchDateTime().substring(0,10));
        comments.setText("Comment:              "+memoir.getComments());
        postcode.setText("Cinema Postcode:"+memoir.getPostcode());
        scorerating.setRating(Float.parseFloat(String.valueOf(memoir.getRatingScore()*2)));
        return view;
    }
}
