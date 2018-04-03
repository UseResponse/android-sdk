package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private int id;
    private String ownership;
    private String type;
    private String name;
    private String slug;

    public Category() {
    }

    public Category(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.ownership = object.getString("ownership");
        this.type = !object.isNull("type") ? object.getString("type") : "";
        this.name = object.getString("name");
        this.slug = object.getString("slug");
    }

    public int getId() {
        return id;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }
}
