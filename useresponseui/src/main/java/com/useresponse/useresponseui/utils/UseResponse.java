package com.useresponse.useresponseui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.useresponse.useresponseui.NotificationsService;

import useresponseapi.ModelIdentity;
import useresponseapi.ModelUser;
import useresponseapi.Useresponseapi;

public class UseResponse {
    public static final String BASE_URL = "https://dev.useresponse.com";

    private static boolean initialized = false;
    private static String apiKey = null;

    public static boolean init(Context context) {
        if (initialized) {
            return true;
        }

        try {
            ModelIdentity identity = new ModelIdentity();
            identity.setToken("alex");
            Useresponseapi.setIdentityData(identity);
            Useresponseapi.setApiUrl(BASE_URL + "/api/4.0");
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        initialized = true;

        return true;
    }

    public static void initIdentity(Activity activity, boolean createIfNotSet) throws Exception {
        if (hasIdentity(activity)) {
            Useresponseapi.setApiKey(apiKey);
            activity.startService(new Intent(activity, NotificationsService.class));
            return;
        }

        ModelUser user = Useresponseapi.getIdentity(createIfNotSet);

        if (user != null && user.getApiKey().length() > 0) {
            apiKey = user.getApiKey();
            SharedPreferences.Editor editor = activity.getSharedPreferences("useresponseui", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.putString("urApiKey", apiKey);
            editor.apply();
            Useresponseapi.setApiKey(apiKey);
            activity.startService(new Intent(activity, NotificationsService.class));
        }
    }

    public static boolean hasIdentity(Activity activity) {
        if (apiKey == null) {
            apiKey = activity.getSharedPreferences("useresponseui", Context.MODE_PRIVATE).getString("urApiKey", "");
        }

        return apiKey.length() > 0;
    }

    public static String getApiKey() {
        return apiKey;
    }
}
