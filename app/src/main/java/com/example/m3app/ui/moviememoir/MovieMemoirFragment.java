package com.example.m3app.ui.moviememoir;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.m3app.R;
import com.example.m3app.data.Memoir;
import com.example.m3app.data.MomoirAdapter;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.ui.SharedPreference;

import java.util.ArrayList;
import java.util.List;


public class MovieMemoirFragment extends Fragment {

    private MovieMemoirViewModel mViewModel;
    private ListView memoirList;
    private Button date;
    private Button userRating;
    private Button publicRating;
    private Button chooseGenre;
    private Spinner genre;
    private ProgressBar loadingBar;
    private List<Memoir> memoirsList = new ArrayList<>();
    private ArrayAdapter<Memoir> memoirAdapter;
    private List<String> genreList = new ArrayList<>();
    private Context context;

    public MovieMemoirFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.movie_memoir_fragment, container, false);
        mViewModel =
                ViewModelProviders.of(this).get(MovieMemoirViewModel.class);
        memoirList = root.findViewById(R.id.memoir_list_view);
        mViewModel.setSortingOption("0");
        mViewModel.getMemoirProcessing();
        date = root.findViewById(R.id.sort_watch_date);
        date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mViewModel.setSortingOption("1");
                mViewModel.getMemoirProcessing();
            }
        });
        userRating = root.findViewById(R.id.sort_personal_rating);
        userRating.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mViewModel.setSortingOption("2");
                mViewModel.getMemoirProcessing();
            }
        });
        publicRating = root.findViewById(R.id.sort_public_rating);
        publicRating.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mViewModel.setSortingOption("3");
                mViewModel.getMemoirProcessing();
            }
        });
        genreList.add("Action");
        genreList.add("Adventure");
        genreList.add("Comedy");
        genreList.add("Family");
        genreList.add("History");
        genreList.add("Romance");
        genreList.add("War");
        genre = root.findViewById(R.id.genre_filter);
        chooseGenre = root.findViewById(R.id.choose_genre);
        chooseGenre.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                    mViewModel.setSortingOption(genre.getSelectedItem().toString().trim());
                    mViewModel.getMemoirProcessing();
            }
        });
        mViewModel.getListMemoirLiveData().observe(getViewLifecycleOwner(), new Observer<List<Memoir>>() {
            @Override
            public void onChanged(List<Memoir> memoirs) {
                memoirsList = memoirs;
                memoirAdapter = new MomoirAdapter(requireActivity(),R.layout.memoir_list_view,memoirsList);
                memoirList.setAdapter(memoirAdapter);
            }
        });
        final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        memoirList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreference sp = SharedPreference.getInstance(context);
                MovieInfo viewmovie = new MovieInfo(0);
                viewmovie.setPoster(memoirAdapter.getItem(position).getPooster());
                viewmovie.setName(memoirAdapter.getItem(position).getMovieName());
                viewmovie.setMovieReleaseDate(memoirAdapter.getItem(position).getMovieReleaseDate());
                sp.putInt("moviememoirTOmovieview", 0);
                Bundle bundle = new Bundle();
                bundle.putParcelable("searchmovie", viewmovie);
                navController.navigate(R.id.nav_movie_view_fragment,bundle);
            }
        });
        loadingBar = root.findViewById(R.id.loading);
        mViewModel.disableProcessingBar().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                if (number == 0) {
                    loadingBar.setVisibility(View.VISIBLE);
                    chooseGenre.setVisibility(View.GONE);
                } else {
                    loadingBar.setVisibility(View.GONE);
                    chooseGenre.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }
}
