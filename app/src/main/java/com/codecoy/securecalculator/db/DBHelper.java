package com.codecoy.securecalculator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codecoy.securecalculator.model.Model;
import com.codecoy.securecalculator.model.AllVideosModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String KEY_NEW_PATH = "new_path";
    private static final String KEY_OLD_PATH = "old_path";
    private static final String TABLE_IMAGE = "image_table";
    private static final String TABLE_VIDEO = "video_table";
    Context context;

    public DBHelper(Context context) {
        super(context, "Calculator", null, 1);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE image_table(_id INTEGER PRIMARY KEY AUTOINCREMENT,old_path TEXT,new_path TEXT)");
        db.execSQL("CREATE TABLE video_table(_id INTEGER PRIMARY KEY AUTOINCREMENT,old_path TEXT,new_path TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS image_table");
        db.execSQL("DROP TABLE IF EXISTS video_table");
        onCreate(db);
    }

    public void addImage(Model images) {
        Log.e("addImage", "Operation Started");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_OLD_PATH, images.getPath());
        contentValues.put(KEY_NEW_PATH, images.getNewPath());
        db.insertWithOnConflict(TABLE_IMAGE, null, contentValues, 5);
        db.close();
        Log.e("addImage", "Operation Completed");
    }

    public ArrayList<Model> getImages() {
        ArrayList<Model> images = new ArrayList();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM image_table", null);
        if (cursor.moveToFirst()) {
            do {
                Model Model = new Model();
                Model.setId(Integer.parseInt(cursor.getString(0)));
                Model.setPath(cursor.getString(1));
                Model.setNewPath(cursor.getString(2));
                images.add(Model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return images;
    }

    public void deleteImage(long id) {
        String selectQuery = "DELETE FROM image_table WHERE _id=" + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public int getImageCount() {
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_IMAGE);
    }

    public void addVideo(AllVideosModel videos) {
        Log.e("addVideo", "Operation Started");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_OLD_PATH, videos.getOldPath());
        contentValues.put(KEY_NEW_PATH, videos.getNewPath());
        db.insertWithOnConflict(TABLE_VIDEO, null, contentValues, 5);
        db.close();
        Log.e("addVideo", "Operation Completed");
    }

    public ArrayList<AllVideosModel> getVideos() {
        ArrayList<AllVideosModel> videos = new ArrayList();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM video_table", null);
        if (cursor.moveToFirst()) {
            do {
                AllVideosModel allVideosModel = new AllVideosModel();
                allVideosModel.setVideoId(Integer.parseInt(cursor.getString(0)));
                allVideosModel.setOldPath(cursor.getString(1));
                allVideosModel.setNewPath(cursor.getString(2));
                videos.add(allVideosModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return videos;
    }

    public void deleteVideo(long id) {
        String selectQuery = "DELETE FROM video_table WHERE _id=" + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }

    public int getVideoCount() {
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_VIDEO);
    }
}
