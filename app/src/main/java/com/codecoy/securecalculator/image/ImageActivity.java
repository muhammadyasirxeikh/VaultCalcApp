package com.codecoy.securecalculator.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codecoy.securecalculator.callbacks.ImageCallBack;
import com.codecoy.securecalculator.model.Model;
import com.facebook.ads.NativeAdLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.app.AppConstants;
import com.codecoy.securecalculator.app.BaseActivity;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.db.DBHelper;
import com.codecoy.securecalculator.fullscreenimage.FullScreenImageActivity;
import com.codecoy.securecalculator.image.adapter.ImagesAdapter;
import com.codecoy.securecalculator.image.add.AddImageActivity;
import com.codecoy.securecalculator.model.AllImagesModel;
import com.codecoy.securecalculator.utils.CenterTitleToolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends BaseActivity implements ImageCallBack {

    private ImagesAdapter adapter;
    //@BindView(R.id.btn_unhide)
    Button btnUnhide;
    private int count;
    DBHelper dbHelper;
    //@BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    private boolean isFileCopied = true;
    private boolean isSelectAll;
    private MenuItem menuItemDelete;
    private MenuItem menuItemSelect;
    private ProgressDialog progressDialog;
    //@BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /* renamed from: t */
    private Timer f15t;
    //@BindView(R.id.toolbar)
    CenterTitleToolbar toolbar;
    //@BindView(R.id.txt_error)
    TextView txtError;
    //@BindView(R.id.viewanimator)
    ViewAnimator viewanimator;

    ImageView front_image,image_icon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_image);
        ButterKnife.bind((Activity) this);

        dbHelper = new DBHelper(this);
        findViews();
        setHeaderInfo();
        Init();

        NativeAdLayout nativeAdLayoutcontainder=findViewById(R.id.native_banner);
        LoadAds.getInstance(this).loadFacebookNativeBanner(this,nativeAdLayoutcontainder);
    }

    private void findViews() {

        btnUnhide = findViewById(R.id.btn_unhide);
        fabAdd = findViewById(R.id.fab_add);
        recyclerview = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        txtError = findViewById(R.id.txt_error);
        front_image = findViewById(R.id.front_image);
        image_icon = findViewById(R.id.image_icon);
        viewanimator = findViewById(R.id.viewanimator);

    }

    private void setHeaderInfo() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.image));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#17181A\">" + getString(R.string.image) + "</font>")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void Init() {
        MainApplication.getInstance().LogFirebaseEvent("2", AppConstants.IMAGE);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        setAdapter();
    }

    private void setAdapter() {
        adapter = new ImagesAdapter(this,this);
//        recyclerview.setAdapter(adapter);
        ArrayList<Model> allImageModels = dbHelper.getImages();
        if (allImageModels == null || allImageModels.size() <= 0) {
            viewanimator.setDisplayedChild(2);

            return;
        }


        adapter.addItems(allImageModels);

        viewanimator.setDisplayedChild(1);
    }

    @OnClick({R.id.btn_unhide, R.id.fab_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unhide:
                recoverFiles();
                return;
            case R.id.fab_add:
                startActivityForResult(new Intent(this, AddImageActivity.class), 1012);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                return;
            default:
                return;
        }
    }

    private void recoverFiles() {
        if (adapter != null) {
            List<String> selectedFiles = new ArrayList();
            if (selectedFiles == null || selectedFiles.size() <= 0) {
                Toast.makeText(this, "Please select at least one image!", 0).show();
            } else {
                showProgressDialog(selectedFiles.size());
            }
        }
    }

    private void showProgressDialog(int size) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Moving Images");
        progressDialog.setMessage("1 of " + size);
        progressDialog.show();
    }


    private void moveFile(File src, final File dst, final int imageId) {
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
                    } else {
                        out.close();
                        isFileCopied = true;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                dbHelper.deleteImage((long) imageId);
                                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(dst)));
                            }
                        });
                        in.close();
                        src.delete();
                        isFileCopied = true;
                        return;
                    }
                }
            } catch (Throwable th) {
                in.close();
                src.delete();
                isFileCopied = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isFileCopied = true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1012 && resultCode == -1) {
            viewanimator.setDisplayedChild(0);
            setAdapter();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        menuItemSelect = menu.findItem(R.id.itm_select);
        menuItemDelete = menu.findItem(R.id.itm_delete);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.itm_delete:
                final AlertDialog alertDialog = new Builder(this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure to delete selected files?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(-1, (CharSequence) "Yes", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedFiles();
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
            case R.id.itm_select:
                if (menuItemSelect != null) {
                    if (!isSelectAll) {
                        menuItemSelect.setIcon(R.drawable.ic_check_filled);
                        if (adapter != null) {
                            adapter.selectAllItem();
                        }
                        showDeleteButton(true);
                        isSelectAll = true;
                        break;
                    }
                    menuItemSelect.setIcon(R.drawable.ic_check_box_outline);
                    if (adapter != null) {
                        adapter.deSelectAllItem();
                    }
                    showDeleteButton(false);
                    isSelectAll = false;
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedFiles() {

        if (adapter != null) {

            List<String> selectedFiles = adapter.getSelectedImagePaths();
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
        if (menuItemDelete != null) {
            menuItemDelete.setVisible(needToshow);
        }
        btnUnhide.setVisibility(needToshow ? 0 : 8);
    }

    public void showSelectAllButton(boolean needToShow) {

        if (menuItemSelect != null) {
            menuItemSelect.setIcon(needToShow ? R.drawable.ic_check_filled : R.drawable.ic_check_box_outline);
            isSelectAll = needToShow;
        }
    }

    public void startFullScreenImageActivity(ArrayList<AllImagesModel> buckets, int position) {
        startActivity(new Intent(this, FullScreenImageActivity.class).putExtra(FullScreenImageActivity.OBJECT, buckets).putExtra(FullScreenImageActivity.POSITION, position));
    }

    public void onBackPressed() {
        setBackData();
    }

    private void setBackData() {
        setResult(-1, new Intent());
        finish();
    }

    @Override
    public void onLongClick(int pos) {

    }
}
