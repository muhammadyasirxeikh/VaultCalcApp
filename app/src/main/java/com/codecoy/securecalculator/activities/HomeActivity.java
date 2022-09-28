package com.codecoy.securecalculator.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecoy.securecalculator.App;
import com.codecoy.securecalculator.album.AlbumsFragmentGrid;
import com.facebook.ads.NativeAdLayout;
import com.codecoy.securecalculator.BuildConfig;
import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.album.AlbumsFragment;
import com.codecoy.securecalculator.app.BaseActivity;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.privacy.PrivacyPolicyActivity;

import java.util.Random;

public class HomeActivity extends BaseActivity {

    String TAG = "TAG";
    ImageView imgHint;

    ImageView imgNewFolder;

    ImageView select_view;

    Toolbar toolbar;

    RecyclerView recyclerview;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    RecyclerView.Adapter iv_recycler_Adapter;

    AlbumsFragment albumsFragment;
    AlbumsFragmentGrid albumsFragmentGrid;
    int row_index = 0;
    int posofItem = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.main_layout);

        NativeAdLayout native_ad_container = findViewById(R.id.nativeAdContainerId);
        LoadAds.getInstance(HomeActivity.this).loadFacebookNativeSmall(this, native_ad_container);

        setHeaderInfo();

        findViews();

        initViews();

    }

    private void findViews() {

        imgHint = findViewById(R.id.hint);
        imgNewFolder = findViewById(R.id.add_new_folder);
        select_view=findViewById(R.id.view);
        recyclerview = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void initViews() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.app_color));
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        iv_recycler_Adapter = new RecyclerViewAdapter(HomeActivity.this, screenTitles, screenIcons, posofItem);
        recyclerview.setAdapter(iv_recycler_Adapter);

        addFirstFragment();

        imgHint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                showForgotPassHintDialog();
            }
        });
        imgNewFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                showFCreateNewFolderDialog();
            }
        });

//        select_view.setOnClickListener(v->{
//
//            selectViewMenu();
//
//
//        });

    }

    private void selectViewMenu() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_forgot_pass_hint);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
    }


    private void addFirstFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add((int) R.id.frame, new AlbumsFragment());
        ft.commit();
    }

    private void setHeaderInfo() {
        setSupportActionBar(this.toolbar);
    }


    protected void onResume() {
        super.onResume();
        Log.e("Result", "" + new Random().nextInt(5));
    }






    private void showForgotPassHintDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_forgot_pass_hint);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btn_ok);
        final CheckBox chk = (CheckBox) dialog.findViewById(R.id.chk);
        ((ImageView) dialog.findViewById(R.id.img_close)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                if (chk.isChecked()) {
                    MainApplication.getInstance().setShowFPHint(true);
                }
            }
        });
        dialog.show();
    }

    private void showFCreateNewFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_new_folder_layout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btn_ok);
        EditText folder_name=(EditText) dialog.findViewById(R.id.folder_name_et);

        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String name=folder_name.getText().toString();
                if (TextUtils.isEmpty(name)){
                    folder_name.setError("Enter Folder Name");
                    folder_name.requestFocus();
                }else {
                    if (App.albumFragment!=null){
                        albumsFragment= (AlbumsFragment) App.albumFragment;

                        albumsFragment.addNewFolder(name);
                    }
                    if (App.albumFragmentGrid!=null){
                        albumsFragmentGrid= (AlbumsFragmentGrid) App.albumFragmentGrid;

                        albumsFragmentGrid.addNewFolder(name);
                    }



                    dialog.dismiss();
                }


            }
        });
        dialog.show();
    }

    public void onBackPressed() {
        showRateUsDialog();
    }

    private void showRateUsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_rate_us);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btn_ok);
        ((ImageView) dialog.findViewById(R.id.img_close)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
                System.exit(0);
                HomeActivity.this.finish();
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                String appPackageName = HomeActivity.this.getPackageName();
                try {
                    HomeActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appPackageName)));
                } catch (Exception e) {
                    HomeActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_view_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_view:
                MainApplication.view_index = 1;

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add((int) R.id.frame, new AlbumsFragment());
                ft.commit();

                return true;

            case R.id.grid_view:

                MainApplication.view_index = 2;

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.add((int) R.id.frame, new AlbumsFragmentGrid());
                ft1.commit();

                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private String[] screenTitles;
        private Drawable[] screenIcons;
        Context context1;

        public RecyclerViewAdapter(Context context2, String[] screenTitles, Drawable[] screenIcons, int posofItem) {

            this.screenTitles = screenTitles;
            this.screenIcons = screenIcons;
            context1 = context2;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView icon;
            public TextView title, txtCoin;
            public LinearLayout layoutItem;

            public ViewHolder(View v) {
                super(v);
                icon = (ImageView) v.findViewById(R.id.icon);
                title = (TextView) v.findViewById(R.id.title);
                layoutItem = (LinearLayout) v.findViewById(R.id.layoutItem);
            }
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view1 = LayoutInflater.from(context1).inflate(R.layout.item_option, parent, false);
            ViewHolder viewHolder1 = new ViewHolder(view1);
            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.icon.setImageDrawable(screenIcons[position]);

            holder.title.setText(screenTitles[position]);

            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row_index = position;
                    notifyDataSetChanged();

                    if (position == 0) {

                        startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));


                    } else if (position == 1) {

                        startActivity(new Intent(HomeActivity.this, SecurityQuestionActivity.class).putExtra(SecurityQuestionActivity.TYPE, SecurityQuestionActivity.CHANGE));

                    } else if (position == 2) {

                        share();

                    } else if (position == 3) {

                        rate();

                    } else if (position == 4) {

                        startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));


                    } else if (position == 5) {

                        startActivity(new Intent(HomeActivity.this, UnAuthorisedActivity.class));

                    }
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                }
            });
        }

        @Override
        public int getItemCount() {
            return screenTitles.length;
        }
    }

    private void rate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to rate this App?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void share() {
        try {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "Calculator Vault" + "\n\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));

        } catch (Exception e) {
        }

    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {

        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");

    }
}

