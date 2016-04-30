package com.picker.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.picker.model.AlbumsModel;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public final class Utils {

    private static final String TAG = "Utils";

    public Utils() {
        throw new RuntimeException("Final Class, cannot be instantiated !");
    }

    private static final String COMPRESSED_FOLDER_NAME = "CompressedImages";

    /**
     * Method to get application folder path
     *
     * @param context
     * @return
     */
    public static File getAppDirectory(Context context) {
        return context.getExternalFilesDir(null);
    }

    /**
     * @param context
     * @return
     */
    public static File getCompressedPicPath(Context context) {
        File file = new File(getAppDirectory(context), COMPRESSED_FOLDER_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * @param cursor
     * @return
     */
    public static ArrayList<AlbumsModel> getAllDirectoriesWithImages(
            Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        final int size = cursor.getCount();

        TreeSet<String> folderPathList = new TreeSet<>();
        ArrayList<AlbumsModel> albumsModels = new ArrayList<>();
        HashMap<String, AlbumsModel> map = new HashMap<>();

        String imgPath, folderPath;
        AlbumsModel tempAlbumsModel;
        for (int i = 0; i < size; i++) {
            imgPath = cursor.getString(0).trim();
            folderPath = imgPath.substring(0, imgPath.lastIndexOf("/"));
            if (folderPathList.add(folderPath)) {
                AlbumsModel gm = new AlbumsModel();
                /*String folderName = gm.getFolderName();
                String folderImagePath = gm.getFolderName();*/
                gm.folderName = folderPath.substring(
                        folderPath.lastIndexOf("/") + 1, folderPath.length());
                gm.folderImages.add(imgPath);
                gm.folderImagePath = imgPath;
                albumsModels.add(gm);
                map.put(folderPath, gm);
            } else if (folderPathList.contains(folderPath)) {
                tempAlbumsModel = map.get(folderPath);
                tempAlbumsModel.folderImages.add(imgPath);
            }
            cursor.moveToNext();
        }
        return albumsModels;
    }

    /**
     * Used to generate package hash
     *
     * @param context The application's environment.
     */
    private void printKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


}