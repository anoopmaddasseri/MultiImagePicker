package com.picker.facebook;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import come.picker.R;
import com.picker.controller.BaseActivity;
import com.picker.model.Album;
import com.picker.utils.Constants;
import com.picker.utils.FacebookService;
import com.picker.utils.MarginDecoration;

public class FacebookAlbumActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private FacebookAlbumAdapter mAdapter;
    private ProgressBar mLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_albums);
        initToolBar(getString(R.string.fb_albums), true);
        initComponents();
    }

    private void initComponents() {
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // create an Object for Adapter
        mAdapter = new FacebookAlbumAdapter(FacebookAlbumActivity.this);

        // set the adapter object to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new FacebookAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent galleryAlbumsIntent = new Intent(FacebookAlbumActivity.this, FacebookAlbumImagesActivity.class);
                galleryAlbumsIntent.putExtra("position", position);
                galleryAlbumsIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                galleryAlbumsIntent.putExtra("albumsList", mAdapter.getItem(position));
                galleryAlbumsIntent.putExtra(Constants.DATA, getIntent().getIntExtra(Constants.DATA, 1));
                startActivity(galleryAlbumsIntent);
                finish();
            }
        });
        initViews();
    }

    private void initViews() {
        FacebookService facebookService = new FacebookService();
        facebookService.getFbAlbum(new FacebookResponse() {
            @Override
            public void onComplete(Object object) {
                mLoading.setVisibility(View.GONE);
                if (object != null) {
                    findViewById(R.id.noAlbums).setVisibility(View.GONE);
                    List<Album> albumList = (List<Album>) object;
                    mAdapter.setList(albumList);
                    mAdapter.notifyDataSetChanged();
                } else
                    findViewById(R.id.noAlbums).setVisibility(View.VISIBLE);
            }
        });

    }

    public static class FacebookAlbumAdapter extends
            RecyclerView.Adapter<FacebookAlbumAdapter.ViewHolder> {

        private static OnItemClickListener mItemClickListener;
        private List<Album> mGalleryImagesList = new ArrayList<>();
        private Context mContext;

        public FacebookAlbumAdapter(Context context) {
            this.mContext = context;
        }

        public void setList(List<Album> albums) {
            mGalleryImagesList.addAll(albums);
        }

        @Override
        public FacebookAlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_gallery_albums, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTvName.setText(mGalleryImagesList.get(position).name);
            Glide.with(mContext)
                    .load(mGalleryImagesList.get(position).url).placeholder(R.drawable.ic_empty_picture).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImgAlbum);
        }

        @Override
        public int getItemCount() {
            return mGalleryImagesList.size();
        }

        public Album getItem(int position) {
            return mGalleryImagesList.get(position);
        }

        public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        public List<Album> getGalleryImagesList() {
            return mGalleryImagesList;
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

}
