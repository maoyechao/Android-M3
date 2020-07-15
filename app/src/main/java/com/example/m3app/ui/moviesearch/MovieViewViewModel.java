package com.example.m3app.ui.moviesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.data.Movie;
import com.example.m3app.data.MovieDB;
import com.example.m3app.data.MovieInfo;
import com.example.m3app.data.movieFilestore;
import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.opencensus.tags.Tag;

import static com.example.m3app.data.Converters.fromTimestamp;

public class MovieViewViewModel extends ViewModel {
    private NetworkConnection networkConnection = new NetworkConnection();
    private MutableLiveData<List<String>> movieView;
    private Context context;
    private MutableLiveData<List<Movie>> movieListLiveData;
    private MutableLiveData<Integer> movieIDLiveData;
    private MutableLiveData<Bitmap> moviePosterLiveData;
    private MovieDB movieDB = null;
    private List<Movie> movieList;
    private List<MovieInfo> movieViewList;
    private int id = 0;
    private FirebaseFirestore moviesFirestore = FirebaseFirestore.getInstance();
    private List<Movie> movieFirestoreList;
    public static final String ID_KEY = "movieID";
    public static final String NAME_KEY = "movieName";
    public static final String RELEASE_KEY = "movieReleaseDate";
    public static final String WATCH_KEY = "movieWatchDate";

    public MovieViewViewModel () {
        movieView = new MutableLiveData<>();
        movieListLiveData = new MutableLiveData<>();
        movieIDLiveData = new MutableLiveData<>();
        movieViewList = new ArrayList<>();
        moviePosterLiveData = new MutableLiveData<>();
        movieFirestoreList = new ArrayList<>();
    }

    public void initMovieDB(Context context){
        movieDB = MovieDB.getInstance(context);
    }

    public LiveData<Bitmap> getPoster() {
        return moviePosterLiveData;
    }

    public LiveData<List<String>> getMovieVIEW() {
        return movieView;
    }

    public LiveData<List<Movie>> getMovieList() {
        return movieListLiveData;
    }

    public LiveData<Integer> movieIDMovieLiveData() {
        return movieIDLiveData;
    }


    public void getMovieIDProcessing(String movieName) {
        GetMovieTask getMovieTask = new GetMovieTask();
        getMovieTask.execute(movieName);
    }

