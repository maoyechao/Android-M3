package com.example.m3app.ui.moviesearch;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m3app.R;
import com.example.m3app.data.Movie;
import com.example.m3app.data.MovieDB;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.ui.SharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.m3app.data.Converters.fromTimestamp;

public class MovieViewFragment extends Fragment {

    private MovieViewViewModel mViewModel;
    private ImageView image;
    private List<String> movie;
    private Context context;
    private MovieInfo viewmovie = new MovieInfo(0);
    private MovieDB movieDB;
    private NavController navController;
    public MovieViewFragment() {
    }

    public void initMovieDB(Context context){
        movieDB = MovieDB.getInstance(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewmovie = getArguments().getParcelable("searchmovie");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_view_fragment, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(MovieViewViewModel.class);
        image = view.findViewById(R.id.movie_view_poster);
        final TextView movieName = view.findViewById(R.id.movie_view_moviename);
        final TextView relaesedate = view.findViewById(R.id.movie_view_relaesedate);
        final TextView country = view.findViewById(R.id.movie_view_country);
        final TextView genre_title = view.findViewById(R.id.movie_view_genre_title);
        final TextView genre = view.findViewById(R.id.movie_view_genre);
        final TextView cast_title = view.findViewById(R.id.movie_view_cast_title);
        final TextView cast = view.findViewById(R.id.movie_view_cast);
        final TextView director_title = view.findViewById(R.id.movie_view_director_title);
        final TextView director = view.findViewById(R.id.movie_view_director);
        final RatingBar scorerating = view.findViewById(R.id.movie_view_scorerating);
        final TextView overview = view.findViewById(R.id.movie_view_overview);
        final Button watch = view.findViewById(R.id.add_to_watchlist);
        final Button memoir = view.findViewById(R.id.add_to_memoir);


        mViewModel.getMovieIDProcessing(viewmovie.getName());
        mViewModel.movieIDMovieLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(Integer movieID) {
                mViewModel.getMovieVIEWProcessing(movieID);
                mViewModel.getMovieVIEW().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChanged(List<String> movieView) {
                        image.setImageBitmap(viewmovie.getPoster());
                        movieName.setText(movieView.get(0));
                        relaesedate.setText(movieView.get(1));
                        country.setText(movieView.get(3));
                        genre_title.setText("Genre:");
                        genre.setText(movieView.get(4));
                        cast_title.setText("Cast:");
                        cast.setText(movieView.get(5));
                        director_title.setText("Director:");
                        director.setText(movieView.get(6));
                        scorerating.setRating(Float.parseFloat(movieView.get(2)));
                        overview.setText(movieView.get(7));
                        movie = movieView;
                        SharedPreference sp = SharedPreference.getInstance(context);
                        //it runs 6 times....
                        sp.putInt("watchlistTOmovieview",1);
                        watch.setVisibility(View.VISIBLE);
                        memoir.setVisibility(View.VISIBLE);
                        if (sp.getInt("watchlistTOmovieview") == 0 || sp.getInt("watchlistTOaddtomemoir") == 0) {
                            watch.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        mViewModel.initMovieDB(getContext());
        mViewModel.readMovieList();
        SharedPreference sp = SharedPreference.getInstance(context);
        if (sp.getInt("searchTOmovieview") == 0 || sp.getInt("moviememoirTOmovieview") == 0 ) {
            sp.putInt("watchlistTOmovieview",1);
            sp.putInt("watchlistTOaddtomemoir",1);
        } else {
            sp.putInt("watchlistTOmovieview",0);
            sp.putInt("watchlistTOaddtomemoir",0);
        }

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMovieDB(getContext());
                CheckMovieTask checkMovieTask = new CheckMovieTask();
                checkMovieTask.execute(movie.get(0), movie.get(1));
            } });
        memoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference sp = SharedPreference.getInstance(context);
                if (sp.getInt("watchlistTOmovieview") == 0) {
                    sp.putInt("watchlistTOaddtomemoir",0);
                }
                navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putParcelable("viewmovie",viewmovie);
                navController.navigate(R.id.nav_add_to_memoir,bundle);
            } });
        return view;
    }

    private class CheckMovieTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            int result = 1;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                long releaseDate = format.parse(params[1]).getTime();
                Date date = fromTimestamp(releaseDate);
                if (movieDB.movieDAO().getAll() == null) {
                    return 1;
                }
                Movie movie = movieDB.movieDAO().getMovieByNameAndDate(params[0],date);
                if (movie == null) {
                    result = 0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                Toast.makeText(requireActivity(),
                        "Movie, " + movie.get(0) + " exist.",
                        Toast.LENGTH_LONG).show();
            }else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                long releaseDate = 0;
                try {
                    releaseDate = format.parse(movie.get(1)).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date = fromTimestamp(releaseDate);
                mViewModel.insertMovie(new Movie(0,movie.get(0),date,new Date()));
                navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putParcelable("viewmovie",viewmovie);
                navController.navigate(R.id.nav_watchlist,bundle);
                Toast.makeText(requireActivity(),
                        "Inserted, " + movie.get(0) + " to watchlist.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
