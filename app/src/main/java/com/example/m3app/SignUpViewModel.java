package com.example.m3app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.m3app.data.Person;
import com.example.m3app.networkconnection.NetworkConnection;
import com.example.m3app.ui.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;

public class SignUpViewModel extends ViewModel {
    NetworkConnection networkConnection = new NetworkConnection();
    //Person person = new Person(0);
    private MutableLiveData<Integer> addPersonResult;
    private Context context;

    public SignUpViewModel() {
        addPersonResult = new MutableLiveData<>();
    }

    public void initContext(Context context) {
        this.context = context;
    }

    public LiveData<Integer> getAddPersonResult() {
        return addPersonResult;
    }

    public void getEmailProcess() {
            getEmailProcessTask getEmailTask = new getEmailProcessTask();
            getEmailTask.execute();
    }

    private class getEmailProcessTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getEmails();
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreference sp = SharedPreference.getInstance(context);
            try {
                JSONArray resultArray = new JSONArray(result);
                if (resultArray.length()>0) {
                    sp.putInt("emailTotalCount", resultArray.length());
                    for (int i = 0; i < resultArray.length(); i++) {
                        sp.putString("email" + i, resultArray.get(i).toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public void signUpProcessing(Person person){
        try {
            AddPersonTask addPersonTask = new AddPersonTask();
            addPersonTask.execute(person);
        } catch (Exception e) {
            addPersonResult.setValue(1);
        }
    }

    private class AddPersonTask extends AsyncTask<Person, Void, String> {
        @Override
        protected String doInBackground(Person... params) {
            return networkConnection.addPerson(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("204"))
                addPersonResult.setValue(0);
            else
                addPersonResult.setValue(1);
            Log.d("sign_up",result);
        }
    }
}
