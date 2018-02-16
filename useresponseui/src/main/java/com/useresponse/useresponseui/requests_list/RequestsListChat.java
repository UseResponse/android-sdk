package com.useresponse.useresponseui.requests_list;

import useresponseapi.ModelChat;
import useresponseapi.ModelChatMessage;

public class RequestsListChat implements RequestsListInterface {
    private ModelChat chat;
    private ModelChatMessage lastMessage;

    public RequestsListChat(ModelChat chat) {
        this.chat = chat;
        this.lastMessage = chat.getLastMessage();
    }

    @Override
    public String getRowType() {
        return "chat";
    }

    @Override
    public String getTitle() {
        return lastMessage.getAuthor().getShortName();
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
        return (int)chat.getId();
    }

    @Override
    public int getUpdatedAt() {
        return (int)chat.getUpdatedAt();
    }
}
