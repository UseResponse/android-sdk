package com.useresponse.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesLoader {

    private Map<String, Item> queue;

    public ImagesLoader() {
        queue = new HashMap<>();
    }

    public void process(String url, ImageView photo) {
        Item item = queue.get(url);

        if (item == null) {
            item = new Item();
            item.images.add(photo);
            queue.put(url, item);
            new DownloadTask(url).execute();
        } else if (item.state == 0) {
            item.images.add(photo);
        } else {
            photo.setImageBitmap(item.bitmap);
        }
    }

    private class Item {
        int state;
        Bitmap bitmap;
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
                Log.e("UrLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (bitmap == null) {
                return;
            }

            Item item = queue.get(url);
            item.bitmap = bitmap;

            for (ImageView photo : item.images) {
                photo.setImageBitmap(item.bitmap);
            }

            item.state = 1;
            item.images = null;
            queue.put(url, item);
        }
    }
}
