package com.codecoy.securecalculator.video.add;

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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

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
import com.codecoy.securecalculator.callbacks.OnAllVideosLoadedListener;
import com.codecoy.securecalculator.db.DBHelper;
import com.codecoy.securecalculator.model.AllVideosModel;
import com.codecoy.securecalculator.utils.CenterTitleToolbar;
import com.codecoy.securecalculator.video.add.adapter.AllVideoAdapter;

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

public class AddVideoActivity extends BaseActivity implements OnAllVideosLoadedListener {


    DBHelper dbHelper;
    TextView txtError;
    CenterTitleToolbar toolbar;
    private TextView txtCount;

    ViewAnimator viewanimator;
    AllVideoAdapter adapter;

    private boolean isFileCopied = true;
    Button btnHide;
    private int count;

    private String destPath;
    private Dialog dialog;
    private boolean isAllSelected;

    private boolean isImageAddedToNewAlbum;
    private boolean isTransfering = true;
    private MenuItem itemSelectAll;
    private ViewGroup nativeAdContainer;
    private int progress = 0;
    private ProgressDialog progressDialog;
    private ProgressBar progressbar;

    TextView name_video;
    ImageView image_video;

    private Timer timer;
    RecyclerView recyclerview;

    String folder_name;

    class C06512 implements Runnable {
        C06512() {
        }

        public void run() {
            progressbar.setProgress(progress);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_adds_image);
        ButterKnife.bind((Activity) this);
        dbHelper = new DBHelper(this);

        folder_name=getIntent().getStringExtra("folder_name");




        NativeAdLayout native_banner = findViewById(R.id.native_banner);
        LoadAds.getInstance(AddVideoActivity.this).loadFacebookNativeBanner(AddVideoActivity.this, native_banner);

        findViews();
        setHeaderInfo();
        Init();
    }

    private void findViews() {

        btnHide = findViewById(R.id.btn_hide);
        recyclerview = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        txtError = findViewById(R.id.txt_error);
        viewanimator = findViewById(R.id.viewanimator);


    }


    private void setHeaderInfo() {
        // toolbar.setNavigationIcon((int) R.drawable.ic_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.add_video));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.add_video) + "</font>")));


        if (getSupportActionBar() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        }
    }

