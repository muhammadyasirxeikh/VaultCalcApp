package com.codecoy.securecalculator.audios.add;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.codecoy.securecalculator.model.AllAudioModel;
import com.codecoy.securecalculator.model.Model;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;

import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.app.AppConstants;
import com.codecoy.securecalculator.app.BaseActivity;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.audios.add.adapter.AllAudiosAdapter;
import com.codecoy.securecalculator.callbacks.OnAllAudiosLoadedListener;
import com.codecoy.securecalculator.db.DBHelper;
import com.codecoy.securecalculator.utils.CenterTitleToolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAudiosActivity extends BaseActivity implements OnAllAudiosLoadedListener {

    private LinearLayout adView;
    AllAudiosAdapter adapter;
    //@BindView(R.id.banner_container)

    //@BindView(R.id.btn_hide)
    TextView name_video;
    ImageView image_video;
    Button btnHide;
    private int count;
    DBHelper dbHelper;
    private String destPath;
    private Dialog dialog;
    private boolean isAllSelected;
    private boolean isFileCopied = true;
    private boolean isImageAddedToNewAlbum;
    private MenuItem itemSelectAll;
    private LinearLayout nativeAdContainer;
    private int progress;
    private ProgressDialog progressDialog;
    private ProgressBar progressbar;
    //@BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /* renamed from: t */
    private Timer f12t;
    //@BindView(R.id.toolbar)
    String folder_name;
    CenterTitleToolbar toolbar;
    private TextView txtCount;
    //@BindView(R.id.txt_error)
    TextView txtError;
    //@BindView(R.id.viewanimator)
    ViewAnimator viewanimator;


    class C05092 implements Runnable {
        C05092() {
        }

        public void run() {
            AddAudiosActivity.this.progressbar.setProgress(AddAudiosActivity.this.progress);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_adds_image);
        ButterKnife.bind((Activity) this);
        this.dbHelper = new DBHelper(this);

        folder_name=getIntent().getStringExtra("folder_name");


        NativeAdLayout native_banner = findViewById(R.id.native_banner);
        LoadAds.getInstance(AddAudiosActivity.this).loadFacebookNativeBanner(AddAudiosActivity.this, native_banner);


        findViews();
        setHeaderInfo();
        Init();
    }

    private void findViews() {


        recyclerview = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        txtError = findViewById(R.id.txt_error);
        viewanimator = findViewById(R.id.viewanimator);
        btnHide = findViewById(R.id.btn_hide);
//        name_video=findViewById(R.id.name_video);
//        image_video=findViewById(R.id.image_video);
    }


    private void setHeaderInfo() {
        //this.toolbar.setNavigationIcon((int) R.drawable.ic_close);
        setSupportActionBar(this.toolbar);
//        name_video.setText("Audio");
//        image_video.setImageResource(R.drawable.ic_baseline_audio_file_white);
        getSupportActionBar().setTitle(getString(R.string.add_audio));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.add_audio) + "</font>")));

        if(getSupportActionBar()!=null){
            Drawable drawable= getResources().getDrawable(R.drawable.ic_close);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
//            getActionBar().setBackgroundDrawable(getDrawable(R.drawable.ic_baseline_arrow_back_24));
        }
    }

    private void Init() {
        MainApplication.getInstance().LogFirebaseEvent("7", "AddAudio");
        File file = new File(AppConstants.IMAGE_PATH+folder_name);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.destPath = file.getAbsolutePath();
        this.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new AllAudiosAdapter(this);
        this.recyclerview.setAdapter(this.adapter);
        GetAllAudiosAsyncTask task = new GetAllAudiosAsyncTask();
        task.onAllAudiosLoadedListener = this;
        task.execute(new Void[0]);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_image_menu, menu);
        this.itemSelectAll = menu.findItem(R.id.action_select_all);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.action_select_all:
                if (!this.isAllSelected) {
                    if (this.adapter != null) {
                        this.adapter.selectAllItem();
                    }
                    this.isAllSelected = true;
                    item.setIcon(R.drawable.ic_check_filled);
                    showHideButton(true);
                    break;
                }
                if (this.adapter != null) {
                    this.adapter.deSelectAllItem();
                }
                this.isAllSelected = false;
                item.setIcon(R.drawable.ic_check_box_outline);
                showHideButton(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHideButton(boolean value) {
        this.btnHide.setVisibility(value ? 0 : 8);
    }

    public void setSelectAll(boolean selectAll) {
        if (this.itemSelectAll == null) {
            return;
        }
        if (selectAll) {
            this.itemSelectAll.setIcon(R.drawable.ic_check_filled);
        } else {
            this.itemSelectAll.setIcon(R.drawable.ic_check_box_outline);
        }
    }

    @OnClick({R.id.btn_hide})
    public void onClick() {


        if (LoadAds.getInstance(AddAudiosActivity.this).isFacebookInterstitialLoaded()) {
            LoadAds.getInstance(AddAudiosActivity.this).showFacebookInterstital();
            LoadAds.getInstance(AddAudiosActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    LoadAds.getInstance(AddAudiosActivity.this).reloadfacebookInterstitial();


                    if (AddAudiosActivity.this.adapter != null) {
                        final List<String> selectedFiles = AddAudiosActivity.this.adapter.getSelectedImages();
                        if (selectedFiles == null || selectedFiles.size() <= 0) {
                            Toast.makeText(AddAudiosActivity.this, "Please select at least one image!", 0).show();
                            return;
                        }
                        showProgressDialog(selectedFiles);
                        AddAudiosActivity.this.f12t = new Timer();
                        AddAudiosActivity.this.f12t.scheduleAtFixedRate(new TimerTask() {

                            class C05061 implements Runnable {
                                C05061() {
                                }

                                public void run() {
                                    AddAudiosActivity.this.publishProgress(selectedFiles.size());
                                }
                            }

                            class C05072 implements Runnable {
                                C05072() {
                                }

                                public void run() {
                                    AddAudiosActivity.this.hideProgressDialog();
                                    AddAudiosActivity.this.setBackData();
                                }
                            }

                            public void run() {
                                if (AddAudiosActivity.this.count == selectedFiles.size()) {
                                    AddAudiosActivity.this.f12t.cancel();
                                    AddAudiosActivity.this.isImageAddedToNewAlbum = true;
                                    AddAudiosActivity.this.runOnUiThread(new C05072());
                                } else if (AddAudiosActivity.this.isFileCopied) {
                                    AddAudiosActivity.this.runOnUiThread(new C05061());
                                    AddAudiosActivity.this.isFileCopied = false;
                                    File src = new File((String) selectedFiles.get(AddAudiosActivity.this.count));
                                    AddAudiosActivity.this.moveFile(src, new File(AddAudiosActivity.this.destPath, src.getName()));
                                    AddAudiosActivity.this.count = AddAudiosActivity.this.count + 1;
                                    AddAudiosActivity.this.isImageAddedToNewAlbum = true;
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
            LoadAds.getInstance(AddAudiosActivity.this).reloadfacebookInterstitial();
            if (AddAudiosActivity.this.adapter != null) {
                final List<String> selectedFiles = AddAudiosActivity.this.adapter.getSelectedImages();
                if (selectedFiles == null || selectedFiles.size() <= 0) {
                    Toast.makeText(AddAudiosActivity.this, "Please select at least one image!", 0).show();
                    return;
                }
                showProgressDialog(selectedFiles);
                this.f12t = new Timer();
                this.f12t.scheduleAtFixedRate(new TimerTask() {

                    class C05061 implements Runnable {
                        C05061() {
                        }

                        public void run() {
                            AddAudiosActivity.this.publishProgress(selectedFiles.size());
                        }
                    }

                    class C05072 implements Runnable {
                        C05072() {
                        }

                        public void run() {
                            AddAudiosActivity.this.hideProgressDialog();
                            AddAudiosActivity.this.setBackData();
                        }
                    }

                    public void run() {
                        if (AddAudiosActivity.this.count == selectedFiles.size()) {
                            AddAudiosActivity.this.f12t.cancel();
                            AddAudiosActivity.this.isImageAddedToNewAlbum = true;
                            AddAudiosActivity.this.runOnUiThread(new C05072());
                        } else if (AddAudiosActivity.this.isFileCopied) {
                            AddAudiosActivity.this.runOnUiThread(new C05061());
                            AddAudiosActivity.this.isFileCopied = false;
                            File src = new File((String) selectedFiles.get(AddAudiosActivity.this.count));
                            AddAudiosActivity.this.moveFile(src, new File(AddAudiosActivity.this.destPath, src.getName()));
                            AddAudiosActivity.this.count = AddAudiosActivity.this.count + 1;
                            AddAudiosActivity.this.isImageAddedToNewAlbum = true;
                        }
                    }
                }, 0, 200);
            }
        }



    }

    private void setBackData() {
        if (!this.isImageAddedToNewAlbum) {
            File file = new File(this.destPath);
            if (file.exists()) {
                file.delete();
            }
        }
        Intent intent = new Intent();
        intent.putExtra(AppConstants.HIDDEN_RESULT, this.isImageAddedToNewAlbum);
        setResult(-1, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    public void onBackPressed() {
        setBackData();
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
        ((TextView) this.dialog.findViewById(R.id.txt_title)).setText("Moving Audio(s)");
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
                        runOnUiThread(new C05092());
                    } else {
                        out.close();
                        this.isFileCopied = true;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                AddAudiosActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(dst)));
                            }
                        });
                        in.close();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                AddAudiosActivity.this.deleteFilePath(src);
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

    public void onAllAudiosLoaded(ArrayList<Model> model) {
        if (model == null || model.size() <= 0) {
            this.viewanimator.setDisplayedChild(2);
            return;
        }
        this.adapter.addItems(model);
        this.viewanimator.setDisplayedChild(1);
    }


    protected void onStop() {
        super.onStop();
        if (this.f12t != null) {
            this.f12t.cancel();
        }
        hideProgressDialog();
    }
}
