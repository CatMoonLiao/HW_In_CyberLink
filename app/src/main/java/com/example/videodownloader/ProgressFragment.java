package com.example.videodownloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;


public class ProgressFragment extends Fragment {
    int position;
    ArrayList<Movie> movies;
    ProgressBar progress;
    Button cancel;
    TextView statustxt;
    DownloadTask downloadTask;
    public ProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_progress, container, false);
        position=MainActivity.position;
        movies=MainActivity.movies;
        cancel=view.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downloadTask!=null){
                    AsyncTask asyncTask=downloadTask.downloadingTask;
                    if(asyncTask!=null && !asyncTask.isCancelled() && asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                        asyncTask.cancel(true);
                    }
                    downloadTask=null;
                }
            }
        });
        progress=view.findViewById(R.id.progressBar);
        progress.setMax(100);
        progress.setProgress(0);
        statustxt=view.findViewById(R.id.status);
        downloadFile();
        return view;
    }

    @Override
    public void onPause() {
        if(downloadTask!=null){
            AsyncTask asyncTask=downloadTask.downloadingTask;
            if(asyncTask!=null && !asyncTask.isCancelled() && asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
            }
            downloadTask=null;
        }
        super.onPause();
    }

    private void downloadFile(){
        Movie m=movies.get(position);
        downloadTask=new DownloadTask(m,progress,statustxt,getActivity(), Objects.requireNonNull(getContext()));
    }



    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}