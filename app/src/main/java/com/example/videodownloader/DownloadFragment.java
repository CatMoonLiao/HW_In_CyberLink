package com.example.videodownloader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;


public class DownloadFragment extends Fragment {
    int position;
    ArrayList<Movie> movies;
    ImageButton downloadbtn;


    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_download, container, false);
        position=MainActivity.position;
        movies=MainActivity.movies;
        downloadbtn=view.findViewById(R.id.download_btn);
        setListener();
        return view;
    }

    private void setListener(){
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download

            FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame,new ProgressFragment());
            ft.addToBackStack(null);
            ft.commit();

            }
        });
    }

}