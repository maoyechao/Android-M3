package com.example.m3app.ui.moviesearch;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.m3app.R;
import com.example.m3app.data.MovieAdapter;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieSearchFragment extends Fragment {

    private static String movieSearchFragment = "msf";
    private MovieSearchViewModel movieSearchViewModel;
    private MovieViewViewModel movieViewViewModel;
    private ListView listView;
    private List<MovieInfo> movieEntityList = new ArrayList<>();
    private ArrayAdapter<MovieInfo> movieAdapter;
    private Button search;
    private EditText movieName;
    private Context context;

    public MovieSearchFragment() {
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        movieSearchViewModel =
                ViewModelProviders.of(this).get(MovieSearchViewModel.class);
        movieViewViewModel =
                ViewModelProviders.of(this).get(MovieViewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_moviesearch, container, false);
        listView = root.findViewById(R.id.list_view);
        search = root.findViewById(R.id.bt_search);
        movieName = root.findViewById(R.id.movie);

        movieSearchViewModel.getMovie().observe(getViewLifecycleOwner(), new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(List<MovieInfo> MovieInfos) {
                movieEntityList = MovieInfos;
                movieAdapter = new MovieAdapter(requireActivity(),R.layout.movie_list,movieEntityList);
                listView.setAdapter(movieAdapter);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                movieSearchViewModel.getMovieProcessing(movieName.getText().toString().trim());
            }
        });

        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreference sp = SharedPreference.getInstance(context);
                sp.putString("movieViewOnClick",sp.getString("searchmovie"+id));
                sp.putInt("searchTOmovieview",0);
                Bundle bundle = new Bundle();
                bundle.putParcelable("searchmovie",movieAdapter.getItem(position));
                navController.navigate(R.id.nav_movie_view_fragment,bundle);
            }
        });
        return root;
    }
}
