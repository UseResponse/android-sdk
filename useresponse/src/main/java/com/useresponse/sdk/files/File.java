package com.useresponse.sdk.files;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

public class File {
    private Context context;
    private Uri path;

    public File(Context context, Uri path) {
        this.context = context;
        this.path = path;
    }

    public String getName() {
        String result = null;

        if (path.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(path, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = path.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public String getContent() {
        ArrayList<Byte> content = new ArrayList<>();
        InputStream input = null;

        try {
            input = context.getContentResolver().openInputStream(path);
            byte[] buffer = new byte[1024];
            int length;

            if (input != null) {
                while ((length = input.read(buffer)) > 0) {
                    for (int i = 0; i < length; i++) {
                        content.add(buffer[i]);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage());
        }

        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
                Log.e("UrLog", e.getMessage());
            }
        }

        byte[] contentArray = new byte[content.size()];

        for (int i = 0; i < content.size(); i++) {
            contentArray[i] = content.get(i);
        }

        return Base64.encodeToString(contentArray, 0);
    }

    public boolean isImage() {
        return getName().toLowerCase().matches(".+\\.(jpg|jpeg|png|gif|bmp|ico)$");
    }
}
