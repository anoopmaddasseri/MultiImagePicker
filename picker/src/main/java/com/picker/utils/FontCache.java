package com.picker.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Caching font from asset
 */
public final class FontCache {
    private static HashMap<String, Typeface> mFontCache = new HashMap<>();

    public FontCache() {
        throw new RuntimeException("Final Class, cannot be instantiated !");
    }

    public static Typeface getTypeface(String fontName, Context context) {
        Typeface typeface = mFontCache.get(fontName);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                return null;
            }
            mFontCache.put(fontName, typeface);
        }
        return typeface;
    }
}