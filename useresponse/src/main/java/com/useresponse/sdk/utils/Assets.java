package com.useresponse.sdk.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Assets {
    public static String getContent(Context context, String fileName) throws Exception {
        InputStream input = context.getResources().getAssets().open(fileName);
        byte[] buffer = new byte[input.available()];
        input.read(buffer);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(buffer);
        output.close();
        input.close();

        return output.toString();
    }
}
