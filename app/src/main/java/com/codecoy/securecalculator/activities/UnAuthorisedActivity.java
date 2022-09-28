package com.codecoy.securecalculator.activities;

import android.app.Dialog;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.facebook.ads.NativeAdLayout;
import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.share.Share;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Environment.DIRECTORY_PICTURES;

public class UnAuthorisedActivity extends AppCompatActivity {

    Dialog dialog;
    ImageView ic_back;

    public static ArrayList<File> al_my_photos = new ArrayList<>();
    private File[] allFiles;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_authorised);

        NativeAdLayout nativeAdLayoutcontainder=findViewById(R.id.native_banner_ad_container);
        LoadAds.getInstance(this).loadFacebookNativeBanner(this,nativeAdLayoutcontainder);

        findViews();
        initViews();

    }

    private void findViews() {

        ic_back = findViewById(R.id.ic_back);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void initViews() {

        al_my_photos.clear();
        Share.al_my_photos_photo.clear();
        File path = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + "/" + ".Calculator Vault" + "/");

        if (path.exists()) {

            allFiles = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });

            if (allFiles.length > 0) {

                for (int i = 0; i < allFiles.length; i++) {

                    al_my_photos.add(allFiles[i]);

                }
                Collections.sort(al_my_photos, Collections.reverseOrder());
                Share.al_my_photos_photo.addAll(al_my_photos);
                Collections.reverse(Share.al_my_photos_photo);

                Adapter_ImageFolder obj_adapter = new Adapter_ImageFolder(getApplicationContext(), Share.al_my_photos_photo, new Adapter_ImageFolder.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        try {

                            dialog = new Dialog(UnAuthorisedActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dlg_exit1);
                            dialog.getWindow().setLayout((int) (DisplayMetricsHandler.getScreenWidth() - 50), Toolbar.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            ImageView img, close;

                            img = dialog.findViewById(R.id.img);
                            close = dialog.findViewById(R.id.close);
                            img.setImageURI(Uri.fromFile(Share.al_my_photos_photo.get(position)));

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();

                                }
                            });

                        } catch (Exception e) {
                            e.getMessage();
                        }

                    }
                });

                recyclerView.setAdapter(obj_adapter);

            } else {


            }
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {

        if (dialog != null) {

            dialog.cancel();

        }
        super.onDestroy();
    }

}
