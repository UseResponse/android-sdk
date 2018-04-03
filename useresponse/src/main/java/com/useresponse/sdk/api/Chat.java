package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Chat {
    private int id;
    private int createdAt;
    private int updatedAt;
    private int completedAt;
    private User author;
    private User responsible;
    private ChatMessage lastMessage;
    private boolean seenByClient;

    public Chat() {
    }

    public Chat(JSONObject object) throws JSONException {
        id = object.getInt("externalId");
        createdAt = object.getInt("createdAtPosix");
        updatedAt = !object.isNull("updatedAtPosix") ? object.getInt("updatedAtPosix") : createdAt;
        completedAt = !object.isNull("completedAtPosix") ? object.getInt("completedAtPosix") : 0;
        author = new User(object.getJSONObject("author"));
        responsible = !object.isNull("responsible") ? new User(object.getJSONObject("responsible")) : null;
        lastMessage = !object.isNull("lastMessage") ? new ChatMessage(object.getJSONObject("lastMessage")) : null;
        seenByClient = object.getBoolean("seenByClient");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCompletedAt() {
        return completedAt;
    }

    public User getAuthor() {
        return author;
    }

    public User getResponsible() {
        return responsible;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public boolean isSeenByClient() {
        return seenByClient;
    }

    public boolean isCompleted() {
        return completedAt > 0;
    }
}
