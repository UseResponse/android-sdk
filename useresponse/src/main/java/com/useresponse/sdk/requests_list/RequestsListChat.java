package com.useresponse.sdk.requests_list;

import com.useresponse.sdk.api.Chat;
import com.useresponse.sdk.api.ChatMessage;

public class RequestsListChat implements RequestsListInterface {
    private Chat chat;
    private ChatMessage lastMessage;

    public RequestsListChat(Chat chat) {
        this.chat = chat;
        this.lastMessage = chat.getLastMessage();
    }

    @Override
    public String getRowType() {
        return "chat";
    }

    @Override
    public String getTitle() {
        return lastMessage.getAuthor().getName();
    }

    @Override
    public String getDescription() {
        return lastMessage.getType().equals("text") ? lastMessage.getContent() : lastMessage.getType();
    }

    @Override
    public String getPhotoUrl() {
        return lastMessage.getAuthor().getAvatar().getMedium();
    }

    @Override
    public int getId() {
        return chat.getId();
    }

    @Override
    public int getUpdatedAt() {
        return chat.getUpdatedAt();
    }
}
