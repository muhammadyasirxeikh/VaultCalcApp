package com.codecoy.securecalculator.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.codecoy.securecalculator.callbacks.OnAllAudiosLoadedListener;
import com.codecoy.securecalculator.callbacks.OnAllImagesLoadedListener;
import com.codecoy.securecalculator.callbacks.OnAllVideosLoadedListener;
import com.codecoy.securecalculator.callbacks.OnAudioLoadedListener;
import com.codecoy.securecalculator.callbacks.OnFilesLoadedListener;
import com.codecoy.securecalculator.callbacks.OnImagesLoadedListener;
import com.codecoy.securecalculator.callbacks.OnVideosLoadedListener;
//import com.safe.galleryigh.calculator.calligraphy.CalligraphyContextWrapper;
import com.codecoy.securecalculator.model.AllFilesModel;
import com.codecoy.securecalculator.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class BaseActivity extends AppCompatActivity {

    public class GetAllAudiosAsyncTask extends AsyncTask<Void, Void, ArrayList<Model>> {
        public OnAllAudiosLoadedListener onAllAudiosLoadedListener;

        protected ArrayList<Model> doInBackground(Void... voids) {
            ArrayList<Model> arrayList = new ArrayList();
            Cursor songCursor = BaseActivity.this.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (songCursor == null || !songCursor.moveToFirst()) {
                return arrayList;
            }
            int column_index = songCursor.getColumnIndex("_data");
            int last_modified = songCursor.getColumnIndex("date_modified");
            do {
                String currentPath = songCursor.getString(column_index);
                if (currentPath.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    arrayList.add(new Model(currentPath, (long) last_modified));
                }
            } while (songCursor.moveToNext());
            return arrayList;
        }

        protected void onPostExecute(ArrayList<Model> allAudioModels) {
            super.onPostExecute(allAudioModels);
            if (this.onAllAudiosLoadedListener != null) {
                this.onAllAudiosLoadedListener.onAllAudiosLoaded(allAudioModels);
            }
        }
    }

    public class GetAllImagesAsyncTask extends AsyncTask<Void, Void, ArrayList<Model>> {
        public OnAllImagesLoadedListener onAllImagesLoadedListener;

        protected ArrayList<Model> doInBackground(Void... voids) {
            Uri u = Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{"_data", "date_modified"};
            Cursor c = null;
            SortedSet<String> dirList = new TreeSet();
            ArrayList<Model> resultIAV = new ArrayList();
            String[] directories = null;
            if (u != null) {
                c = BaseActivity.this.getContentResolver().query(u, projection, null, null, null);
            }
            if (c != null && c.moveToFirst()) {
                do {
                    String tempDir = c.getString(0);
                    try {
                        dirList.add(tempDir.substring(0, tempDir.lastIndexOf("/")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
                directories = new String[dirList.size()];
                dirList.toArray(directories);
            }
            for (int i = 0; i < dirList.size(); i++) {
                File[] listFiles = new File(directories[i]).listFiles();
                if (listFiles != null) {
                    int length = listFiles.length;
                    int i2 = 0;
                    File[] imageList = listFiles;
                    while (i2 < length) {
                        File imagePath = listFiles[i2];
                        try {
                            if (imagePath.isDirectory()) {
                                imageList = imagePath.listFiles();
                            }
                            if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG") || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG") || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG") || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF") || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")) {
                                String path = imagePath.getAbsolutePath();
                                long lastModified = imagePath.lastModified();
                                if (path.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                                    resultIAV.add(new Model(path, lastModified));
                                }
                                i2++;
                            } else {
                                i2++;
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    listFiles = imageList;
                }
            }
            return resultIAV;
        }

        protected void onPostExecute(ArrayList<Model> allImageModels) {
            super.onPostExecute(allImageModels);
            if (this.onAllImagesLoadedListener != null) {
                this.onAllImagesLoadedListener.onAllImagesLoaded(allImageModels);
            }
        }
    }

    public class GetAllVideosAsyncTask extends AsyncTask<Void, Void, ArrayList<Model>> {

        public OnAllVideosLoadedListener onAllVideosLoadedListener;

        protected ArrayList<Model> doInBackground(Void... voids) {
            ArrayList<Model> videos = new ArrayList();
            Cursor cursor = BaseActivity.this.getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "date_modified"}, null, null, null);
            try {
                cursor.moveToFirst();
                do {
                    String path = cursor.getString(cursor.getColumnIndex("_data"));
                    String lastmodified = cursor.getString(cursor.getColumnIndex("_data"));
                    if (path.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                        videos.add(new Model(path, lastmodified));
                    }
                } while (cursor.moveToNext());
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return videos;
        }

        protected void onPostExecute(ArrayList<Model> allImages) {
            super.onPostExecute(allImages);
            if (this.onAllVideosLoadedListener != null) {
                this.onAllVideosLoadedListener.onAllVideosLoaded(allImages);
            }
        }
    }

    public class GetHiddenAudio extends AsyncTask<Void, Void, ArrayList<Model>> {

        public OnAudioLoadedListener onAudioLoadedListener;

        protected ArrayList<Model> doInBackground(Void... voids) {
            ArrayList<Model> resultIAV = new ArrayList();
            File file = new File(AppConstants.AUDIO_PATH);
            if (!file.exists()) {
                return null;
            }
            File[] imageList = file.listFiles();
            if (imageList == null) {
                return null;
            }
            for (File imagePath : imageList) {
                Log.e("PATH", "" + imagePath.getAbsolutePath());
                try {
                    resultIAV.add(new Model(imagePath.getAbsolutePath(), imagePath.lastModified()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resultIAV;
        }

        protected void onPostExecute(ArrayList<Model> allAudioModels) {
            super.onPostExecute(allAudioModels);
            if (this.onAudioLoadedListener != null) {
                this.onAudioLoadedListener.onAudiosLoaded(allAudioModels);
            }
        }
    }

    public class GetHiddenFiles extends AsyncTask<Void, Void, ArrayList<AllFilesModel>> {

        public OnFilesLoadedListener onFilesLoadedListener;

        protected ArrayList<AllFilesModel> doInBackground(Void... voids) {
            ArrayList<AllFilesModel> resultIAV = new ArrayList();
            File file = new File(AppConstants.FILES_PATH);
            if (!file.exists()) {
                return null;
            }
            File[] imageList = file.listFiles();
            if (imageList == null) {
                return null;
            }
            for (File imagePath : imageList) {
                Log.e("PATH", "" + imagePath.getAbsolutePath());
                try {
                    resultIAV.add(new AllFilesModel(imagePath.getAbsolutePath(), imagePath.lastModified()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resultIAV;
        }

        protected void onPostExecute(ArrayList<AllFilesModel> allFileModels) {
            super.onPostExecute(allFileModels);
            if (this.onFilesLoadedListener != null) {
                this.onFilesLoadedListener.onFilesLoaded(allFileModels);
            }
        }
    }

    public static class GetHiddenImages extends AsyncTask<Void, Void, ArrayList<Model>> {

        public OnImagesLoadedListener onImagesLoadedListener;
        public TextView count;
        String name;
        public GetHiddenImages(String folder_name) {
            this.name=folder_name;
        }



        protected ArrayList<Model> doInBackground(Void... voids) {
            ArrayList<Model> resultIAV = new ArrayList();
            File file = new File(AppConstants.IMAGE_PATH+name);
            if (!file.exists()) {
                return null;
            }
            File[] imageList = file.listFiles();

            if (imageList == null) {
                return null;
            }
            for (File imagePath : imageList) {
                Log.e("PATH", "" + imagePath.getAbsolutePath());
                try {
                    resultIAV.add(new Model(imagePath.getAbsolutePath(), imagePath.lastModified()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resultIAV;
        }

        protected void onPostExecute(ArrayList<Model> allImageModels) {
            super.onPostExecute(allImageModels);
            if (this.onImagesLoadedListener != null) {
                if (count!=null){
                    if (allImageModels!=null){
                        count.setText(allImageModels.size()+" files");
                    }else {
                        count.setText("0 files");
                    }

                }
                this.onImagesLoadedListener.onImagesLoaded(allImageModels);
            }
        }
    }



    public class GetHiddenVideos extends AsyncTask<Void, Void, ArrayList<Model>> {
        public OnVideosLoadedListener onVideosLoadedListener;

        protected ArrayList<Model> doInBackground(Void... voids) {
            ArrayList<Model> resultIAV = new ArrayList();
            File file = new File(AppConstants.VIDEO_PATH);
            if (!file.exists()) {
                return null;
            }
            File[] imageList = file.listFiles();
            if (imageList == null) {
                return null;
            }
            for (File imagePath : imageList) {
                Log.e("PATH", "" + imagePath.getAbsolutePath());
                try {
                    resultIAV.add(new Model(imagePath.getAbsolutePath(), imagePath.lastModified()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return resultIAV;
        }

        protected void onPostExecute(ArrayList<Model> allVideoModels) {
            super.onPostExecute(allVideoModels);
            if (this.onVideosLoadedListener != null) {
                this.onVideosLoadedListener.onVideosLoaded(allVideoModels);
            }
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
