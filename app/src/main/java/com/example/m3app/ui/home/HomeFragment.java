package com.example.m3app.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.m3app.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public HomeFragment(){

    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView1 = root.findViewById(R.id.text_welcome);
        homeViewModel.getPersonResultLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String text) {
                textView1.setText(text);
            }
        });
        final TextView textView2 = root.findViewById(R.id.text_top5_movies);
        homeViewModel.getGetMoviesResultLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String text) {
                textView2.setText(text);
            }
        });

        return root;
    }
}
