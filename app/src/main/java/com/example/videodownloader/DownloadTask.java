package com.example.videodownloader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private String downloadUrl = "", downloadFileName = "";
    private ProgressBar pb;
    private TextView tx;
    private FragmentActivity a;
    private ContentResolver contentResolver;
    AsyncTask downloadingTask;
    private File videoFile;
    private ContentValues contentValues;
    private Uri videoplace,movieDir;
    public DownloadTask(Movie m, ProgressBar pb, TextView tx, FragmentActivity a,Context context) {
        this.downloadUrl = m.source;
        this.pb=pb;
        this.tx=tx;
        this.a=a;

        this.contentResolver=context.getContentResolver();
        downloadFileName = m.source.substring(m.source.lastIndexOf('/')+1);

        //Start Downloading Task
        downloadingTask=new DownloadingTask().execute();
    }

    private OutputStream getOutputStream() throws FileNotFoundException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = contentResolver;
            contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,
                    downloadFileName.replace(downloadFileName.substring(downloadFileName.lastIndexOf('.')+1),"tmp"));
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/tmp");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 1);
            //取存取地點的名字
            movieDir = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            videoplace = resolver.insert(movieDir, contentValues);//存檔
            OutputStream os=null;
            if (videoplace != null) {
                os = resolver.openOutputStream(videoplace);
            }
            contentValues.clear();
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
            resolver.update(videoplace, contentValues, null, null);
            return os;
        }
        //old version
        else {
            videoFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString());
            boolean success = true;
            if (!videoFile.exists()) {
                success = videoFile.mkdirs();
            }
            if (success) {
                videoFile = new File(videoFile,
                        downloadFileName.replace(downloadFileName.substring(downloadFileName.lastIndexOf('.')+1),"tmp"));
                return new FileOutputStream(videoFile);
            }
        }
        return null;
    }
    private class DownloadingTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL video=new URL(downloadUrl);
                HttpURLConnection c = (HttpURLConnection) video.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("download", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                OutputStream os = getOutputStream();
                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                Integer len1 = 0;//init length
                Double total=(double)0;
                Double contentLength = (double)c.getContentLength();

                while ((len1 = is.read(buffer)) != -1) {
                    if(isCancelled()){
                        return null;
                    }
                    os.write(buffer, 0, len1);//Write new file
                    total+=len1;
                    publishProgress((int) (100 * (total / contentLength)));
                }



                //Close all connection after doing task
                os.close();
                is.close();


            } catch (IOException e) {
                e.printStackTrace();

                Log.e("download", "Download Error Exception " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values!=null){
                pb.setProgress(values[0]);
                tx.setText("Downloading..."+values[0].toString()+"%");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear();
                contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME,downloadFileName);
                String extension=downloadFileName.substring(downloadFileName.lastIndexOf('.')+1);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/"+extension);
                contentResolver.update(videoplace,contentValues,null,null);

            }else{
                File newfile=new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString(),downloadFileName);
                videoFile.renameTo(newfile);
            }
            a.getSupportFragmentManager().beginTransaction().replace(R.id.frame,MainActivity.show).commit();

        }

        @Override
        protected void onCancelled() {
            cancelwork();
            super.onCancelled();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            cancelwork();
        }

        private void cancelwork(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                contentResolver.delete(videoplace,null,null);
            }
            else{
                if(videoFile!=null && videoFile.exists()){
                    videoFile.delete();
                }
            }
            Toast.makeText(a.getApplicationContext(),"下載取消",Toast.LENGTH_SHORT).show();
            a.getSupportFragmentManager().beginTransaction().replace(R.id.frame,MainActivity.download).commit();
        }
    }

}
