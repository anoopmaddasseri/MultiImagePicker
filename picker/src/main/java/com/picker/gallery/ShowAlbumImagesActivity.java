package com.picker.gallery;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.picker.adapter.ShowAlbumImagesAdapter;
import com.picker.controller.BaseActivity;
import com.picker.model.AlbumImages;
import com.picker.model.AlbumsModel;
import com.picker.utils.Constants;
import com.picker.utils.FetchPath;
import com.picker.utils.ImageCompressor;
import com.picker.utils.MarginDecoration;

import java.io.File;
import java.util.ArrayList;

import come.picker.R;

public class ShowAlbumImagesActivity extends BaseActivity implements ShowAlbumImagesAdapter.ViewHolder.ClickListener {

    private static final String TAG = "ShowAlbumImagesActivity";
    private final int MIN_SELECTED = 1;
    private int MAX_SELECTED = 1;
    private int mPosition;

    private RecyclerView mRecyclerView;
    private Button mUpload;

    private ShowAlbumImagesAdapter mAdapter;

    private ArrayList<AlbumsModel> mAlbumsModels;
    private ArrayList<Uri> mShareImages = new ArrayList<>();
    private ArrayList<String> mSelectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery_images);
        initToolBar(getString(R.string.gallery_album_activity), true);
        initComponents();
    }

    private void initComponents() {
        mUpload = (Button) findViewById(R.id.upload);
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));

        MAX_SELECTED = getIntent().getIntExtra(Constants.DATA, MAX_SELECTED);
        mPosition = getIntent().getIntExtra("position", 0);
        mAlbumsModels = (ArrayList<AlbumsModel>) getIntent().getSerializableExtra("albumsList");

        getToolbarTitleView().setText(mAlbumsModels.get(mPosition).folderName == null ? getString(R.string.show_album_images_activity) : mAlbumsModels.get(mPosition).folderName);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // create an Object for Adapter
        mAdapter = new ShowAlbumImagesAdapter(ShowAlbumImagesActivity.this, getAlbumImages(), this);
        // set the adapter object to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        initCallBacks();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(Constants.RESULT_RETRY, intent);
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Constants.RESULT_RETRY, intent);
        super.onBackPressed();
    }

    private void initCallBacks() {
        mUpload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final int size = mAdapter.getSelectedItemCount();
                if (size < MIN_SELECTED) {
                    Snackbar.make(findViewById(R.id.parent), String.format(getString(R.string.error_profile_image), MIN_SELECTED),
                            Snackbar.LENGTH_LONG).show();
                } else {
                    mSelectedFiles = new ArrayList<>();
                    // TODO use background thread to compress
                    for (int i = 0; i < size; i++) {
                        mSelectedFiles.add(ImageCompressor.compressImage(ShowAlbumImagesActivity.this, FetchPath.getPath(ShowAlbumImagesActivity.this, mShareImages.get(i)), null));
                    }
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(Constants.DATA, mSelectedFiles);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return this.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    private ArrayList<AlbumImages> getAlbumImages() {
        Object[] abc = mAlbumsModels.get(mPosition).folderImages.toArray();
        ArrayList<AlbumImages> paths = new ArrayList<>();
        for (Object anAbc : abc) {
            AlbumImages albumImages = new AlbumImages();
            albumImages.setAlbumImages((String) anAbc);
            paths.add(albumImages);
        }
        return paths;
    }

    @Override
    public void onItemClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        if (mAdapter.getSelectedItemCount() < MAX_SELECTED) {
            Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
            String path = uriPath.getPath();
            File imageFile = new File(path);
            if (imageFile.exists() && imageFile.length() > 0) {
                mAdapter.toggleSelection(position);
                Uri uri = getImageContentUri(imageFile);
                if (mAdapter.isSelected(position)) {
                    mShareImages.add(uri);
                } else {
                    mShareImages.remove(uri);
                }
            } else
                Snackbar.make(findViewById(R.id.parent), getString(R.string.warning_image_select),
                        Snackbar.LENGTH_LONG).show();
        } else {
            if (mAdapter.isSelected(position)) {
                mAdapter.toggleSelection(position);
                Uri uriPath = Uri.parse(mAdapter.getAlbumImagesList().get(position).getAlbumImages());
                String path = uriPath.getPath();
                File imageFile = new File(path);
                Uri uri = getImageContentUri(imageFile);
                mShareImages.remove(uri);
            } else
                Snackbar.make(findViewById(R.id.parent), String.format(getString(R.string.warning_image_exceed), MAX_SELECTED),
                        Snackbar.LENGTH_LONG).show();
        }
    }


}