package com.codecoy.securecalculator.fullscreenimage.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.fullscreenimage.FullScreenImageActivity;
import com.codecoy.securecalculator.model.Model;
import com.codecoy.securecalculator.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageAdapter extends PagerAdapter {
    private final GestureDetector detector;
    private Context mContext;
    private List<Model> models = new ArrayList();

    class C05891 implements OnClickListener {
        C05891() {
        }

        public void onClick(View view) {
            if (FullScreenImageAdapter.this.mContext instanceof FullScreenImageActivity) {
                ((FullScreenImageActivity) FullScreenImageAdapter.this.mContext).showHideUI();
            }
        }
    }

    class GestureTap extends SimpleOnGestureListener {
        GestureTap() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            Log.i("onDoubleTap :", "" + e.getAction());
            return true;
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            ((FullScreenImageActivity) FullScreenImageAdapter.this.mContext).showHideUI();
            return true;
        }
    }

    public FullScreenImageAdapter(Context context) {
        this.detector = new GestureDetector(context, new GestureTap());
        this.mContext = context;
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup layout = (ViewGroup) LayoutInflater.from(this.mContext).inflate(R.layout.raw_fullscreen, collection, false);
        PhotoView image = (PhotoView) layout.findViewById(R.id.image);
        image.setOnClickListener(new C05891());
        File fFile = new File(((Model) this.models.get(position)).getPath());
        if (!((Model) this.models.get(position)).getPath().isEmpty()) {
            Glide.with(this.mContext).load(Uri.fromFile(fFile)).into(image);
        }
        collection.addView(layout);
        return layout;
    }

    public int getItemPosition(Object object) {
        if (this.models.contains((View) object)) {
            return this.models.indexOf((View) object);
        }
        return -2;
    }

    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public int getCount() {
        return this.models == null ? 0 : this.models.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void addItems(List<Model> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    public void removeItem(Model image) {
        File file = new File(image.getPath());
        if (file.exists()) {
            file.delete();
        }
        this.models.remove(image);
        notifyDataSetChanged();
    }
}
