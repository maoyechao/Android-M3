package com.example.m3app.networkconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.m3app.data.Cinema;
import com.example.m3app.data.Credential;
import com.example.m3app.data.Memoir;
import com.example.m3app.data.Person;
import com.example.m3app.ui.SharedPreference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkConnection {
    private Context context;
    private OkHttpClient client=null;
    public static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");
    public NetworkConnection(){
        client=new OkHttpClient();
    }
    private static final String BASE_URL =
            "http://192.168.0.104:18588/M3/webresources/";

    public String getPerson(String username, String password) {
        final String methodPath = "restm3.person/findPersonByUsernameAndPasswordHash/" + username + "/" + getSHA256StrJava(password);
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getEmails() {
        final String methodPath = "restm3.credential";
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getCinema() {
        final String methodPath = "restm3.cinema";
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getMemoir(String personid) {
        final String methodPath = "restm3.memoir/findMemoirByPersonID/"+ Integer.valueOf(personid);
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getAllMemoir() {
        final String methodPath = "restm3.memoir";
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getMemoirByStartEnd(String personid,String start,String end) {
        final String methodPath = "restm3.memoir/findCinemaPostcodeTotalMovieNumByPersonIDStartDateEndDate/"
                + Integer.valueOf(personid) + "/" + start + "/" + end;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getMemoirByYear(String personid,String year) {
        final String methodPath = "restm3.memoir/findMonthlyNumMoviesBypersonIDYear/"+ Integer.valueOf(personid) + "/" + year;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getCurrentYearTop5MoviesByPersonID(String personid) {
        SharedPreference sp = SharedPreference.getInstance(context);
        final String methodPath = "restm3.person/findTop5MoviesByPersonID/" + Integer.valueOf(personid);
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String addPerson(Person person) {
        Gson gson = new Gson();
        String personJson = gson.toJson(person);
        String strResponse = "";
        final String credentialMethodPath = "restm3.credential";
        final String personMethodPath = "restm3.person";
        try {
            JSONObject jsonObject = new JSONObject(personJson);
            RequestBody credentialBody = RequestBody.create(String.valueOf(jsonObject.getJSONObject("credentialid")), JSON);
            Request credentialRequest = new Request.Builder()
                    .url(BASE_URL + credentialMethodPath)
                    .post(credentialBody)
                    .build();
            Response credentialResponse = client.newCall(credentialRequest).execute();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        RequestBody personBody = RequestBody.create(personJson, JSON);
        Log.d("sign_up", "json: " + personJson);
        Request personRequest = new Request.Builder()
                .url(BASE_URL + personMethodPath)
                .post(personBody)
                .build();
        try {
            Response personResponse = client.newCall(personRequest).execute();
            if (personResponse.code() == 204) {
                return "204";
            }
            strResponse = personResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    public String addMemoir(Memoir memoir) {
        Gson gson = new Gson();
        String memoirJson = gson.toJson(memoir);
        String result = "";
        final String memoirMethodPath = "restm3.memoir";
        RequestBody memoirBody = RequestBody.create(memoirJson, JSON);
        Request memoirRequest = new Request.Builder()
                .url(BASE_URL + memoirMethodPath)
                .post(memoirBody)
                .build();
        try {
            Response memoirResponse = client.newCall(memoirRequest).execute();
            if (memoirResponse.code() == 204) {
                return "204";
            }
            result = memoirResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String addCinema(Cinema cinema) {
        Gson gson = new Gson();
        String cinemaJson = gson.toJson(cinema);
        String result = "";
        final String cinemaMethodPath = "restm3.cinema";
        RequestBody cinemaBody = RequestBody.create(cinemaJson, JSON);
        Request cinemaRequest = new Request.Builder()
                .url(BASE_URL + cinemaMethodPath)
                .post(cinemaBody)
                .build();
        try {
            Response cinemaResponse = client.newCall(cinemaRequest).execute();
            if (cinemaResponse.code() == 204) {
                return "204";
            }
            result = cinemaResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getLatLng(String address) {
        final String methodPath = "http://api.opencagedata.com/geocode/v1/json?q=" + address + "&key=452b578e138d471ca42283b6777f8ad7";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getMovie(String name) {
        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key=f4f936068eeb5b0f05bf5695b09db11b&query=" + name + "&page=1&include_adult=false";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Bitmap getMemoirPooster(String name) {
        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key=f4f936068eeb5b0f05bf5695b09db11b&query=" + name + "&page=1&include_adult=false";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        Bitmap bitmap = null;
        try {
            Response response = client.newCall(request).execute();
            JSONObject result = new JSONObject(response.body().string());
            JSONArray resultArray = result.getJSONArray("results");
            if (resultArray.length()>0){
                JSONObject firstAddObj = new JSONObject(resultArray.get(0).toString());
                String posterPath = firstAddObj.getString("poster_path");
                final String methodPath2 = "http://image.tmdb.org/t/p/w342" + posterPath;
                Request.Builder builder2 = new Request.Builder();
                builder2.url(methodPath2);
                Request request2 = builder2.build();
                Response response2 = client.newCall(request2).execute();
                InputStream results = Objects.requireNonNull(response2.body()).byteStream();
                bitmap = BitmapFactory.decodeStream(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getMemoirDetail(String name) {
        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key=f4f936068eeb5b0f05bf5695b09db11b&query=" + name + "&page=1&include_adult=false";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            JSONObject result = new JSONObject(response.body().string());
            JSONArray resultArray = result.getJSONArray("results");
            if (resultArray.length()>0){
                JSONObject firstAddObj = new JSONObject(resultArray.get(0).toString());
                int movieID = firstAddObj.getInt("id");
                final String methodPath1 = "https://api.themoviedb.org/3/movie/" + movieID + "?api_key=f4f936068eeb5b0f05bf5695b09db11b&append_to_response=credits";
                Request.Builder builder1 = new Request.Builder();
                builder1.url(methodPath1);
                Request request1 = builder1.build();
                Response response1 = client.newCall(request1).execute();
                results = response1.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public String getMovieView(Integer id) {
        final String methodPath = "https://api.themoviedb.org/3/movie/" + id + "?api_key=f4f936068eeb5b0f05bf5695b09db11b&append_to_response=credits";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        String results = "";
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Bitmap getMoviePoster(String posterPath) {
        final String methodPath = "http://image.tmdb.org/t/p/w342" + posterPath;
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        Bitmap bitmap = null;
        try {
            Response response = client.newCall(request).execute();
            InputStream results = Objects.requireNonNull(response.body()).byteStream();
            bitmap = BitmapFactory.decodeStream(results);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String returnValue = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            returnValue = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
