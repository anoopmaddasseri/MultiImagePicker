package com.picker.controller;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import come.picker.R;
import com.picker.utils.Constants;
import com.picker.view.TextView;


public class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initToolBar(String title, boolean showHome) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbarTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showHome);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mTitle.setText(title);
    }

    public void initSharedPreference() {
        if (mSharedPreferences == null)
            mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
        if (mEditor == null) mEditor = mSharedPreferences.edit();
    }


    public void hideKeyBoard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public TextView getToolbarTitleView() {
        return mTitle;
    }


    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public SharedPreferences.Editor getSharedPreferencesEditor() {
        return mEditor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
