package com.useresponse.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.useresponse.sdk.NotificationsService;
import com.useresponse.sdk.api.Api;
import com.useresponse.sdk.api.IdentityData;
import com.useresponse.sdk.api.User;

import org.json.JSONArray;
import org.json.JSONObject;

public class UseResponse {
    private static boolean initialized = false;
    private static String apiKey = null;
    private static JSONObject config = null;
    private static int forumId = -1;

    public static boolean init(Context context) {
        if (initialized) {
            return true;
        }

        try {
            Api.setApiUrl(getBaseUrl(context) + "/api/4.0");
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        initialized = true;

        return true;
    }

    public static void initIdentity(Activity activity, boolean createIfNotSet) throws Exception {
        if (hasIdentity(activity)) {
            Api.setApiKey(apiKey);
            activity.startService(new Intent(activity, NotificationsService.class));
            return;
        }

        User user = Api.getIdentity(createIfNotSet);

        if (user != null && user.getApiKey().length() > 0) {
            IdentityData identityData = Api.getIdentityData();
            apiKey = user.getApiKey();
            SharedPreferences.Editor editor = activity.getSharedPreferences("useresponseui", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.putString("urApiKey", apiKey);
            editor.putString("userToken", identityData != null ? identityData.getToken() : "");
            editor.apply();
            Api.setApiKey(apiKey);
            activity.startService(new Intent(activity, NotificationsService.class));
        }
    }

    public static boolean hasIdentity(Activity activity) {
        if (apiKey == null) {
            SharedPreferences preferences = activity.getSharedPreferences("useresponseui", Context.MODE_PRIVATE);
            apiKey = preferences.getString("urApiKey", "");
            String userToken = preferences.getString("userToken", "");

            if (apiKey.length() > 0) {
                IdentityData identityData = Api.getIdentityData();

                if (identityData == null && userToken.length() > 0 || identityData != null && !identityData.getToken().equals(userToken)) {
                    apiKey = "";
                }
            }
        }

        return apiKey.length() > 0;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getBaseUrl(Context context) {
        try {
            return getConfig(context).getString("baseUrl");
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return "";
    }

    public static String getPushWsUrl(Context context) {
        try {
            return getConfig(context).getString("pushWsUrl");
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return "";
    }

    public static String getChatWsUrl(Context context) {
        try {
            return getConfig(context).getString("chatWsUrl");
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return "";
    }

    public static boolean hasModule(Context context, String name) {
        try {
            JSONArray modules = getConfig(context).getJSONArray("modules");

            for (int i = 0; i < modules.length(); i++) {
                if (modules.getString(i).equals(name)) {
                    return true;
                }
            }

        } catch (Exception e) {
            Log.e("UrLog", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public static int getForumId(Context context) {
        if (forumId >= 0) {
            return forumId;
        }

        try {
            forumId = getConfig(context).getInt("forumId");
        } catch (Exception e) {
            forumId = 0;
            Log.e("UrLog", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return forumId;
    }

    public static void setForumId(int forumId) {
        UseResponse.forumId = forumId;
    }

    public static JSONObject getConfig(Context context) throws Exception {
        if (config == null) {
            config = new JSONObject(Assets.getContent(context, "config.json"));
        }

        return config;
    }
}