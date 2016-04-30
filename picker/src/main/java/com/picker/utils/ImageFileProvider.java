package com.picker.utils;

import android.net.Uri;

public class ImageFileProvider extends android.support.v4.content.FileProvider {
    @Override
    public String getType(Uri uri) {
        return "image/*";
    }
}