package com.codecoy.securecalculator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.codecoy.securecalculator.TinyDB;
import com.codecoy.securecalculator.app.MainApplication;
import com.facebook.ads.NativeAdLayout;
import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SplashScreen extends AppCompatActivity {
    List<String> name_list;
    List<String> quantity_list;
    List<Integer> image_resource;
    List<Integer> image_background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        name_list=new ArrayList<>();
        quantity_list=new ArrayList<>();
        image_background=new ArrayList<>();
        image_resource=new ArrayList<>();

        NativeAdLayout native_ad_container = findViewById(R.id.nativeAdContainerId);
        LoadAds.getInstance(SplashScreen.this).loadFacebookNative(this, native_ad_container);

        int SPLASH_DISPLAY_LENGTH = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (MainApplication.getInstance().getPassword().equals("")) {

                    name_list.add("Pictures");
                    name_list.add("Videos");
                    name_list.add("Audios");

                    quantity_list.add("11");
                    quantity_list.add("11");
                    quantity_list.add("11");

                    image_background.add(R.drawable.circlebackgroundblue);
                    image_background.add(R.drawable.circleback2);
                    image_background.add(R.drawable.circleback1);

                    image_resource.add(R.drawable.ic_baseline_insert_photo_white);
                    image_resource.add(R.drawable.ic_baseline_video_white);
                    image_resource.add(R.drawable.ic_baseline_audio_file_white);



                    TinyDB tinyDB=new TinyDB(SplashScreen.this);
                    tinyDB.putListString("folder_name",name_list);
                    tinyDB.putListString("folder_quantity",quantity_list);
                    tinyDB.putListInt("folder_resource",image_resource);
                    tinyDB.putListInt("folder_background",image_background);
                    Intent mainIntent = new Intent(SplashScreen.this, OnBoardScreen.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }else {
                    Intent mainIntent = new Intent(SplashScreen.this, CalcActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}