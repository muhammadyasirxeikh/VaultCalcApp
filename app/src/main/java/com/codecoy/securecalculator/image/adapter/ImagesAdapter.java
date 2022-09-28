package com.codecoy.securecalculator.image.adapter;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codecoy.securecalculator.R;
import com.codecoy.securecalculator.callbacks.ImageCallBack;
import com.codecoy.securecalculator.image.ImagesActivity;
import com.codecoy.securecalculator.model.AllImagesModel;
import com.codecoy.securecalculator.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Unbinder;
import butterknife.internal.Utils;

public class ImagesAdapter extends Adapter<ViewHolder> {


    private ArrayList<Model> bucketsArraylist;
    private Context context;
    private boolean isLongPressed = false;
    private ImageCallBack callBack;

    class ImageViewHolder extends ViewHolder {

        ImageView img;

        ImageView imgSelection;
        View mView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img);
            imgSelection = (ImageView) itemView.findViewById(R.id.img_selection);

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
            target.imgSelection = (ImageView) Utils.findRequiredViewAsType(source, R.id.img_selection, "field 'imgSelection'", ImageView.class);
        }

        @CallSuper
        public void unbind() {
            ImageViewHolder target = this.target;
            if (target == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            target.img = null;
            target.imgSelection = null;
        }
    }

    public ImagesAdapter(Context context,ImageCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        this.bucketsArraylist = new ArrayList();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(this.context).inflate(R.layout.raw_all_images, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Model bucket = (Model) this.bucketsArraylist.get(position);
        if (holder instanceof ImageViewHolder) {
            if (!bucket.getPath().isEmpty()) {
                Glide.with(this.context).load(Uri.fromFile(new File(bucket.getPath()))).into(((ImageViewHolder) holder).img);
            }
            if (bucket.isSelected()) {
                ((ImageViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_check_white_24dp);
            } else {
                ((ImageViewHolder) holder).imgSelection.setImageResource(0);
            }
            ((ImageViewHolder) holder).mView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (bucket.isEditable()) {
                        bucket.setSelected(!bucket.isSelected());
                        if (bucket.isSelected()) {
                            ((ImageViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_check_white_24dp);
                        } else {
                            ((ImageViewHolder) holder).imgSelection.setImageResource(0);
                        }
                        checkIfAllFilesDeselected();
                        return;
                    }
                    ((ImagesActivity) context).startFullScreenImageActivity(bucketsArraylist, holder.getAdapterPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    callBack.onLongClick(position);
                    return true;
                }
            });
        }
    }

    private void checkIfAllFilesDeselected() {
        List<String> selectedFiles = new ArrayList();
        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(bucket.getPath());
            }
        }
        if (selectedFiles.size() == 0) {
            ((ImagesActivity) this.context).showDeleteButton(false);
            ((ImagesActivity) this.context).showSelectAllButton(false);
        } else if (selectedFiles.size() == this.bucketsArraylist.size()) {
            ((ImagesActivity) this.context).showDeleteButton(true);
            ((ImagesActivity) this.context).showSelectAllButton(true);
        } else {
            ((ImagesActivity) this.context).showDeleteButton(true);
            ((ImagesActivity) this.context).showSelectAllButton(false);
        }
    }

    public List<String> getSelectedImages() {
        List<String> selectedFiles = new ArrayList();
        Iterator it = this.bucketsArraylist.iterator();
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
        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(bucket.getPath());
            }
        }
        return selectedFiles;
    }

    public void selectAllItem() {

        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setSelected(true);
        }
        this.isLongPressed = true;
        notifyDataSetChanged();
    }

    public void deSelectAllItem() {

        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setSelected(false);
        }
        this.isLongPressed = false;
        notifyDataSetChanged();
    }

    public void isItemEditable(boolean isEditable) {
        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            ((Model) it.next()).setEditable(isEditable);
        }
        notifyDataSetChanged();
    }

    public void removeSelectedFiles() {

        List<Model> selectedFiles = new ArrayList();
        Iterator it = this.bucketsArraylist.iterator();
        while (it.hasNext()) {
            Model bucket = (Model) it.next();
            if (bucket.isSelected()) {
                selectedFiles.add(new Model(bucket.getPath(), bucket.getLastModified()));
            }
        }
        this.bucketsArraylist.removeAll(selectedFiles);
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.bucketsArraylist == null ? 0 : this.bucketsArraylist.size();
    }

    public void addItems(ArrayList<Model> allBuckets) {
        this.bucketsArraylist.addAll(allBuckets);
        notifyDataSetChanged();
    }
}
