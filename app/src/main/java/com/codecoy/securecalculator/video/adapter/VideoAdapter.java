package com.codecoy.securecalculator.video.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.app.MainApplication;
import com.codecoy.securecalculator.model.Model;
import com.codecoy.securecalculator.video.VideoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Unbinder;
import butterknife.internal.Utils;

public class VideoAdapter extends Adapter<ViewHolder> {
    private ArrayList<Model> buckets;
    private Context context;
    private boolean isLongPressed = false;

    class ImageViewHolder extends ViewHolder {

        CheckBox checkbox;

        ImageView img;

        View mView;

        TextView txtSize;

//        TextView txtTitle;

        public ImageViewHolder(View itemView) {
            super(itemView);

            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            img = (ImageView) itemView.findViewById(R.id.img);
            txtSize = (TextView) itemView.findViewById(R.id.txt_size);
//            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);

            //ButterKnife.bind((Object) this, itemView);
            this.mView = itemView;
        }
    }

    public class ImageViewHolder_ViewBinding implements Unbinder {
        private ImageViewHolder target;

        @UiThread
        public ImageViewHolder_ViewBinding(ImageViewHolder target, View source) {
            this.target = target;
            target.img = (ImageView) Utils.findRequiredViewAsType(source, R.id.img, "field 'img'", ImageView.class);
//            target.txtTitle = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_title, "field 'txtTitle'", TextView.class);
            target.txtSize = (TextView) Utils.findRequiredViewAsType(source, R.id.txt_size, "field 'txtSize'", TextView.class);
            target.checkbox = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox, "field 'checkbox'", CheckBox.class);
        }

        @CallSuper
        public void unbind() {
            ImageViewHolder target = this.target;
            if (target == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            target.img = null;
//            target.txtTitle = null;
            target.txtSize = null;
            target.checkbox = null;
        }
    }

    public VideoAdapter(Context context) {
        this.context = context;
        this.buckets = new ArrayList();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_hidevideo, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Model bucket = (Model) this.buckets.get(position);
        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).checkbox.setOnCheckedChangeListener(null);
            if (bucket.isEditable()) {
                ((ImageViewHolder) holder).checkbox.setVisibility(0);
            } else {
                ((ImageViewHolder) holder).checkbox.setVisibility(8);
            }
            if (bucket.isSelected()) {
                ((ImageViewHolder) holder).checkbox.setChecked(true);
            } else {
                ((ImageViewHolder) holder).checkbox.setChecked(false);
            }
            if (bucket.isSelected()) {
                ((ImageViewHolder) holder).checkbox.setChecked(true);
            } else {
                ((ImageViewHolder) holder).checkbox.setChecked(false);
            }
            if (!bucket.getPath().isEmpty()) {
                File fFile = new File(bucket.getPath());
                Glide.with(this.context)
                        .load(Uri.fromFile(fFile))
                        .into(((ImageViewHolder) holder).img);
//                ((ImageViewHolder) holder).txtTitle.setText(fFile.getName());
                ((ImageViewHolder) holder).txtSize.setText(MainApplication.getInstance().getFileSize(fFile.length()));
            }
            ((ImageViewHolder) holder).mView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (bucket.isEditable()) {
                        bucket.setSelected(!bucket.isSelected());
                        if (bucket.isSelected()) {
                            ((ImageViewHolder) holder).checkbox.setChecked(true);
                        } else {
                            ((ImageViewHolder) holder).checkbox.setChecked(false);
                        }
                        VideoAdapter.this.checkIfAllFilesDeselected();
                        return;
                    }
                    ((VideoActivity) VideoAdapter.this.context).openVideo(bucket.getPath());
                }
            });
            ((ImageViewHolder) holder).checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (bucket.isEditable()) {
                        if (isChecked) {
                            bucket.setSelected(true);
                        } else {
                            bucket.setSelected(false);
                        }
                        VideoAdapter.this.checkIfAllFilesDeselected();
                    }
                }
            });
        }
    }

    private void checkIfAllFilesDeselected() {
        List<String> selectedFiles = new ArrayList();
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(bucket.getPath());
            }
        }
        if (selectedFiles.size() == 0) {
            ((VideoActivity) this.context).showDeleteButton(false);
            ((VideoActivity) this.context).showSelectAllButton(false);
        } else if (selectedFiles.size() == this.buckets.size()) {
            ((VideoActivity) this.context).showDeleteButton(true);
            ((VideoActivity) this.context).showSelectAllButton(true);
        } else {
            ((VideoActivity) this.context).showDeleteButton(true);
            ((VideoActivity) this.context).showSelectAllButton(false);
        }
    }

    public List<String> getSelectedImages() {
        List<String> selectedFiles = new ArrayList();
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(bucket.getPath());
            }
        }
        return selectedFiles;
    }

    public List<String> getSelectedImagePaths() {
        List<String> selectedFiles = new ArrayList();
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(bucket.getPath());
            }
        }
        return selectedFiles;
    }

    public void selectAllItem() {
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setSelected(true);
        }
        this.isLongPressed = true;
        notifyDataSetChanged();
    }

    public void deSelectAllItem() {
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setSelected(false);
        }
        this.isLongPressed = false;
        notifyDataSetChanged();
    }

    public void removeSelectedFiles() {
        List<Model> selectedFiles = new ArrayList();
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(new Model(bucket.getPath(), bucket.getLastModified()));
            }
        }
        this.buckets.removeAll(selectedFiles);
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.buckets == null ? 0 : this.buckets.size();
    }

    public void addItems(ArrayList<Model> allBuckets) {
        this.buckets.addAll(allBuckets);
        notifyDataSetChanged();
    }

    public void isItemEditable(boolean isEditable) {
        Iterator it = this.buckets.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setEditable(isEditable);
        }
        notifyDataSetChanged();
    }
}
