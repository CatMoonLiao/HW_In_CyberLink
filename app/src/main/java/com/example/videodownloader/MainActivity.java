package com.example.videodownloader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Spinner title;
    FrameLayout frame;
    public static ArrayList<Movie> movies=new ArrayList<Movie>();
    ArrayList<String> titlelist=new ArrayList<String>();
    public static int position=0;
    public static Fragment download=new DownloadFragment();
    public static Fragment show=new ShowFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        title=findViewById(R.id.title_spinner);
        getTitleList();
        SpinnerAdapter adapter=new SpinnerAdapter(this,titlelist);
        title.setAdapter(adapter);
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position=i;
                selectFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        frame=findViewById(R.id.frame);


        selectFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(this);
    }
    private void checkPermission(Activity activity){

        int permission= ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1
            );
        }
        permission= ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission,
                                           int[] grantResults) {
        if(grantResults.length <= 0 ){
            Toast.makeText(this, "need your permission", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isvideoExisted(String source){
        File f=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                +"/"+source.substring(source.lastIndexOf('/')+1));
        return f.exists();

    }




    private void selectFragment(){

        FragmentManager fm=getSupportFragmentManager();
        if(isvideoExisted(movies.get(position).source)) {
            fm.beginTransaction().replace(R.id.frame, show).commit();
            fm.beginTransaction().hide(show).commit();
            fm.beginTransaction().show(show).commit();

        }else {
            fm.beginTransaction().replace(R.id.frame, download).commit();
        }
    }


    private void getTitleList(){
        try {
            InputStream fin=getResources().openRawResource(R.raw.videolist);
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(fin, StandardCharsets.UTF_8), 8);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
            fin.close();
            //phase JSON
            JSONObject json=new JSONObject(sb.toString());
            JSONArray ja=json.getJSONArray("categories");
            JSONArray videos=ja.getJSONObject(0).getJSONArray("videos");
            for(int i=0;i<videos.length();i++){
                Movie m=new Movie();
                JSONObject movie=videos.getJSONObject(i);
                m.title=movie.getString("title");
                m.subtitle=movie.getString("subtitle");
                m.description=movie.getString("description");
                m.source=movie.getJSONArray("sources").get(0).toString();
                m.source=m.source.replace("\\\\","");
                m.source=m.source.replace("http://","https://");

                int endindex=m.source.indexOf("sample")+7;
                m.img=m.source.substring(0,endindex)
                        +movie.getString("thumb").replace("\\\\","");

                movies.add(m);
                titlelist.add(m.title);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("read","讀檔失敗");
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("read","JSON error");
        }
    }





}