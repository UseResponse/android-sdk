package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String name;
    private String shortName;
    private String apiKey;
    private Avatar avatar;

    public User() {
    }

    public User(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.name = object.getString("name");
        this.shortName = object.getString("shortName");
        this.apiKey = object.has("apiKey") ? object.getString("apiKey") : "";
        this.avatar = new Avatar(object.getJSONObject("avatar"));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
