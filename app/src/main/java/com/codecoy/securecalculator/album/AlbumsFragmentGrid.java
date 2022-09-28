package com.codecoy.securecalculator.album;

import android.Manifest;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecoy.securecalculator.App;
import com.codecoy.securecalculator.TinyDB;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.PathUtil;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.app.AppConstants;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.audios.AudiosActivity;
import com.codecoy.securecalculator.db.DBHelper;
import com.codecoy.securecalculator.files.FilesActivity;
import com.codecoy.securecalculator.image.ImagesActivity;
import com.codecoy.securecalculator.utils.PolicyManager;
import com.codecoy.securecalculator.video.VideoActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class AlbumsFragmentGrid extends Fragment {

    DBHelper dbHelper;
    private String type;

    ConstraintLayout image, audios, videos, files, camera,main_layout;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final String TAG = "AlbumsFragmentGrid";


    private Dialog dialog;
    private ProgressBar progressbar;
    private TextView txtCount;

    TextView txtError;
    private int progress;
    private int count;
    private Timer f17t;
    private boolean isFileCopied = true;
    private boolean isImageAddedToNewAlbum;
    private String destPath;

    List<String> name_list;

    List<Integer> image_resource;
    List<Integer> image_background;
    List<String> quantity_list;

    AlbumsGridAdapter adapter;

    LinearLayoutManager manager;

    RecyclerView recyclerView;
    View view;
    TinyDB tinyDB;
    View viewblack;

    private FloatingActionMenu floatingActionMenuId;
    private FloatingActionButton pictureFloatingButton, audioFloatingButton, videoFloatingButton, fileFloatingButton, cameraFloatingButton;


    class C06092 implements Runnable {
        C06092() {
        }

        public void run() {
            progressbar.setProgress(progress);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.testlayout_grid, container, false);
        tinyDB=new TinyDB(requireContext());


        App.albumFragmentGrid=this;



        name_list=tinyDB.getListString("folder_name");
        quantity_list=tinyDB.getListString("folder_quantity");
        image_resource=tinyDB.getListInt("folder_resource");
        image_background=tinyDB.getListInt("folder_background");

        recyclerView=view.findViewById(R.id.recyclerview);


        ButterKnife.bind((Object) this, view);



        NativeAdLayout native_banner = view.findViewById(R.id.native_banner);
        LoadAds.getInstance(getContext()).loadFacebookNativeBanner(getContext(), native_banner);


        NativeAdLayout nativeAdLayout=view.findViewById(R.id.native_ad_container);
        LoadAds.getInstance(getContext()).loadFacebookNative(getContext(),nativeAdLayout);

        findViews(view);

        setadapter();
//        addNewFolder();


        return view;
    }

    public void addNewFolder(String name) {

        name_list.add(name);
        quantity_list.add("0");
        image_background.add(R.drawable.circleback3);
        image_resource.add(R.drawable.ic_baseline_picture_in_picture_24);

        tinyDB.putListString("folder_name",name_list);
        tinyDB.putListString("folder_quantity",quantity_list);
        tinyDB.putListInt("folder_resource",image_resource);
        tinyDB.putListInt("folder_background",image_background);
        adapter.notifyDataSetChanged();

    }


    public void setadapter() {




        adapter = new AlbumsGridAdapter(name_list,quantity_list,image_resource,image_background,requireContext());

        manager = new GridLayoutManager(requireContext(), 2);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);



    }

    private void findViews(View v) {

//        image = v.findViewById(R.id.image);
//        audios = v.findViewById(R.id.audios);
//        videos = v.findViewById(R.id.videos);
//        files = v.findViewById(R.id.files);
//        camera = v.findViewById(R.id.cameraId);
        main_layout = v.findViewById(R.id.main_layout);
        viewblack=v.findViewById(R.id.view);

        floatingActionMenuId = v.findViewById(R.id.floatingActionMenuId);

        floatingActionMenuId.setMenuButtonColorPressed(R.color.colorAccent);


        floatingActionMenuId.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened){

                    viewblack.bringToFront();
                    viewblack.setVisibility(View.VISIBLE);
                    floatingActionMenuId.bringToFront();
//                    main_layout.bringToFront();
//                    main_layout.setBackgroundResource(R.color.black_dull);
                }else {
                    viewblack.setVisibility(View.GONE);
                    main_layout.bringToFront();
                    main_layout.setBackgroundResource(R.color.white);
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
        pictureFloatingButton = v.findViewById(R.id.picturesFloatingActionButtonId);
        audioFloatingButton = v.findViewById(R.id.audioFloatingActionButtonId);
        videoFloatingButton = v.findViewById(R.id.videoFloatingActionButtonId);
        fileFloatingButton = v.findViewById(R.id.fileFloatingActionButtonId);
        cameraFloatingButton = v.findViewById(R.id.cameraFloatingActionButtonId);

//        image.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
//                    LoadAds.getInstance(getContext()).showFacebookInterstital();
//                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(Ad ad) {
//
//                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                            startActivityForResult(new Intent(getActivity(), ImagesActivity.class), AppConstants.REFRESH_LIST);
//                        }
//
//                        @Override
//                        public void onError(Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(Ad ad) {
//
//                        }
//                    });
//                } else {
//                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                    startActivityForResult(new Intent(getActivity(), ImagesActivity.class), AppConstants.REFRESH_LIST);
//                }
//
//
//            }
//        });
//        audios.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
//                    LoadAds.getInstance(getContext()).showFacebookInterstital();
//                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(Ad ad) {
//
//                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                            startActivityForResult(new Intent(getActivity(), AudiosActivity.class), AppConstants.REFRESH_LIST);
//
//                        }
//
//                        @Override
//                        public void onError(Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(Ad ad) {
//
//                        }
//                    });
//                } else {
//                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                    startActivityForResult(new Intent(getActivity(), AudiosActivity.class), AppConstants.REFRESH_LIST);
//                }
//
//
//            }
//        });
//        videos.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
//                    LoadAds.getInstance(getContext()).showFacebookInterstital();
//                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(Ad ad) {
//
//                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                            AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), VideoActivity.class), AppConstants.REFRESH_LIST);
//
//
//                        }
//
//                        @Override
//                        public void onError(Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(Ad ad) {
//
//                        }
//                    });
//                } else {
//                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                    AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), VideoActivity.class), AppConstants.REFRESH_LIST);
//
//                }
//
//            }
//        });
//        files.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//
//                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
//                    LoadAds.getInstance(getContext()).showFacebookInterstital();
//                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(Ad ad) {
//
//                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                            startActivityForResult(new Intent(getActivity(), FilesActivity.class), AppConstants.REFRESH_LIST);
//
//                        }
//
//                        @Override
//                        public void onError(Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(Ad ad) {
//
//                        }
//                    });
//                } else {
//                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//                    startActivityForResult(new Intent(getActivity(), FilesActivity.class), AppConstants.REFRESH_LIST);
//
//
//                }
//
//
//            }
//        });
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
//                    LoadAds.getInstance(getContext()).showFacebookInterstital();
//                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(Ad ad) {
//
//                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//
//                            if (checkPermission()) {
//                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(cameraIntent, 1);
//                            } else {
//                                requestPermission();
//                            }
//
//                        }
//
//                        @Override
//                        public void onError(Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(Ad ad) {
//
//                        }
//                    });
//                } else {
//                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
//
//                    if (checkPermission()) {
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, 1);
//                    } else {
//                        requestPermission();
//                    }
//
//
//                }
//
//
//            }
//        });
        pictureFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(getContext()).showFacebookInterstital();
                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(getActivity(), ImagesActivity.class), AppConstants.REFRESH_LIST);
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
                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(getActivity(), ImagesActivity.class), AppConstants.REFRESH_LIST);
                }


            }
        });
        audioFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(getContext()).showFacebookInterstital();
                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(getActivity(), AudiosActivity.class), AppConstants.REFRESH_LIST);

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
                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(getActivity(), AudiosActivity.class), AppConstants.REFRESH_LIST);
                }

            }
        });
        videoFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(getContext()).showFacebookInterstital();
                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                            AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), VideoActivity.class), AppConstants.REFRESH_LIST);


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
                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                    AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), VideoActivity.class), AppConstants.REFRESH_LIST);

                }


            }
        });
        fileFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(getContext()).showFacebookInterstital();
                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                            startActivityForResult(new Intent(getActivity(), FilesActivity.class), AppConstants.REFRESH_LIST);

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
                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();
                    startActivityForResult(new Intent(getActivity(), FilesActivity.class), AppConstants.REFRESH_LIST);
                }

            }
        });
        cameraFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoadAds.getInstance(getContext()).isFacebookInterstitialLoaded()) {
                    LoadAds.getInstance(getContext()).showFacebookInterstital();
                    LoadAds.getInstance(getContext()).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            LoadAds.getInstance(getContext()).reloadfacebookInterstitial();

                            if (checkPermission()) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1);
                            } else {
                                requestPermission();
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
                    LoadAds.getInstance(getContext()).reloadfacebookInterstitial();

                    if (checkPermission()) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 1);
                    } else {
                        requestPermission();
                    }


                }

            }
        });
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Log.i("pictureFormat", "onActivityResult: " + imageBitmap);
                Uri uri = getImageUri(getContext(), imageBitmap);
                Log.i("pictureFormat", "onActivityResult: " + uri);

                File file = new File(PathUtil.getPath(getContext(), uri));

                Log.i(TAG, "onActivityResult: fileSrcPath" + file);

                File filetarget = new File(AppConstants.IMAGE_PATH);


                Log.i(TAG, "onActivityResult: targetFile" + filetarget);

                String name = String.valueOf(System.currentTimeMillis());

                //  moveFileData(String.valueOf(file), name, filetarget + "/");

                moveFile(file, new File(filetarget, System.currentTimeMillis() + ".jpeg"));
                Toast.makeText(getContext(), "Camera image saved in photos", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {
            File file = new File(AppConstants.IMAGE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            destPath = file.getAbsolutePath();

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.i(TAG, "getImageUri: pathBitmap" + path);


        return Uri.parse(path);

    }

    private void moveFile(final File src, final File dst) {
        Log.i(TAG, "moveFile: src:" + src);
        Log.i(TAG, "moveFile: target:" + dst);
        OutputStream out;
        try {
            InputStream in = new FileInputStream(src);

            Log.i(TAG, "moveFile: inFile" + in);
            try {
                out = new FileOutputStream(dst);

                Log.i(TAG, "moveFile:  outFile" + out);

                byte[] buf = new byte[1024];
                while (true) {
                    int len = in.read(buf);

                    Log.i(TAG, "moveFile: lengthOf" + len);

                    if (len > 0) {
                        Log.i(TAG, "moveFile: if" + "if");
                        out.write(buf, 0, len);
                    } else {
                        Log.i(TAG, "moveFile: else " + "else");
                        out.close();

                        in.close();
                        getActivity().runOnUiThread(new Runnable() {
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
                runOnUiThread(/* anonymous class already generated */);
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
            ContentResolver contentResolver = getContext().getContentResolver();
            Uri filesUri = MediaStore.Files.getContentUri("external");
            contentResolver.delete(filesUri, "_data=?", selectionArgs);
            if (file.exists()) {
                contentResolver.delete(filesUri, "_data=?", selectionArgs);
                file.delete();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.getMessage(), 1).show();
        }
    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        ComponentName demoDeviceAdmin = new ComponentName(getActivity(), PolicyManager.class);
        if (devicePolicyManager == null || !devicePolicyManager.isAdminActive(demoDeviceAdmin)) {
            MainApplication.getInstance().LogFirebaseEvent("1", "Home");

            AddMobe();
            this.dbHelper = new DBHelper(getActivity());

        } else {
            MainApplication.getInstance().LogFirebaseEvent("1", "Home");

            AddMobe();
            this.dbHelper = new DBHelper(getActivity());

        }
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0 || ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }


    private void enableAdmin() {

        ComponentName demoDeviceAdmin = new ComponentName(getActivity(), PolicyManager.class);
        Log.e("DeviceAdminActive==", "" + demoDeviceAdmin);
        Intent intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
        intent.putExtra("android.app.extra.DEVICE_ADMIN", demoDeviceAdmin);
        intent.putExtra("android.app.extra.ADD_EXPLANATION", "Disable app");
        intent.putExtra("android.app.extra.ADD_EXPLANATION", "After activating admin, you will be able to block application uninstallation.");
        startActivityForResult(intent, PolicyManager.ACTIVATION_REQUEST);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0 || grantResults[1] != 0) {
            final AlertDialog alertDialog = new Builder(getActivity()).create();
            alertDialog.setTitle("Grant Permission");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please grant all permissions to access additional functionality.");
            alertDialog.setButton(-1, (CharSequence) "DISMISS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    AlbumsFragmentGrid.this.getActivity().finish();
                }
            });
            alertDialog.show();
        }
    }


    private void AddMobe() {

        if (AlbumsFragmentGrid.this.type == null) {
            return;
        }
        if (AlbumsFragmentGrid.this.type.equals(AppConstants.IMAGE)) {
            AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), ImagesActivity.class), AppConstants.REFRESH_LIST);
        } else {
            AlbumsFragmentGrid.this.startActivityForResult(new Intent(AlbumsFragmentGrid.this.getActivity(), VideoActivity.class), AppConstants.REFRESH_LIST);
        }
    }






}
