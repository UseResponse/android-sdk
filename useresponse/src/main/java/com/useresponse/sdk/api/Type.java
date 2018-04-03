package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Type {
    private String slug;
    private String title;

    public Type() {
    }

    public Type(JSONObject object) throws JSONException {
        this.slug = object.getString("slug");
        this.title = object.getString("title");
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }
}
