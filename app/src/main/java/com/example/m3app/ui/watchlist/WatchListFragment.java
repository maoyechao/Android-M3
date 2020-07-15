package com.example.m3app.ui.watchlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.m3app.R;
import com.example.m3app.data.Memoir;
import com.example.m3app.data.MomoirAdapter;
import com.example.m3app.data.Movie;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.data.watchlistAdapter;
import com.example.m3app.ui.SharedPreference;
import com.example.m3app.ui.moviememoir.MovieMemoirViewModel;
import com.example.m3app.ui.moviesearch.MovieViewViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchListFragment extends Fragment {
    private ArrayAdapter<Movie> movieWatchAdapter;
    private List<Movie> moviewatchlist;
    private MovieInfo searchmovie;
    private ListView watchListView;
    private MovieViewViewModel movieViewViewModel;
    private Context context;
    private Bitmap passingPooster;
    private MovieInfo viewmovie;
    private FirebaseFirestore movies = FirebaseFirestore.getInstance();

    public WatchListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewmovie = getArguments().getParcelable("viewmovie");
        }
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        movieViewViewModel =
                ViewModelProviders.of(this).get(MovieViewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        final Button delete  = root.findViewById(R.id.Delete);
        final Button view  = root.findViewById(R.id.View);
        watchListView = root.findViewById(R.id.watchlist_view);
        movieViewViewModel.initMovieDB(getContext());
        movieViewViewModel.readMovieList();
        movieViewViewModel.getMovieList().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> s) {
                moviewatchlist = s;
                movieWatchAdapter = new watchlistAdapter(requireActivity(),R.layout.movie_watch_list,moviewatchlist);
                watchListView.setAdapter(movieWatchAdapter);
            }
        });
        watchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreference sp = SharedPreference.getInstance(context);
                sp.putInt("watchlistOnClickPosition",position);
                String moviename = movieWatchAdapter.getItem(position).getMovieName();
                sp.putString("watchlistOnClickmoviename",moviename);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String releaseDate = format.format(movieWatchAdapter.getItem(position).getReleaseDate());
                sp.putString("watchlistOnClickmoviereleasedate",releaseDate);
                sp.putInt("searchTOmovieview",1);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference sp = SharedPreference.getInstance(context);
                if (sp.getInt("watchlistOnClickPosition") == moviewatchlist.size()) {
                    Toast.makeText(requireActivity(),
                            "Cannot delete un-exist movie",
                            Toast.LENGTH_LONG).show();
                } else {
                    //sp.getInt default walue is 0;
                    movieViewViewModel.deleteMovieProcessing(sp.getInt("watchlistOnClickPosition"));
                }
            }
        });
        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference sp = SharedPreference.getInstance(context);
                if (sp.getInt("watchlistOnClickPosition") == moviewatchlist.size()) {
                    Toast.makeText(requireActivity(),
                            "Cannot view un-exist movie",
                            Toast.LENGTH_LONG).show();
                } else {
                sp.putInt("watchlistTOmovieview",0);
                sp.putInt("moviememoirTOmovieview", 1);
                String moviename2 = sp.getString("watchlistOnClickmoviename");
                movieViewViewModel.getImageProcessing(moviename2);
                }
            }
        });
        movieViewViewModel.getPoster().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap poster) {
                if (poster != null) {
                    viewmovie = new MovieInfo(0);
                    SharedPreference sp = SharedPreference.getInstance(context);
                    viewmovie.setPoster(poster);
                    viewmovie.setName(sp.getString("watchlistOnClickmoviename"));
                    viewmovie.setMovieReleaseDate(sp.getString("watchlistOnClickmoviereleasedate"));
                    if (sp.getInt("watchlistTOmovieview") == 0) {
                        sp.putInt("searchTOmovieview",1);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("searchmovie",viewmovie);
                        navController.navigate(R.id.nav_movie_view_fragment,bundle);
                    }
                }
            }
        });
        return root;
    }
}