    private void Init() {

        MainApplication.getInstance().LogFirebaseEvent("5", "AddVideo");
        File file = new File(AppConstants.IMAGE_PATH+folder_name);
        if (!file.exists()) {
            file.mkdirs();
        }
        destPath = file.getAbsolutePath();
        recyclerview.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new AllVideoAdapter(this);
        recyclerview.setAdapter(adapter);
        GetAllVideosAsyncTask task = new GetAllVideosAsyncTask();
        task.onAllVideosLoadedListener = this;
        task.execute(new Void[0]);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_image_menu, menu);
        itemSelectAll = menu.findItem(R.id.action_select_all);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.action_select_all:
                if (!isAllSelected) {
                    if (adapter != null) {
                        adapter.selectAllItem();
                    }
                    isAllSelected = true;
                    item.setIcon(R.drawable.ic_check_filled);
                    showHideButton(true);
                    break;
                }
                if (adapter != null) {
                    adapter.deSelectAllItem();
                }
                isAllSelected = false;
                item.setIcon(R.drawable.ic_check_box_outline);
                showHideButton(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHideButton(boolean value) {
        btnHide.setVisibility(value ? 0 : 8);
    }

    public void setSelectAll(boolean selectAll) {
        if (itemSelectAll == null) {
            return;
        }
        if (selectAll) {
            itemSelectAll.setIcon(R.drawable.ic_check_filled);
        } else {
            itemSelectAll.setIcon(R.drawable.ic_check_box_outline);
        }
    }

    public void onAllVideosLoaded(ArrayList<Model> allVideoModels) {
        if (allVideoModels == null || allVideoModels.size() <= 0) {
            viewanimator.setDisplayedChild(2);
            return;
        }
        adapter.addItems(allVideoModels);
        viewanimator.setDisplayedChild(1);
    }

    @OnClick({R.id.btn_hide})
    public void onClick() {


        if (LoadAds.getInstance(AddVideoActivity.this).isFacebookInterstitialLoaded()) {
            LoadAds.getInstance(AddVideoActivity.this).showFacebookInterstital();
            LoadAds.getInstance(AddVideoActivity.this).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    LoadAds.getInstance(AddVideoActivity.this).reloadfacebookInterstitial();
                    if (adapter != null) {
                        final List<String> selectedFiles = adapter.getSelectedImages();
                        if (selectedFiles == null || selectedFiles.size() <= 0) {
                            Toast.makeText(AddVideoActivity.this, "Please select at least one video!", 0).show();
                            return;
                        }
                        count = 0;
                        showProgressDialog(selectedFiles);
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {

                            class C06481 implements Runnable {
                                C06481() {
                                }

                                public void run() {
                                    publishProgress(selectedFiles.size());
                                }
                            }

                            class C06492 implements Runnable {
                                C06492() {
                                }

                                public void run() {
                                    hideProgressDialog();
                                    setBackData();
                                }
                            }

                            public void run() {
                                if (count == selectedFiles.size()) {
                                    timer.cancel();
                                    isImageAddedToNewAlbum = true;
                                    runOnUiThread(new C06492());
                                } else if (isFileCopied) {
                                    runOnUiThread(new C06481());
                                    isFileCopied = false;
                                    File src = new File((String) selectedFiles.get(count));
                                    moveFile(src, new File(destPath, src.getName()));
                                    count = count + 1;
                                    isImageAddedToNewAlbum = true;
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
            LoadAds.getInstance(AddVideoActivity.this).reloadfacebookInterstitial();
            if (adapter != null) {
                final List<String> selectedFiles = adapter.getSelectedImages();
                if (selectedFiles == null || selectedFiles.size() <= 0) {
                    Toast.makeText(this, "Please select at least one video!", 0).show();
                    return;
                }
                count = 0;
                showProgressDialog(selectedFiles);
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {

                    class C06481 implements Runnable {
                        C06481() {
                        }

                        public void run() {
                            publishProgress(selectedFiles.size());
                        }
                    }

                    class C06492 implements Runnable {
                        C06492() {
                        }

                        public void run() {
                            hideProgressDialog();
                            setBackData();
                        }
                    }

                    public void run() {
                        if (count == selectedFiles.size()) {
                            timer.cancel();
                            isImageAddedToNewAlbum = true;
                            runOnUiThread(new C06492());
                        } else if (isFileCopied) {
                            runOnUiThread(new C06481());
                            isFileCopied = false;
                            File src = new File((String) selectedFiles.get(count));
                            moveFile(src, new File(destPath, src.getName()));
                            count = count + 1;
                            isImageAddedToNewAlbum = true;
                        }
                    }
                }, 0, 200);
            }
        }


    }

    private void setBackData() {

        if (!isImageAddedToNewAlbum) {
            File file = new File(destPath);
            if (file.exists()) {
                file.delete();
            }
        }
        Intent intent = new Intent();
        intent.putExtra(AppConstants.HIDDEN_RESULT, isImageAddedToNewAlbum);
        setResult(-1, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }

    public void onBackPressed() {
        setBackData();
    }

    private void showProgressDialog(List<String> files) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_progress);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        progressbar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        txtCount = (TextView) dialog.findViewById(R.id.txt_count);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        nativeAdContainer = (LinearLayout) dialog.findViewById(R.id.native_ad_container);
        txtTitle.setText("Moving Video(s)");
        txtCount.setText("Moving 1 of " + files.size());
        int totalFileSize = 0;
        for (String ss : files) {
            totalFileSize += (int) new File(ss).length();
        }
        progressbar.setMax(totalFileSize);
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void publishProgress(int size) {
        if (dialog != null && dialog.isShowing()) {
            txtCount.setText("Moving " + (count + 1) + " of " + size);
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
                        progress += len;
                        runOnUiThread(new C06512());
                    } else {
                        out.close();
                        isFileCopied = true;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(dst)));
                            }
                        });
                        in.close();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                deleteFilePath(src);
                            }
                        });
                        isFileCopied = true;
                        return;
                    }
                }
            } catch (Throwable th) {
                in.close();
                runOnUiThread();
                isFileCopied = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isFileCopied = true;
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

    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        hideProgressDialog();
    }
}
