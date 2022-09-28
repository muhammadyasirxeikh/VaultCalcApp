package com.codecoy.securecalculator.image.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.model.Model;
import com.smarteist.autoimageslider.SliderViewAdapter;


import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    // list for storing urls of images.

    List<Model> images;

    // Constructor
    public SliderAdapter(Context context , List<Model> images) {

        this.images=images;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final Model sliderItem = images.get(position);

        // Glide is use to load image
        // from url in your imageview.
//        viewHolder.imageViewBackground.setImageResource(images.get(position));
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getPath())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return images.size();
    }

    static class SliderAdapterViewHolder extends ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.imageViewMain);
            this.itemView = itemView;
        }
    }
}
