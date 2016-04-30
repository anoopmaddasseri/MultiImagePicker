package com.picker.facebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import come.picker.R;
import com.picker.adapter.FacebookAlbumImagesAdapter;
import com.picker.controller.BaseActivity;
import com.picker.model.Album;
import com.picker.utils.Constants;
import com.picker.utils.MarginDecoration;
import com.picker.utils.Utils;
import com.picker.view.TextView;

public class FacebookAlbumImagesActivity extends BaseActivity implements FacebookAlbumImagesAdapter.ViewHolder.ClickListener {

    private static final String TAG = "FacebookAlbumImagesActivity";
    private final int MIN_SELECTED = 1;
    private int MAX_SELECTED = 1;
    private int mImagesCount = 0;
    private int arraySize = 0;
    private int offset = 0;
    private int limit = 20;

    private String mToolbarTittle;

    private RecyclerView mRecyclerView;
    private Button mUpload;
    private TextView mTitle;

    private FacebookAlbumImagesAdapter mAdapter;

    private Album mAlbums;
    private ArrayList<Bitmap> mShareImages = new ArrayList<>();
    private ArrayList<String> fbImages = new ArrayList<>();
    private ArrayList<String> mSelectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery_images);
        initToolBar(getString(R.string.fb_albums), true);
        initComponents();
    }

    private void initComponents() {
        mUpload = (Button) findViewById(R.id.upload);
        mTitle = getToolbarTitleView();
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));

        mTitle.setSingleLine(true);
        mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTitle.setMarqueeRepeatLimit(-1);
        mTitle.setHorizontallyScrolling(true);
        mTitle.setSelected(true);

        mAlbums = (Album) getIntent().getSerializableExtra("albumsList");
        mImagesCount = Integer.parseInt(mAlbums.count);

        MAX_SELECTED = getIntent().getIntExtra(Constants.DATA, MAX_SELECTED);
        mToolbarTittle = mAlbums.name == null ? getString(R.string.fb_albums) : mAlbums.name + " ( " + mImagesCount + " ) ";

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // create an Object for Adapter
        mAdapter = new FacebookAlbumImagesAdapter(FacebookAlbumImagesActivity.this, this);
        // set the adapter object to the RecyclerView
        mRecyclerView.setAdapter(mAdapter);

        initViews();
        initCallBacks();

    }

    private void initCallBacks() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mImagesCount > offset + limit) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    //position starts at 0
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == gridLayoutManager.getItemCount() - 1) {
                        offset = offset + limit;
                        if (mImagesCount - offset >= 20)
                            limit = 20;
                        else
                            limit = mImagesCount - offset;
                        getFacebookImages(mAlbums.id, offset, limit);
                    }
                }
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int size = mAdapter.getSelectedItemCount();
                if (size < MIN_SELECTED) {
                    Snackbar.make(findViewById(R.id.parent), String.format(getString(R.string.error_profile_image), MIN_SELECTED),
                            Snackbar.LENGTH_LONG).show();
                } else {
                    mSelectedFiles = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        FileOutputStream out;
                        String filename = getFilename(FacebookAlbumImagesActivity.this);
                        try {
                            out = new FileOutputStream(filename);
                            // write the compressed bitmap at the destination specified by filename.
                            mShareImages.get(i).compress(Bitmap.CompressFormat.JPEG, 80, out);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mSelectedFiles.add(filename);
                    }
                    recycle();
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(Constants.DATA, mSelectedFiles);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private String getFilename(final Context context) {
        return (Utils.getCompressedPicPath(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    private void initViews() {
        getFacebookImages(mAlbums.id, offset, limit);
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
    public void onItemClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);
        return false;
    }


    private void toggleSelection(int position) {
        ShareTask shareTask = new ShareTask(position);
        shareTask.execute(mAdapter.getItem(position));
    }

    private void recycle() {
        mShareImages.clear();
        mShareImages = null;
    }

    private void getFacebookImages(String albumId, int offsetValue, int limitValue) {
        mTitle.setText(getString(R.string.loading));
        offset = offsetValue;
        limit = limitValue;
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        parameters.putString("offset", String.valueOf(offset));
        parameters.putString("limit", String.valueOf(limit));

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                      /* handle the result */
                        Log.e("TAG", "Facebook Photos response: " + response);
                        try {
                            if (response.getError() == null) {
                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");
                                        if (jaImages.length() > 0) {
                                            arraySize = fbImages.size();
                                            fbImages.add(jaImages.getJSONObject(0).getString("source"));
                                        }
                                    }
                                }
                                mAdapter.setItems(fbImages);
                                if (offset != 0)
                                    mAdapter.notifyItemRangeInserted(arraySize, fbImages.size() - arraySize);
                                else
                                    mAdapter.notifyDataSetChanged();
                                mTitle.setText(mToolbarTittle);
                            } else {
                                Log.e("TAG", response.getError().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    private class ShareTask extends AsyncTask<String, Void, Bitmap> {
        private int position;

        public ShareTask(int position) {
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0]; // should be easy to extend to share multiple images at once
            try {
                return Glide.
                        with(FacebookAlbumImagesActivity.this).
                        load(url).
                        asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).
                        into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL). // Width and height
                        get();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Snackbar.make(findViewById(R.id.parent), getString(R.string.warning_image_select),
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            // startActivity probably needs UI thread
            if (mAdapter.getSelectedItemCount() < MAX_SELECTED) {
                mAdapter.toggleSelection(position);
                if (mAdapter.isSelected(position)) {
                    mShareImages.add(result);
                } else {
                    mShareImages.remove(result);
                }
            } else {
                if (mAdapter.isSelected(position)) {
                    mAdapter.toggleSelection(position);
                    mShareImages.remove(result);
                } else
                    Snackbar.make(findViewById(R.id.parent), String.format(getString(R.string.warning_image_exceed), MAX_SELECTED),
                            Snackbar.LENGTH_LONG).show();
            }

        }

    }
}





