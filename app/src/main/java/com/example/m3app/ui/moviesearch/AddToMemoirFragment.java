package com.example.m3app.ui.moviesearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.m3app.R;
import com.example.m3app.SignInActivity;
import com.example.m3app.data.Cinema;
import com.example.m3app.data.Credential;
import com.example.m3app.data.Memoir;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.data.Person;
import com.example.m3app.ui.SharedPreference;
import com.example.m3app.ui.moviememoir.MovieMemoirViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddToMemoirFragment extends Fragment {
    private MovieMemoirViewModel mViewModel;
    private MovieInfo movieview;
    private ImageView image;
    private TextView moviename;
    private TextView releasedate;
    private TextView watcheddate;
    private TextView postcode;
    private DatePicker watchdate;
    private String watchDateString;
    private Spinner address;
    private String selectedAddress;
    private Button add_address;
    private EditText comment;
    private EditText optionName;
    private EditText optionPostcode;
    private RatingBar ratingbar;
    private Button add_memoir;
    private MovieViewViewModel movieViewViewModel;
    private Context context;
    private List<String> postcodeSpinner = new ArrayList<>();
    private List<String> checkCinema = new ArrayList<>();
    private ArrayAdapter<String> aa;

    public AddToMemoirFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieview = getArguments().getParcelable("viewmovie");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_to_memoir, container, false);
        movieViewViewModel =
                ViewModelProviders.of(this).get(MovieViewViewModel.class);
        mViewModel = ViewModelProviders.of(this).get(MovieMemoirViewModel.class);

        image = root.findViewById(R.id.poster);
        image.setImageBitmap(movieview.getPoster());
        moviename = root.findViewById(R.id.moviename);
        moviename.setText(movieview.getName());
        releasedate = root.findViewById(R.id.moviereleasedate);
        releasedate.setText(movieview.getMovieReleaseDate());
        watcheddate = root.findViewById(R.id.addwatchdate);
        postcode = root.findViewById(R.id.cinemaaddress);
        watchdate = root.findViewById(R.id.datepickerwatched);
        watchdate.init(2020, 5, 1, new DatePicker.OnDateChangedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year,monthOfYear,dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
                watchDateString = sdf.format(c);
            }
        });
        address = root.findViewById(R.id.spinner_cinemas);
        SharedPreference sp = SharedPreference.getInstance(context);
        for (int i = 0; i < sp.getInt("cinemaTotalCount"); i++) {
            try {
                JSONObject cinema = new JSONObject(sp.getString("cinema"+i));
                int j = i + 1;
                postcodeSpinner.add(j + ". "+cinema.getString("cinemaname") + " " + cinema.getString("postcode"));
                checkCinema.add(cinema.getString("cinemaname") + " " + cinema.getString("postcode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        aa  = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, postcodeSpinner);
        address.setAdapter(aa);
        optionName = root.findViewById(R.id.option_cinema_name);
        optionPostcode = root.findViewById(R.id.option_postcode);
        add_address = root.findViewById(R.id.add_cinema);
        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add = optionName.getText().toString().trim() + " " + optionPostcode.getText().toString();
                if (optionPostcode.getText().toString().length() != 4) {
                    Toast.makeText(requireActivity(),
                            "The postcode should be 4 char.",
                            Toast.LENGTH_SHORT).show();
                    //optionPostcode.setError("The postcode should be 4 char.");
                } else if (TextUtils.isEmpty(optionName.getText())) {
                    Toast.makeText(requireActivity(),
                            "The cinema name is required.",
                            Toast.LENGTH_SHORT).show();
                    //optionPostcode.setError("The cinema name is required.");
                } else if (checkCinema.contains(add)) {
                    Toast.makeText(getContext(), "Cinema exists.", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreference sp = SharedPreference.getInstance(context);
                    int j = 1 + sp.getInt("cinemaTotalCount");
                    postcodeSpinner.add(j+ ". " + add);
                    aa.notifyDataSetChanged();
                    address.setSelection(aa.getPosition(j+ ". " + add));
                    mViewModel.addCinemaProcessing(new Cinema(j
                            ,optionName.getText().toString().trim()
                            ,Integer.parseInt(optionPostcode.getText().toString())));
                }
            }
        });
        mViewModel.addCinemaResultLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer result) {
                if (result == 0) {
                    SharedPreference sp = SharedPreference.getInstance(context);
                    sp.putInt("cinemaTotalCount",1 + sp.getInt("cinemaTotalCount"));
                    JSONObject object = new JSONObject();
                    try {
                        object.put("cinemaid",sp.getInt("cinemaTotalCount"));
                        object.put("cinemaname",optionName.getText().toString().trim());
                        object.put("postcode",Integer.parseInt(optionPostcode.getText().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sp.putString("cinema" + (sp.getInt("cinemaTotalCount")-1), object.toString());
                    Toast.makeText(requireActivity(),
                            "Success Add cinema, " + optionName.getText().toString().trim() + " to M3",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireActivity(),
                            "Fail add cinema, " + optionName.getText().toString().trim() + " to M3",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        comment = root.findViewById(R.id.comment);
        ratingbar = root.findViewById(R.id.movie_memoir_scorerating);
        float a = ratingbar.getRating();

        mViewModel.getAllMemoirProcessing();
        mViewModel.getMemoirProcessing();
        add_memoir = root.findViewById(R.id.add_to_memoir_server);
        add_memoir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(comment.getText())) {
                        Toast.makeText(requireActivity(),
                                "The comment is required.",
                                Toast.LENGTH_SHORT).show();
                        optionPostcode.setError("The comment is required.");
                    }else if (ratingbar.getRating() == 0.0) {
                        Toast.makeText(requireActivity(),
                                "The rating score is required.",
                                Toast.LENGTH_SHORT).show();
                        optionPostcode.setError("The rating score is required.");
                    } else {
                        mViewModel.addMemoirProcessing(initMemoir());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        mViewModel.addMemoirResultLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer result) {
                if (result == 0) {
                    Toast.makeText(requireActivity(),
                            "Success Add memoir, " + movieview.getName() + " to M3",
                            Toast.LENGTH_LONG).show();
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.nav_moviememoir);
                } else {
                    Toast.makeText(requireActivity(),
                            "Fail add memoir, " + movieview.getName() + " to M3",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Memoir initMemoir() throws ParseException {
        SharedPreference sp = SharedPreference.getInstance(context);
        int id = sp.getInt("allMemoirTotalCount") + 1;
        Cinema cinema = new Cinema(Integer.parseInt(address.getSelectedItem().toString().trim().substring(0,1)));
        Person person = new Person(sp.getInt("personid"));
        Memoir memoir = new Memoir(id);
        memoir.setCinemaId(cinema);
        memoir.setPersonId(person);
        memoir.setMovieName(movieview.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = format.parse(movieview.getMovieReleaseDate());
        String date = sdf.format(releaseDate);
        memoir.setMovieReleaseDate(date);
        memoir.setWatchDateTime(watchDateString);
        memoir.setComments(comment.getText().toString().trim());
        memoir.setRatingScore((double)(Math.ceil(ratingbar.getRating())/2));
        return memoir;
    }
}
