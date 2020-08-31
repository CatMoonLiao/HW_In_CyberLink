package com.example.videodownloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ShowFragment extends Fragment {

    ImageView imgview;
    TextView titletxt,subtitletxt,describetxt;
    int position;
    ArrayList<Movie> movies;
    String path;
    public ShowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show,container, false);
        imgview= view.findViewById(R.id.show_img);
        titletxt= view.findViewById(R.id.title_txt);
        subtitletxt= view.findViewById(R.id.subtitle_txt);
        describetxt= view.findViewById(R.id.discribe_txt);
        movies=MainActivity.movies;
        setUI();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setUI();
    }

    public void setUI(){
        position=MainActivity.position;
        Movie cur=movies.get(position);
        if(!isvideoExisted(cur.source)){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,MainActivity.download);
        }
        String filetype=cur.source.substring(cur.source.lastIndexOf('.')+1);
        path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                + "/" + cur.source.substring(cur.source.lastIndexOf('/')+1).replace(filetype,"png");
        titletxt.setText(cur.title);
        subtitletxt.setText(cur.subtitle);
        describetxt.setText(cur.description);
        imgDownloadTask task=new imgDownloadTask();
        task.execute();

    }
    private boolean isvideoExisted(String source){
        File f=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                +"/"+source.substring(source.lastIndexOf('/')+1));
        return f.exists();

    }


    private class imgDownloadTask extends AsyncTask<Void, Void, Void> {
        private Movie cur=movies.get(position);
        private Bitmap imgBitmap;
        @Override
        protected Void doInBackground(Void... voids) {


            imgBitmap=null;
            try {
                URL img = new URL(cur.img);
                HttpURLConnection c1 = (HttpURLConnection) img.openConnection();
                c1.setRequestMethod("GET");
                c1.connect();
                InputStream is = c1.getInputStream();//Get InputStream for connection
                imgBitmap=BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imgview.setImageBitmap(imgBitmap);
        }
    }
}