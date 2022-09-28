package com.codecoy.securecalculator.image;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Files;

import com.codecoy.securecalculator.album.AlbumsFragment;
import com.codecoy.securecalculator.audios.AudiosActivity;
import com.codecoy.securecalculator.audios.add.AddAudiosActivity;
import com.codecoy.securecalculator.callbacks.ImageCallBack;
import com.codecoy.securecalculator.callbacks.OnVideosLoadedListener;
import com.codecoy.securecalculator.files.FileSelectionActivity;
import com.codecoy.securecalculator.files.FilesActivity;
import com.codecoy.securecalculator.image.adapter.SliderAdapter;
import com.codecoy.securecalculator.model.Model;
import com.codecoy.securecalculator.video.VideoActivity;
import com.codecoy.securecalculator.video.add.AddVideoActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.app.AppConstants;
import com.codecoy.securecalculator.app.BaseActivity;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.callbacks.OnImagesLoadedListener;
import com.codecoy.securecalculator.db.DBHelper;
import com.codecoy.securecalculator.fullscreenimage.FullScreenImageActivity;
import com.codecoy.securecalculator.image.adapter.ImagesAdapter;
import com.codecoy.securecalculator.image.add.AddImageActivity;
import com.codecoy.securecalculator.model.AllImagesModel;
import com.codecoy.securecalculator.utils.CenterTitleToolbar;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImagesActivity extends BaseActivity implements OnImagesLoadedListener, OnVideosLoadedListener, ImageCallBack {

    String TAG = "TAG";
    private LinearLayout adView;
    private ImagesAdapter adapter;
    List<Model> images_list;
    LinearLayout bannerContainer;

    @BindView(R.id.btn_unhide)
    Button btnUnhide;



    private int count;
    DBHelper dbHelper;
    private Dialog dialog;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    private boolean isEditable;
    private boolean isFileCopied = true;
    private boolean isSelectAll;
    public MenuItem menuItemDelete;
    public MenuItem menuItemUnlock;
    public MenuItem menuItemEdit;
    public MenuItem menuItemSelect;
    private LinearLayout nativeAdContainer;
    private int progress;
    private ProgressDialog progressDialog;
    private ProgressBar progressbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /* renamed from: t */
    private Timer f16t;
    @BindView(R.id.toolbar)
    CenterTitleToolbar toolbar;
    private TextView txtCount;
    @BindView(R.id.txt_error)
    TextView txtError;

    @BindView(R.id.playback)
    ImageView img;



    @BindView(R.id.viewanimator)
    ViewAnimator viewanimator;
    ImageView front_image,image_icon;
    String  folder_name;
    View viewblack;
    TextView folder_name_tv;
    SliderAdapter sliderAdapter;
    SliderView slider;
    private FloatingActionMenu floatingActionMenuId;
    private com.github.clans.fab.FloatingActionButton pictureFloatingButton, audioFloatingButton, videoFloatingButton, fileFloatingButton, cameraFloatingButton;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_image);
        ButterKnife.bind((Activity) this);
        this.dbHelper = new DBHelper(this);
        setHeaderInfo();
        folder_name=getIntent().getStringExtra("folder_name");
        folder_name_tv = findViewById(R.id.folder_name_tv);
        folder_name_tv.setText(folder_name);

        images_list=new ArrayList<>();
        slider=findViewById(R.id.slider);


        NativeAdLayout native_banner = findViewById(R.id.native_banner);
        LoadAds.getInstance(ImagesActivity.this).loadFacebookNativeBanner(ImagesActivity.this, native_banner);

        Init();



    }

    @Override
    public void onLongClick(int pos) {
        selectpicture(pos);

    }


    class C05982 implements Runnable {
        C05982() {
        }

        public void run() {
            ImagesActivity.this.progressbar.setProgress(ImagesActivity.this.progress);
        }
    }


    private void setHeaderInfo() {
        // this.toolbar.setNavigationIcon((int) R.drawable.ic_arrow);

        this.toolbar.bringToFront();
        setSupportActionBar(this.toolbar);

//        if (getSupportActionBar() != null) {
//            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow);
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
//            newdrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
//
//        }
    }

    private void Init() {
        // LoadBannerAd();
        this.recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
//        selectpicture();


        findViews();
    }

    public void selectpicture(int pos) {
        Model model=new Model();
        this.isEditable = true;
        model.setSelected(true);
       this.adapter.notifyDataSetChanged();



        if (this.menuItemSelect != null) {
            this.menuItemSelect.setVisible(true);
        }
        if (this.adapter != null) {
            this.adapter.isItemEditable(true);
        }
        if (getSupportActionBar() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        }
    }

    private void getpath()
    {
//        ..........
        AppConstants.folder_name="folder_name";
        File file = new File(AppConstants.IMAGE_PATH+folder_name);
        if (!file.exists()) {
            file.mkdir();
        }else{
            File[] imageList = file.listFiles();
            if (imageList == null) {

            }
            for (File imagePath : imageList) {
                Log.e("PATH", "" + imagePath.getAbsolutePath());

                image_icon.setVisibility(View.GONE);
                front_image.setImageURI(Uri.parse(imagePath.getAbsolutePath()));
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        }


    }
    private void getsliderimages() {

//        int[] images={R.drawable.pic1,R.drawable.samplepic,R.drawable.pic1};

//        mViewPagerAdapter = new ViewPagerAdapter(requireContext(), images);
        sliderAdapter = new SliderAdapter(this, images_list);
//start
//        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);



        // below method is used to
        // setadapter to sliderview.
        slider.setSliderAdapter(sliderAdapter);


        // below method is use to set
        // scroll time in seconds.
        slider.setScrollTimeInSec(3);

        // to set it scrollable automatically
        // we use below method.
        slider.setAutoCycle(true);

        // to start autocycle below method is used.
        slider.startAutoCycle();

//end
    }
    private void setAdapter() {
        MainApplication.getInstance().LogFirebaseEvent("3", "AddImage");
        this.adapter = new ImagesAdapter(this,this);
        this.recyclerview.setAdapter(this.adapter);
        front_image = findViewById(R.id.front_image);
        image_icon = findViewById(R.id.image_icon);
        viewblack = findViewById(R.id.view);



        getpath();

        Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
        newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        GetHiddenImages task = new GetHiddenImages(folder_name);

        task.onImagesLoadedListener = this;
        task.execute(new Void[0]);
    }

    private void findViews() {

       

        floatingActionMenuId = findViewById(R.id.floatingActionMenuId);

        floatingActionMenuId.setMenuButtonColorPressed(R.color.colorAccent);


        floatingActionMenuId.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened){


                    viewblack.setVisibility(View.VISIBLE);
                    viewblack.bringToFront();
                    floatingActionMenuId.bringToFront();

//                    main_layout.bringToFront();
//                    main_layout.setBackgroundResource(R.color.black_dull);
                }else {
                    viewblack.setVisibility(View.GONE);
//                    main_layout.bringToFront();
//                    main_layout.setBackgroundResource(R.color.white);
                }
            }
        });


