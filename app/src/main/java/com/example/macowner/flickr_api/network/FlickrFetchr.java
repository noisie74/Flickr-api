package com.example.macowner.flickr_api.network;

import android.net.Uri;
import android.util.Log;

import com.example.macowner.flickr_api.model.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikhail on 9/29/16.
 */
public class FlickrFetchr {

  private static final String TAG = "FlickrFetchr";
  private static final String API_KEY = "f55905acfce160a08a05c0d94a1d2961";
  private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
  private static final String SEARCH_METHOD = "flickr.photos.search";
  private static final Uri ENDPOINT = Uri
      .parse("https://api.flickr.com/services/rest/")
      .buildUpon()
      .appendQueryParameter("api_key", API_KEY)
      .appendQueryParameter("format", "json")
      .appendQueryParameter("nojsoncallback", "1")
      .appendQueryParameter("extras", "url_s")
      .build();

  public byte[] getUrlBytes(String urlSpec) throws IOException {

    URL url = new URL(urlSpec);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InputStream in = connection.getInputStream();

      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException(connection.getResponseMessage() +
            ": with " +
            urlSpec);
      }

      int bytesRead = 0;
      byte[] buffer = new byte[1024];
      while ((bytesRead = in.read(buffer)) > 0) {
        out.write(buffer, 0, bytesRead);
      }
      out.close();
      return out.toByteArray();
    } finally {
      connection.disconnect();
    }
  }

  public String getUrlString(String urlSpec) throws IOException {
    return new String(getUrlBytes(urlSpec));

  }

  public List<GalleryItem> downloadGalleryItems(String url) {

    List<GalleryItem> items = new ArrayList<>();

    try {

      String jsonString = getUrlString(url);
      Log.i(TAG, "Received JSON: " + jsonString);
      JSONObject jsonBody = new JSONObject(jsonString);
      parseItems(items, jsonBody);
    } catch (JSONException e) {
      e.printStackTrace();
      Log.e(TAG, "Failed to parse JSON", e);
    } catch (IOException ioe) {
      Log.e(TAG, "Failed to fetch items", ioe);
    }
    return items;
  }

  public List<GalleryItem> fetchRecentPhotos() {
    String url = buildUrl(FETCH_RECENTS_METHOD, null);
    return downloadGalleryItems(url);
  }

  public List<GalleryItem> searchPhotos(String query) {
    String url = buildUrl(SEARCH_METHOD, query);
    return downloadGalleryItems(url);
  }

  private String buildUrl(String method, String query) {
    Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);

    if (method.equals(SEARCH_METHOD)) {
      uriBuilder.appendQueryParameter("text", query);
    }

    return uriBuilder.build().toString();
  }

  private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
      throws IOException, JSONException {

    JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
    JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

    for (int i = 0; i < photoJsonArray.length(); i++) {
      JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

      GalleryItem item = new GalleryItem();
      item.setId(photoJsonObject.getString("id"));
      item.setCaption(photoJsonObject.getString("title"));

      if (!photoJsonObject.has("url_s")) {
        continue;
      }

      item.setUrl(photoJsonObject.getString("url_s"));
      item.setOwner(photoJsonObject.getString("owner"));
      items.add(item);
    }
  }


}



