package com.picker.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import come.picker.R;
import com.picker.adapter.GalleryAlbumAdapter;
import com.picker.controller.BaseActivity;
import com.picker.model.AlbumsModel;
import com.picker.utils.Constants;
import com.picker.utils.MarginDecoration;
import com.picker.utils.Utils;

public class GalleryAlbumActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private GalleryAlbumAdapter mAdapter;
    private ArrayList<AlbumsModel> mAlbumsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_albums);
        initToolBar(getString(R.string.gallery_album_activity), true);
        initComponents();
    }

    private void initComponents() {

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        getGalleryAlbumImages();
        findViewById(R.id.loading).setVisibility(View.GONE);
        if (mAlbumsModels.size() > 0) {
            findViewById(R.id.noAlbums).setVisibility(View.GONE);
            // create an Object for Adapter
            mAdapter = new GalleryAlbumAdapter(GalleryAlbumActivity.this, mAlbumsModels);
            // set the adapter object to the RecyclerView
            mRecyclerView.setAdapter(mAdapter);
            initCallBacks();
        } else
            findViewById(R.id.noAlbums).setVisibility(View.VISIBLE);
    }

    private void initCallBacks() {
        mAdapter.setOnItemClickListener(new GalleryAlbumAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                // do something with position
                Intent galleryAlbumsIntent = new Intent(GalleryAlbumActivity.this, ShowAlbumImagesActivity.class);
                galleryAlbumsIntent.putExtra("position", position);
                galleryAlbumsIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                galleryAlbumsIntent.putExtra("albumsList", mAlbumsModels);
                galleryAlbumsIntent.putExtra(Constants.DATA, getIntent().getIntExtra(Constants.DATA, 1));
                startActivity(galleryAlbumsIntent);
                finish();
            }
        });
    }

    private void getGalleryAlbumImages() {
        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
        mAlbumsModels = Utils.getAllDirectoriesWithImages(imageCursor);
        imageCursor.close();
    }

}