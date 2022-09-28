package com.codecoy.securecalculator.album;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.securecalculator.App;
import com.codecoy.securecalculator.LoadAds;
import com.codecoy.securecalculator.TinyDB;
import com.codecoy.securecalculator.app.AppConstants;
import com.codecoy.securecalculator.app.BaseActivity;
import com.codecoy.securecalculator.audios.AudiosActivity;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.callbacks.OnImagesLoadedListener;
import com.codecoy.securecalculator.image.ImagesActivity;
import com.codecoy.securecalculator.model.Model;
import com.codecoy.securecalculator.video.VideoActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.BusinessCategoryVH> implements OnImagesLoadedListener {
    private final List<String> folder_names;
    private List<String> list_names;
    private List<Integer> image_resource;
    private List<Integer> image_backgroungd;
    private final List<String> files_quanitty;
    String option="rename";




    Context mcontext;

    public AlbumsAdapter(List<String> name, List<String> quantity,List<Integer> img_rsc,List<Integer> imgbcg, Context context) {
        this.folder_names = name;
        this.files_quanitty = quantity;
        this.image_backgroungd=imgbcg;
        this.image_resource=img_rsc;

        this.mcontext=context;

    }


    @NonNull

    @Override
    public BusinessCategoryVH onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main, parent, false);


        return new BusinessCategoryVH(view, parent.getContext() );
    }

    @Override
    public void onBindViewHolder(@NonNull  BusinessCategoryVH holder, int position) {

        BaseActivity.GetHiddenImages task = new BaseActivity.GetHiddenImages(folder_names.get(position));

        task.onImagesLoadedListener = this;
        task.count = holder.total_files;
        task.execute(new Void[0]);


        holder.folder_name.setText(folder_names.get(position));

        holder.folder_image.setBackgroundResource(image_backgroungd.get(position));
        holder.folder_image.setImageResource(image_resource.get(position));




        holder.itemView.setOnClickListener(v->{
            if (LoadAds.getInstance(mcontext).isFacebookInterstitialLoaded()) {
                LoadAds.getInstance(mcontext).showFacebookInterstital();
                LoadAds.getInstance(mcontext).setOnFacebookInterstitialAdclosedListner(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {

                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {

                        LoadAds.getInstance(mcontext).reloadfacebookInterstitial();

                        ((Activity) mcontext).startActivityForResult(new Intent(mcontext, ImagesActivity.class).putExtra("folder_name",folder_names.get(position)), AppConstants.REFRESH_LIST);

//                        if (folder_names.get(position).equals("Pictures")){
//                            ((Activity) mcontext).startActivityForResult(new Intent(mcontext, ImagesActivity.class), AppConstants.REFRESH_LIST);
//
//                        }else if (folder_names.get(position).equals("Audios")){
//                            ((Activity) mcontext).startActivityForResult(new Intent(mcontext, AudiosActivity.class), AppConstants.REFRESH_LIST);
//
//                        }else if (folder_names.get(position).equals("Videos")){
//                            ((Activity) mcontext).startActivityForResult(new Intent(mcontext, VideoActivity.class), AppConstants.REFRESH_LIST);
//
//                        }
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
                LoadAds.getInstance(mcontext).reloadfacebookInterstitial();
                ((Activity) mcontext).startActivityForResult(new Intent(mcontext, ImagesActivity.class).putExtra("folder_name",folder_names.get(position)), AppConstants.REFRESH_LIST);

//                if (folder_names.get(position).equals("Pictures")){
//                    ((Activity) mcontext).startActivityForResult(new Intent(mcontext, ImagesActivity.class), AppConstants.REFRESH_LIST);
//
//                }else if (folder_names.get(position).equals("Audios")){
//                    ((Activity) mcontext).startActivityForResult(new Intent(mcontext, AudiosActivity.class), AppConstants.REFRESH_LIST);
//
//                }else if (folder_names.get(position).equals("Videos")){
//                    ((Activity) mcontext).startActivityForResult(new Intent(mcontext, VideoActivity.class), AppConstants.REFRESH_LIST);
//
//                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                showOptionsDialog(folder_names.get(position),position);

                return true;
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsDialog(folder_names.get(position),position);

            }
        });

    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return folder_names.size();
    }

    @Override
    public void onImagesLoaded(ArrayList<Model> arrayList) {
//        Log.d("TAG", "onImagesLoaded: "+arrayList.size());
    }


    public static class BusinessCategoryVH extends RecyclerView.ViewHolder {

        private final TextView folder_name,total_files;
        private final ImageView folder_image,menu;


        public BusinessCategoryVH(@NonNull  View itemView, Context context) {
            super(itemView);


            folder_name = itemView.findViewById(R.id.item_name);
            total_files = itemView.findViewById(R.id.quantity);
            folder_image = itemView.findViewById(R.id.imageview);
            menu = itemView.findViewById(R.id.menu);

        }
    }//VH

    private void showOptionsDialog(String s, int position) {
        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.option_menu_layout);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setLayout(-1, -2);
        }
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView folder_name=(TextView) dialog.findViewById(R.id.folder_name);
        RadioGroup radioGroup=(RadioGroup)dialog.findViewById(R.id.radio_group);
        RadioButton rename=(RadioButton) dialog.findViewById(R.id.rename);
        folder_name.setText(s);
        rename.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rename:
                        option="rename";

                        break;
                    case R.id.delete:
                        option="delete";

                        break;
                    case R.id.unhide:
                        option="unhide";

                        break;
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Toast.makeText(mcontext, option+"", Toast.LENGTH_SHORT).show();
                if (option.equals("rename")){
                    showFCreateNewFolderDialog(s,position);
                    dialog.dismiss();

                }else if (option.equals("delete")){

                    TinyDB tinyDB=new TinyDB(mcontext);
                    list_names= tinyDB.getListString("folder_name");
                    list_names.remove(position);
                    folder_names.remove(position);
                    image_backgroungd.remove(position);
                    image_resource.remove(position);
                    files_quanitty.remove(position);
                    tinyDB.putListString("folder_name",list_names);
                    tinyDB.putListString("folder_quantity",files_quanitty);
                    tinyDB.putListInt("folder_background",image_backgroungd);
                    tinyDB.putListInt("folder_resource",image_resource);
                    notifyDataSetChanged();
                    dialog.dismiss();

                }


//                    dialog.dismiss();



            }
        });
        dialog.show();
    }
    private void showFCreateNewFolderDialog(String s, int position) {
        final Dialog dialog = new Dialog(mcontext);
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

        folder_name.setText(s);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name=folder_name.getText().toString();
                if (TextUtils.isEmpty(name)){
                    folder_name.setError("Enter Folder Name");
                    folder_name.requestFocus();
                }else {
                    TinyDB tinyDB=new TinyDB(mcontext);
                   list_names= tinyDB.getListString("folder_name");
                   list_names.set(position,name);
                   folder_names.set(position,name);
                   tinyDB.putListString("folder_name",list_names);
                   notifyDataSetChanged();

//                    if (App.albumFragment!=null){
//                        albumsFragment= (AlbumsFragment) App.albumFragment;
//
//                        albumsFragment.addNewFolder(name);
//                    }



                    dialog.dismiss();
                }


            }
        });
        dialog.show();
    }


}

