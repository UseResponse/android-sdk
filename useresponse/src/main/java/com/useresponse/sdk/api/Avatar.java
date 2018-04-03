package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Avatar {
    private String tiny;
    private String small;
    private String medium;
    private String big;

    public Avatar() {
    }

    public Avatar(JSONObject object) throws JSONException {
        this.tiny = object.getString("tiny");
        this.small = object.getString("small");
        this.medium = object.getString("medium");
        this.big = object.getString("big");
    }

    public String getTiny() {
        return tiny;
    }

    public String getSmall() {
        return small;
    }

    public String getMedium() {
        return medium;
    }

    public String getBig() {
        return big;
    }
}
