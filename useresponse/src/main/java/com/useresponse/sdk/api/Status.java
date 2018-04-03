package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Status {
    private int id;
    private String title;
    private String slug;
    private boolean isDefault;
    private boolean isClosed;
    private String bgColor;
    private String textColor;

    public Status() {
    }

    public Status(JSONObject object) throws JSONException {
        id = object.getInt("id");
        title = object.getString("title");
        slug = object.getString("slug");
        isDefault = object.getBoolean("isDefault");
        isClosed = object.getBoolean("isClosed");

        JSONObject color = object.getJSONObject("color");
        bgColor = color.getString("background");
        textColor = color.getString("text");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getTextColor() {
        return textColor;
    }
}
