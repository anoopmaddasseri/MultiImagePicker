package com.picker.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.picker.facebook.FacebookResponse;
import com.picker.model.Album;


public class FacebookService {
    public void getFbAlbum(final FacebookResponse graphUserCallback) {
      /*make API call*/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(album),count");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),  // Facebook access token
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",// User id
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(final GraphResponse response) {
                        Log.e("Response===", response.toString());
                        try {
                            if (response.getError() == null) {
                                new AsyncTask<Void, Void, List<Album>>() {
                                    @Override
                                    protected List<Album> doInBackground(Void... params) {
                                        List<Album> albums = null;
                                        try {
                                            albums = getAlbumsWithName(response
                                                    .getRawResponse());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return albums;
                                    }

                                    protected void onPostExecute(final List<Album> result) {
                                        if (result != null) {
                                            graphUserCallback.onComplete(result);
                                        } else {
                                            List<Album> albumList = null;
                                            graphUserCallback.onComplete(albumList);
                                        }
                                    }

                                }.execute();

                            } else {
                                graphUserCallback.onComplete(null);
                                Log.e("Response===", response.getError().toString());
                            }
                        } catch (Exception e) {
                            graphUserCallback.onComplete(null);
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private List<Album> getAlbumsWithName(String rawResponse)
            throws Exception {
        JSONArray dataArray = new JSONObject(rawResponse).getJSONArray("data");
        List<Album> albumIds = new ArrayList<>();
        int size = dataArray.length();
        JSONObject dataJson;
        for (int i = 0; i < size; i++) {
            dataJson = dataArray.getJSONObject(i);
            String albumName = dataJson.optString("name");
            if (albumName != null) {
                // if (albumName != null||
                // dataJson.optString("name").equals(PROFILE_ALBUM_NAME)) {
                Album album = new Album();
                album.count = dataJson.optString("count");
                album.id = dataJson.optString("id");
                album.name = albumName;
                album.url = dataJson.getJSONObject("picture").getJSONObject("data").optString("url");
                albumIds.add(album);
            }
        }
        return albumIds;
    }

}
