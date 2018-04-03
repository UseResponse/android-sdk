package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadedFile {
    private String token;
    private String url;

    public UploadedFile(JSONObject object) throws JSONException {
        token = object.getString("token");
        url = object.getString("url");
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }
}
