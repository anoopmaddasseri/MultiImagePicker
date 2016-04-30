package com.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import come.picker.R;
import com.picker.model.AlbumImages;

public class ShowAlbumImagesAdapter extends SelectableAdapter<ShowAlbumImagesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AlbumImages> mAlbumImages;
    private ViewHolder.ClickListener mClickListener;

    public ShowAlbumImagesAdapter(Context context, ArrayList<AlbumImages> galleryImagesList, ViewHolder.ClickListener clickListener) {
        this.mAlbumImages = galleryImagesList;
        this.mContext = context;
        this.mClickListener = clickListener;

    }

    @Override
    public ShowAlbumImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_show_gallery_images, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView, mClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Glide.with(mContext)
                .load("file://" + mAlbumImages.get(position).getAlbumImages())
                .placeholder(R.drawable.ic_empty_picture).centerCrop()
                .into(viewHolder.mImgAlbum);
        viewHolder.mSelectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mAlbumImages.size();
    }

    public ArrayList<AlbumImages> getAlbumImagesList() {
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