package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ticket {
    private int id;
    private String title;
    private String content;
    private String url;
    private int createdAt;
    private int latestActivity;
    private User author;
    private User responsible;
    private Status status;
    private ArrayList<File> files = new ArrayList<>();

    public Ticket() {
    }

    public Ticket(JSONObject object) throws JSONException {
        id = object.getInt("id");
        title = object.getString("title");
        content = !object.isNull("content") ? object.getString("content") : "";
        url = object.getString("url");
        createdAt = object.getInt("createdAtPosix");
        latestActivity = !object.isNull("latestActivityPosix") ? object.getInt("latestActivityPosix") : createdAt;
        author = new User(object.getJSONObject("author"));
        responsible = !object.isNull("responsible") ? new User(object.getJSONObject("responsible")) : null;
        status = new Status(object.getJSONObject("status"));

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

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getLatestActivity() {
        return latestActivity;
    }

    public User getAuthor() {
        return author;
    }

    public User getResponsible() {
        return responsible;
    }

    public Status getStatus() {
        return status;
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
