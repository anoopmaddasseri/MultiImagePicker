package com.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import come.picker.R;

public class FacebookAlbumImagesAdapter extends SelectableAdapter<FacebookAlbumImagesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> mAlbumImages = new ArrayList<>();
    private ViewHolder.ClickListener mClickListener;

    public FacebookAlbumImagesAdapter(Context context, ViewHolder.ClickListener clickListener) {
        this.mContext = context;
        this.mClickListener = clickListener;
    }

    @Override
    public FacebookAlbumImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_show_gallery_images, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView, mClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Glide.with(mContext)
                .load(mAlbumImages.get(position))
                .placeholder(R.drawable.ic_empty_picture).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.mImgAlbum);
        viewHolder.mSelectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mAlbumImages.size();
    }

    public void setItems(ArrayList<String> items) {
        this.mAlbumImages = items;
    }

    public String getItem(int pos) {
        return mAlbumImages.get(pos);
    }

    public ArrayList<String> getAlbumImagesList() {
        return mAlbumImages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final RelativeLayout mSelectedOverlay;
        public ImageView mImgAlbum;
        private ClickListener mListener;

        public ViewHolder(View itemLayoutView, ClickListener listener) {
            super(itemLayoutView);
            this.mListener = listener;
            mImgAlbum = (ImageView) itemLayoutView.findViewById(R.id.imgAlbum);
            mSelectedOverlay = (RelativeLayout) itemView.findViewById(R.id.selectedOverlay);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mListener != null) {
                return mListener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);

            boolean onItemLongClicked(int position);
        }
    }
}