//        floatingActionMenuId.setOnMenuButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(requireContext(), "asdjk", Toast.LENGTH_SHORT).show();
//               main_layout.setBackgroundResource(R.color.black_dull);
//
//            }
//        });
        pictureFloatingButton = findViewById(R.id.picturesFloatingActionButtonId);
        audioFloatingButton = findViewById(R.id.audioFloatingActionButtonId);
        videoFloatingButton = findViewById(R.id.videoFloatingActionButtonId);
        fileFloatingButton = findViewById(R.id.fileFloatingActionButtonId);
        cameraFloatingButton = findViewById(R.id.cameraFloatingActionButtonId);

       
        pictureFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
                    LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(ImagesActivity.this, AddImageActivity.class).putExtra("folder_name",folder_name), 1012);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            return;
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });
                } else {
                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(ImagesActivity.this, AddImageActivity.class).putExtra("folder_name",folder_name), 1012);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    return;
                }

            }
        });
        audioFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
                    LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(ImagesActivity.this, AddAudiosActivity.class).putExtra("folder_name",folder_name), 1012);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            return;
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });
                } else {
                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(ImagesActivity.this, AddAudiosActivity.class).putExtra("folder_name",folder_name), 1012);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    return;
                }


            }
        });
        videoFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
                    LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(ImagesActivity.this, AddVideoActivity.class).putExtra("folder_name",folder_name), 1012);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            return;


                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });
                } else {
                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(ImagesActivity.this, AddVideoActivity.class).putExtra("folder_name",folder_name), 1012);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    return;
                }


            }
        });
        fileFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
                    LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(ImagesActivity.this, FileSelectionActivity.class).putExtra("folder_name",folder_name), 1012);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            return;

                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });
                } else {
                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(ImagesActivity.this, FileSelectionActivity.class).putExtra("folder_name",folder_name), 1012);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    return;


                }

            }
        });



        this.img.setOnClickListener(v->{
            slider.setVisibility(View.VISIBLE);
            slider.bringToFront();
            getsliderimages();

        });



    }


    @OnClick({R.id.fab_add, R.id.btn_unhide})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unhide:
                recoverFiles();
                return;
            case R.id.fab_add:


                if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
                    LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(ImagesActivity.this, AddImageActivity.class).putExtra("folder_name",folder_name), 1012);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                            return;
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });
                } else {
                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(this, AddImageActivity.class).putExtra("folder_name",folder_name), 1012);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    return;
                }


                return;

            default:
                return;
        }
    }

    private void recoverFiles() {




        if (LoadAds.getInstance(ImagesActivity.this).isFacebookInterstitialLoaded()) {
            LoadAds.getInstance(ImagesActivity.this).showFacebookInterstital();
            LoadAds.getInstance(ImagesActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();

                    if (ImagesActivity.this.adapter != null) {
                        final List<String> selectedFiles = ImagesActivity.this.adapter.getSelectedImages();
                        if (selectedFiles == null || selectedFiles.size() <= 0) {
                            Toast.makeText(ImagesActivity.this, "Please select at least one image!", 0).show();
                            return;
                        }
                        showProgressDialog(selectedFiles);
                        ImagesActivity.this.f16t = new Timer();
                        ImagesActivity.this.f16t.scheduleAtFixedRate(new TimerTask() {

                            class C05951 implements Runnable {
                                C05951() {
                                }

                                public void run() {
                                    ImagesActivity.this.publishProgress(selectedFiles.size());
                                }
                            }

                            class C05962 implements Runnable {
                                C05962() {
                                }

                                public void run() {
                                    ImagesActivity.this.hideProgressDialog();
                                    ImagesActivity.this.btnUnhide.setVisibility(8);
                                    if (ImagesActivity.this.menuItemEdit != null) {
                                        ImagesActivity.this.menuItemEdit.setVisible(true);
                                    }
                                    if (ImagesActivity.this.menuItemSelect != null) {
                                        ImagesActivity.this.menuItemSelect.setVisible(false);
                                        ImagesActivity.this.menuItemSelect.setIcon(R.drawable.ic_check_box_outline);
                                    }
                                    if (ImagesActivity.this.menuItemDelete != null) {
                                        ImagesActivity.this.menuItemDelete.setVisible(false);
                                    } if (ImagesActivity.this.menuItemUnlock != null) {
                                        ImagesActivity.this.menuItemUnlock.setVisible(false);
                                    }

                                    ImagesActivity.this.isEditable = false;
                                    if (getSupportActionBar() != null) {
                                        Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow);
                                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
                                        newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                        getSupportActionBar().setHomeAsUpIndicator(newdrawable);

                                    }
                                    //ImagesActivity.this.toolbar.setNavigationIcon((int) R.drawable.ic_arrow);
                                    ImagesActivity.this.setAdapter();
                                }
                            }

                            public void run() {
                                if (ImagesActivity.this.count == selectedFiles.size()) {
                                    ImagesActivity.this.f16t.cancel();
                                    ImagesActivity.this.count = 0;
                                    ImagesActivity.this.runOnUiThread(new C05962());
                                } else if (ImagesActivity.this.isFileCopied) {
                                    ImagesActivity.this.runOnUiThread(new C05951());
                                    ImagesActivity.this.isFileCopied = false;
                                    File src = new File((String) selectedFiles.get(ImagesActivity.this.count));
                                    File file = new File(AppConstants.IMAGE_EXPORT_PATH);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    ImagesActivity.this.moveFile(src, new File(AppConstants.IMAGE_EXPORT_PATH, src.getName()));
                                    ImagesActivity.this.count = ImagesActivity.this.count + 1;
                                }
                            }
                        }, 0, 200);
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
        } else {
            LoadAds.getInstance(ImagesActivity.this).reloadfacebookInterstitial();

            if (this.adapter != null) {
                final List<String> selectedFiles = this.adapter.getSelectedImages();
                if (selectedFiles == null || selectedFiles.size() <= 0) {
                    Toast.makeText(this, "Please select at least one image!", 0).show();
                    return;
                }
                showProgressDialog(selectedFiles);
                this.f16t = new Timer();
                this.f16t.scheduleAtFixedRate(new TimerTask() {

                    class C05951 implements Runnable {
                        C05951() {
                        }

                        public void run() {
                            ImagesActivity.this.publishProgress(selectedFiles.size());
                        }
                    }

                    class C05962 implements Runnable {
                        C05962() {
                        }

                        public void run() {
                            ImagesActivity.this.hideProgressDialog();
                            ImagesActivity.this.btnUnhide.setVisibility(8);
                            if (ImagesActivity.this.menuItemEdit != null) {
                                ImagesActivity.this.menuItemEdit.setVisible(true);
                            }
                            if (ImagesActivity.this.menuItemSelect != null) {
                                ImagesActivity.this.menuItemSelect.setVisible(false);
                                ImagesActivity.this.menuItemSelect.setIcon(R.drawable.ic_check_box_outline);
                            }
                            if (ImagesActivity.this.menuItemDelete != null) {
                                ImagesActivity.this.menuItemDelete.setVisible(false);
                            }
                            if (ImagesActivity.this.menuItemUnlock != null) {
                                ImagesActivity.this.menuItemUnlock.setVisible(false);
                            }
                            ImagesActivity.this.isEditable = false;
                            if (getSupportActionBar() != null) {
                                Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow);
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
                                newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                getSupportActionBar().setHomeAsUpIndicator(newdrawable);

                            }
                            //ImagesActivity.this.toolbar.setNavigationIcon((int) R.drawable.ic_arrow);
                            ImagesActivity.this.setAdapter();
                        }
                    }

                    public void run() {
                        if (ImagesActivity.this.count == selectedFiles.size()) {
                            ImagesActivity.this.f16t.cancel();
                            ImagesActivity.this.count = 0;
                            ImagesActivity.this.runOnUiThread(new C05962());
                        } else if (ImagesActivity.this.isFileCopied) {
                            ImagesActivity.this.runOnUiThread(new C05951());
                            ImagesActivity.this.isFileCopied = false;
                            File src = new File((String) selectedFiles.get(ImagesActivity.this.count));
                            File file = new File(AppConstants.IMAGE_EXPORT_PATH);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            ImagesActivity.this.moveFile(src, new File(AppConstants.IMAGE_EXPORT_PATH, src.getName()));
                            ImagesActivity.this.count = ImagesActivity.this.count + 1;
                        }
                    }
                }, 0, 200);
            }
        }



    }

    private void showProgressDialog(List<String> files) {
        this.dialog = new Dialog(this);
        this.dialog.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.dialog_progress);
        if (this.dialog.getWindow() != null) {
            this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            this.dialog.getWindow().setLayout(-1, -2);
        }
        this.progressbar = (ProgressBar) this.dialog.findViewById(R.id.progress_bar);
        this.txtCount = (TextView) this.dialog.findViewById(R.id.txt_count);
        this.nativeAdContainer = (LinearLayout) this.dialog.findViewById(R.id.native_ad_container);
        ((TextView) this.dialog.findViewById(R.id.txt_title)).setText("Moving Image(s)");
        this.txtCount.setText("Moving 1 of " + files.size());
        int totalFileSize = 0;
        for (String ss : files) {
            totalFileSize += (int) new File(ss).length();
        }
        this.progressbar.setMax(totalFileSize);
        this.dialog.show();
    }

    private void hideProgressDialog() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    private void publishProgress(int size) {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.txtCount.setText("Moving " + (this.count + 1) + " of " + size);
        }
    }

    private void moveFile(final File src, final File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out;
            try {
                out = new FileOutputStream(dst);
                byte[] buf = new byte[1024];
                while (true) {
                    int len = in.read(buf);
                    if (len > 0) {
                        out.write(buf, 0, len);
                        this.progress += len;
                        runOnUiThread(new C05982());
                    } else {
                        out.close();
                        this.isFileCopied = true;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ImagesActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(dst)));
                            }
                        });
                        in.close();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ImagesActivity.this.deleteFilePath(src);
                            }
                        });
                        this.isFileCopied = true;
                        return;
                    }
                }
            } catch (Throwable th) {
                in.close();
                runOnUiThread(/* anonymous class already generated */);
                this.isFileCopied = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.isFileCopied = true;
        }
    }

    private void runOnUiThread() {
    }

    private void deleteFilePath(File file) {
        try {
            String where = "_data=?";
            String[] selectionArgs = new String[]{file.getAbsolutePath()};
            ContentResolver contentResolver = getContentResolver();
            Uri filesUri = Files.getContentUri("external");
            contentResolver.delete(filesUri, "_data=?", selectionArgs);
            if (file.exists()) {
                contentResolver.delete(filesUri, "_data=?", selectionArgs);
                file.delete();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), 1).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1012 && resultCode == -1) {
            this.viewanimator.setDisplayedChild(0);
            setAdapter();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        this.menuItemSelect = menu.findItem(R.id.itm_select);
        this.menuItemDelete = menu.findItem(R.id.itm_delete);
        this.menuItemUnlock=menu.findItem(R.id.itm_unlock);
        this.menuItemEdit = menu.findItem(R.id.itm_edit);
        setAdapter();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.itm_unlock:
                recoverFiles();
            case R.id.itm_delete:
                final AlertDialog alertDialog = new Builder(this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure to delete selected files?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(-1, (CharSequence) "Yes", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ImagesActivity.this.deleteSelectedFiles();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(-2, (CharSequence) "No", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.itm_edit:
                this.isEditable = true;
                this.img.setVisibility(View.GONE);
                item.setVisible(false);
                if (this.menuItemSelect != null) {
                    this.menuItemSelect.setVisible(true);
                }
                if (this.adapter != null) {
                    this.adapter.isItemEditable(true);
                }
                if (getSupportActionBar() != null) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
                    newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(newdrawable);

                }
                // this.toolbar.setNavigationIcon((int) R.drawable.ic_close);
                break;
            case R.id.itm_select:
                if (this.menuItemSelect != null) {
                    if (!this.isSelectAll) {
                        this.menuItemSelect.setIcon(R.drawable.ic_check_filled);
                        if (this.adapter != null) {
                            this.adapter.selectAllItem();
                        }
                        showDeleteButton(true);
                        this.isSelectAll = true;
                        break;
                    }
                    this.menuItemSelect.setIcon(R.drawable.ic_check_box_outline);
                    if (this.adapter != null) {
                        this.adapter.deSelectAllItem();
                    }
                    showDeleteButton(false);
                    this.isSelectAll = false;
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedFiles() {
        if (this.adapter != null) {
            List<String> selectedFiles = this.adapter.getSelectedImagePaths();
            if (selectedFiles != null && selectedFiles.size() > 0) {
                for (String selectedFile : selectedFiles) {
                    new File(selectedFile).delete();
                }
            }
            showDeleteButton(false);
            setAdapter();
        }
    }

    public void showDeleteButton(boolean needToshow) {
        if (this.menuItemDelete != null) {
            this.menuItemDelete.setVisible(needToshow);
        }
        if (this.menuItemUnlock != null) {
            this.menuItemUnlock.setVisible(needToshow);
        }

        this.btnUnhide.setVisibility(needToshow ? 0 : 8);
    }

    public void showSelectAllButton(boolean needToShow) {
        if (this.menuItemSelect != null) {
            this.menuItemSelect.setIcon(needToShow ? R.drawable.ic_check_filled : R.drawable.ic_check_box_outline);
            this.isSelectAll = needToShow;
        }
    }

    public void startFullScreenImageActivity(final ArrayList<Model> buckets, final int position) {

        startActivity(new Intent(this, FullScreenImageActivity.class).putExtra(FullScreenImageActivity.OBJECT, buckets).putExtra(FullScreenImageActivity.POSITION, position));


    }

    public void onBackPressed() {
        if (this.isEditable) {
            if (this.menuItemEdit != null) {
                this.menuItemEdit.setVisible(true);
                this.img.setVisibility(View.VISIBLE);
            }
            if (this.menuItemSelect != null) {
                this.menuItemSelect.setVisible(false);
                this.menuItemSelect.setIcon(R.drawable.ic_check_box_outline);
            }
            if (this.menuItemDelete != null) {
                this.menuItemDelete.setVisible(false);
            }
            if (this.menuItemUnlock != null) {
                this.menuItemUnlock.setVisible(false);
            }
            this.isEditable = false;
            if (this.adapter != null) {
                this.adapter.isItemEditable(false);
            }
            if (this.adapter != null) {
                this.adapter.deSelectAllItem();
            }
            //this.toolbar.setNavigationIcon((int) R.drawable.ic_arrow);
            if (getSupportActionBar() != null) {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
                newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(newdrawable);

            }
            this.btnUnhide.setVisibility(8);
            return;
        }
        setBackData();
    }

    private void setBackData() {
        setResult(-1, new Intent());
        finish();
    }

    private void enableMenuItems(boolean isEnabled) {
        if (this.menuItemEdit != null) {
            this.menuItemEdit.setVisible(isEnabled);
        }
    }

    public void onImagesLoaded(ArrayList<Model> allImageModels) {
        if (allImageModels == null || allImageModels.size() <= 0) {
            MainApplication.getInstance().saveImageCount(0);
            enableMenuItems(false);
            this.viewanimator.setDisplayedChild(2);
            return;
        }
        this.adapter.addItems(allImageModels);
        images_list=allImageModels;
        MainApplication.getInstance().saveImageCount(allImageModels.size());
        enableMenuItems(true);
        this.viewanimator.setDisplayedChild(1);
    }
    public void onVideosLoaded(ArrayList<Model> allVideoModels) {
        if (allVideoModels == null || allVideoModels.size() <= 0) {
            MainApplication.getInstance().saveVideoCount(0);
            enableMenuItems(false);
            viewanimator.setDisplayedChild(2);
            return;
        }
        adapter.addItems(allVideoModels);
        MainApplication.getInstance().saveVideoCount(allVideoModels.size());
        enableMenuItems(true);
        viewanimator.setDisplayedChild(1);
    }

  /*  private void LoadBannerAd() {
        new AdsManager().LoadBannerAd(this, this.bannerContainer);
    }*/

    protected void onStop() {
        super.onStop();
        if (this.f16t != null) {
            this.f16t.cancel();
        }
        hideProgressDialog();
        Log.e("TAG", "onStop: ");
    }


}
