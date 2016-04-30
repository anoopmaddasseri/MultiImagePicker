package com.sample.picker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.picker.controller.BaseActivity;
import com.picker.facebook.FacebookAlbumActivity;
import com.picker.gallery.GalleryAlbumActivity;
import com.picker.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private final static int GALLERY_PICKER_CODE = 1;
    private final static int FACEBOOK_PICKER_CODE = 4;
    private final int MAX_SELECTED = 2;
    private final int MIN_SELECTED = 1;

    private View mParent;
    private AppCompatButton mGalleryPicker;
    private AppCompatButton mFacebookPicker;
    private ImageView mImageOne;
    private ImageView mImageTwo;

    private List<String> mSelectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar(getString(R.string.app_name), true);
        initComponents();
        initListeners();
    }

    private void initComponents() {
        mParent = findViewById(R.id.parent);
        mGalleryPicker = (AppCompatButton) findViewById(R.id.galleryPicker);
        mFacebookPicker = (AppCompatButton) findViewById(R.id.facebookPicker);
        mImageOne = (ImageView) findViewById(R.id.imageOne);
        mImageTwo = (ImageView) findViewById(R.id.imageTwo);

        mSelectedFiles = new ArrayList<>();
    }

    private void initListeners() {
        mGalleryPicker.setOnClickListener(this);
        mFacebookPicker.setOnClickListener(this);
    }

    private void browseGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestSDCardAccessPermission(String.format(getString(R.string.permission_rationale), getString(R.string.p_gallery)));
        } else {
            startActivityForResult(new Intent(this, GalleryAlbumActivity.class).putExtra(Constants.DATA, mSelectedFiles.size() == 0 ? MAX_SELECTED : MIN_SELECTED), GALLERY_PICKER_CODE);
        }
    }

    private void browseFacebook() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestSDCardAccessPermission(String.format(getString(R.string.permission_rationale), getString(R.string.p_storage)));
        } else {
            startActivityForResult(new Intent(this, FacebookAlbumActivity.class).putExtra(Constants.DATA, mSelectedFiles.size() == 0 ? MAX_SELECTED : MIN_SELECTED), FACEBOOK_PICKER_CODE);
        }
    }

    private boolean canBrowse() {
        if (mSelectedFiles.size() >= MAX_SELECTED) {
            Snackbar.make(mParent, getString(R.string.error_max_selected, MAX_SELECTED),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void requestSDCardAccessPermission(String message) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mParent, message,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    // Set selected image
    private void setImageBitmap() {
        Glide.with(this).load(mSelectedFiles.get(0)).asBitmap().centerCrop().into(mImageOne);
        if (mSelectedFiles.size() == MAX_SELECTED)
            Glide.with(this).load(mSelectedFiles.get(MIN_SELECTED)).asBitmap().centerCrop().into(mImageTwo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.galleryPicker:
                if (canBrowse())
                    browseGallery();
                break;
            case R.id.facebookPicker:
                if (canBrowse())
                    browseFacebook();
                break;
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mParent, R.string.permission_granted,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mParent, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_RETRY && requestCode == GALLERY_PICKER_CODE) {
            browseGallery();
        } else if (resultCode == Constants.RESULT_RETRY && requestCode == FACEBOOK_PICKER_CODE) {
            browseFacebook();
        } else if ((requestCode == GALLERY_PICKER_CODE || requestCode == FACEBOOK_PICKER_CODE) && resultCode == RESULT_OK) {
            if (data != null) {
                mSelectedFiles.addAll(data.getStringArrayListExtra(Constants.DATA));
                setImageBitmap();
            }
        }
    }

}
