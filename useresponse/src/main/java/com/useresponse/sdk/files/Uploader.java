package com.useresponse.sdk.files;

import android.app.Activity;
import android.content.Intent;

public class Uploader {
    public static final int PICK_FILE_REQUEST = 1;

    public static void pickFile(Activity activity) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }
}