    private class GetMovieTask extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getMovie(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            movieList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject jsonObject1 = new JSONObject(jsonArray.get(0).toString());
                movieIDLiveData.postValue(jsonObject1.getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getMovieVIEWProcessing(int movieID){
            getMovieViewTask getMovieViewTask = new getMovieViewTask();
            getMovieViewTask.execute(movieID);
    }

    private class getMovieViewTask extends AsyncTask<Integer, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Integer... params) {
            return networkConnection.getMovieView(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                SharedPreference sp = SharedPreference.getInstance(context);
                List<String> viewList = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(result);
                viewList.add(jsonObject.getString("original_title"));
                viewList.add(jsonObject.getString("release_date"));
                viewList.add(jsonObject.getString("vote_average"));

                JSONArray countryList = jsonObject.getJSONArray("production_countries");
                JSONObject country1 = new JSONObject(countryList.get(0).toString());
                viewList.add(country1.getString("name"));
                JSONArray genresList = jsonObject.getJSONArray("genres");
                if (genresList.length()>0) {
                    sp.putString("genres", genresList.getJSONObject(0).getString("name"));
                    for (int i = 1; i<genresList.length(); i++){
                        sp.putString("genres", sp.getString("genres") + "   " + genresList.getJSONObject(i).getString("name"));
                    }
                }
                viewList.add(sp.getString("genres"));
                JSONArray castList = jsonObject.getJSONObject("credits").getJSONArray("cast");
                if (castList.length()>0) {
                    sp.putString("casts", castList.getJSONObject(0).getString("name"));
                    if (castList.length()>1) {
                        sp.putString("casts", sp.getString("casts") + "   " + castList.getJSONObject(1).getString("name"));
                    }
                }
                viewList.add(sp.getString("casts"));
                JSONArray crewList = jsonObject.getJSONObject("credits").getJSONArray("crew");
                String director = "";
                for (int i = 0; i < crewList.length(); i++){
                    if (crewList.getJSONObject(i).getString("job").equals("Director")) {
                        director = director + "   " + crewList.getJSONObject(i).getString("name");
                    }
                }
                viewList.add(director);
                viewList.add(jsonObject.getString("overview"));
                movieView.postValue(viewList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public void insertMovie(Movie movie){
        movie.setUid(++id);
        Map<String, Object> movieDoc = new HashMap<>();
        movieDoc.put(ID_KEY,id);
        movieDoc.put(NAME_KEY, movie.getMovieName());
        movieDoc.put(RELEASE_KEY, movie.getReleaseDate());
        movieDoc.put(WATCH_KEY, movie.getWatchDate());
        moviesFirestore.document("movies/"+movie.getMovieName())
                .set(movieDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        InsertMovies insertMovies = new InsertMovies();
        insertMovies.execute(movie);
    }

    public void deleteMovieProcessing(int position) {
        deleteMovieTask deleteMovieTask = new deleteMovieTask();
        deleteMovieTask.execute(position);
    }

    public void readMovieList() {
        ReadMovieDatabase readMovieDatabase = new ReadMovieDatabase();
        readMovieDatabase.execute();
    }

//    private class ReadMovieDatabase extends AsyncTask<Void, Void, List<Movie>> {
//        @Override
//        protected List<Movie> doInBackground(Void... params) {
//            List<Movie> movies = movieDB.movieDAO().getAll();
//            String allMovies = "";
//            for (Movie temp : movies) {
//                String tempStr = (temp.getUid() + " " + temp.getMovieName() +
//                        " " + temp.getReleaseDate() + " " + temp.getWatchDate() + " , ");
//                allMovies = allMovies + System.getProperty("line.separator") +
//                        tempStr;
//                id = temp.getUid();
//            }
//            return movies;
//        }
//        @Override
//        protected void onPostExecute(List<Movie> movies) {
//            movieListLiveData.setValue(movies);
//        }
//    }

//    private class ReadMovieDatabase extends AsyncTask<Void, Void, List<Movie>> {
//        @Override
//        protected List<Movie> doInBackground(Void... params) {
//            List<Movie> movies = movieDB.movieDAO().getAll();
//            for (Movie temp : movies) {
//                id = temp.getUid();
//            }
//            return movies;
//        }
//        @Override
//        protected void onPostExecute(List<Movie> movies) {
//            movieFirestoreList = new ArrayList<>();
//            moviesFirestore.collection("movies")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    movieFilestore tempMovie = document.toObject(movieFilestore.class);
//                                    movieFirestoreList.add(new Movie(tempMovie.getMovieID().intValue(),tempMovie.getMovieName(),
//                                            tempMovie.getMovieReleaseDate(),tempMovie.getMovieWatchDate()));
//                                }
//                                movieListLiveData.postValue(movieFirestoreList);
//                            } else {
//                            }
//                        }
//                    });
//            //movieListLiveData.setValue(movies);
//        }
//    }

    private class ReadMovieDatabase extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... params) {
            List<Movie> movies = movieDB.movieDAO().getAll();
                for (Movie temp : movies) {
                    //The id is increase.
                    id = temp.getUid();
                }
                return movies;
        }
        @Override
        protected void onPostExecute(List<Movie> movies) {
            movieFirestoreList = new ArrayList<>();
            moviesFirestore.collection("movies")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    movieFilestore tempMovie = document.toObject(movieFilestore.class);
                                    movieFirestoreList.add(new Movie(tempMovie.getMovieID().intValue(),tempMovie.getMovieName(),
                                            tempMovie.getMovieReleaseDate(),tempMovie.getMovieWatchDate()));
                                }
                                addCouldMovieToRoomProcessing(movieFirestoreList);
                            } else {
                            }

                        }
                    });
        }
    }

    public void addCouldMovieToRoomProcessing(List<Movie> movies) {
        addCouldMovieToRoomTask addCouldMovieToRoom = new addCouldMovieToRoomTask();
        addCouldMovieToRoom.execute(movies);
    }

    private class addCouldMovieToRoomTask extends AsyncTask<List<Movie>, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(List<Movie>... params) {
            movieDB.movieDAO().deleteAll();
            for (Movie movie : params[0]) {
                movieDB.movieDAO().insert(movie);
            }
            return movieDB.movieDAO().getAll();
        }
        @Override
        protected void onPostExecute(List<Movie> movies) {
            movieListLiveData.setValue(movies);
        }
    }

    private class deleteMovieTask extends AsyncTask<Integer, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Integer... params) {
            moviesFirestore.collection("movies")
                    .document(movieDB.movieDAO().getAll().get(params[0]).getMovieName())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) { }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { }
                    });
            if (movieDB.movieDAO().getAll().size()>0) {
                movieDB.movieDAO().delete(movieDB.movieDAO().getAll().get(params[0]));
                id--;
            }
            return movieDB.movieDAO().getAll();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            movieListLiveData.setValue(movies);
        }
    }

    private class InsertMovies extends AsyncTask<Movie, Void, String> {
        @Override
        protected String doInBackground(Movie... params) {
            movieDB.movieDAO().insert(params[0]);
            movieList = movieDB.movieDAO().getAll();
            return "inserted";
        }

        @Override
        protected void onPostExecute(String result) {
            movieListLiveData.setValue(movieList);
        }
    }
    public void getImageProcessing(String movieName){
        watchlistGetImageTask getImageTask = new watchlistGetImageTask();
        getImageTask.execute(movieName);
    }

    private class watchlistGetImageTask extends AsyncTask<String, Void, Bitmap> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(String... params) {
            return networkConnection.getMemoirPooster(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            moviePosterLiveData.postValue(result);
        }
    }
}
