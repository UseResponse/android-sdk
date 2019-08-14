package com.useresponse.sdk.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.useresponse.sdk.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotosLoader {

    private Resources resources;
    private Map<String, Item> queue;

    public PhotosLoader(Resources resources) {
        this.resources = resources;
        queue = new HashMap<>();
    }

    public void process(String url, ImageView photo) {
        if (url.toLowerCase().endsWith(".svg") || url.toLowerCase().startsWith("data:")) {
            photo.setImageResource(R.drawable.ic_default_avatar);
            return;
        }

        Item item = queue.get(url);

        if (item == null) {
            item = new Item();
            item.images.add(photo);
            queue.put(url, item);
            new DownloadTask(url).execute();
        } else if (item.state == 0) {
            item.images.add(photo);
        } else {
            photo.setImageDrawable(item.bitmap);
        }
    }

    private class Item {
        int state;
        RoundedBitmapDrawable bitmap;
        List<ImageView> images;

        Item() {
            state = 0;
            bitmap = null;
            images = new ArrayList<>();
        }
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        String url;
        Bitmap bitmap = null;

        DownloadTask(String url) {
            super();
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (bitmap == null) {
                return;
            }

            Item item = queue.get(url);
            item.bitmap = RoundedBitmapDrawableFactory.create(resources, bitmap);
            item.bitmap.setCircular(true);

            for (ImageView photo : item.images) {
                photo.setImageDrawable(item.bitmap);
            }

            item.state = 1;
            item.images = null;
            queue.put(url, item);
        }
    }
}
