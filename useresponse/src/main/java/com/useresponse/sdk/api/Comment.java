package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Comment {
    private int id;
    private String content;
    private int createdAt;
    private User author;
    private ArrayList<File> files = new ArrayList<>();

    public Comment() {
    }

    public Comment(JSONObject object) throws JSONException {
        id = object.getInt("id");
        content = !object.isNull("content") ? object.getString("content") : "";
        createdAt = object.getInt("createdAtPosix");
        author = new User(object.getJSONObject("author"));

        if (!object.isNull("attachments")) {
            JSONArray attachments = object.getJSONArray("attachments");

            for (int i = 0; i < attachments.length(); i++) {
                files.add(new File(attachments.getJSONObject(i)));
            }
        }
    }

    public int getId() {
        return id;
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

    public ArrayList<File> getFiles() {
        return files;
    }

    public ArrayList<Message> getMessages() {
        ArrayList<Message> messages = ApiHelper.bbCodeToMessages(content != null ? content : "");

        for (File file : files) {
            Message message = new Message();
            message.setType(file.isImage() ? "image" : "document");
            message.setContent(file.getUrl());
            messages.add(message);
        }

        return messages;
    }
}
