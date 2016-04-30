package com.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import come.picker.R;
import com.picker.model.AlbumsModel;

public class GalleryAlbumAdapter extends
        RecyclerView.Adapter<GalleryAlbumAdapter.ViewHolder> {

    static OnItemClickListener mItemClickListener;
    private ArrayList<AlbumsModel> mGalleryImagesList;
    private Context mContext;

    public GalleryAlbumAdapter(Context context, ArrayList<AlbumsModel> galleryImagesList) {
        this.mGalleryImagesList = galleryImagesList;
        this.mContext = context;

    }

    @Override
    public GalleryAlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_gallery_albums, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTvName.setText(mGalleryImagesList.get(position).getFolderName());
        Glide.with(mContext)
                .load("file://" + mGalleryImagesList.get(position).getFolderImagePath())
                .placeholder(R.drawable.ic_empty_picture).centerCrop()
                .into(viewHolder.mImgAlbum);
    }

    @Override
    public int getItemCount() {
        return mGalleryImagesList.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public ArrayList<AlbumsModel> getGalleryImagesList() {
        return mGalleryImagesList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTvName;
        public ImageView mImgAlbum;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mTvName = (TextView) itemLayoutView.findViewById(R.id.tvName);
            mImgAlbum = (ImageView) itemLayoutView.findViewById(R.id.imgAlbum);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

}