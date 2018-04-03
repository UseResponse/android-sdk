package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
    private int id;
    private String type;
    private String content;
    private int createdAt;
    private User author;
    private boolean isSystem;
    private String token;
    private String fileName;

    public ChatMessage() {
    }

    public ChatMessage(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.type = object.getString("type");
        this.content = object.getString("content");
        this.createdAt = object.getInt("createdAtPosix");
        this.author = new User(object.getJSONObject("author"));
        this.isSystem = object.getBoolean("isSystem");
        this.token = object.getString("token");
        this.fileName = !object.isNull("fileName") ? object.getString("fileName") : "";
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public String getToken() {
        return token;
    }

    public String getFileName() {
        return fileName;
    }
}
