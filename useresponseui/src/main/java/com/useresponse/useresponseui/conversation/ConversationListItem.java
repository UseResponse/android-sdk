package com.useresponse.useresponseui.conversation;

public class ConversationListItem {
    private String type;
    private String contentType;
    private String text;
    private String photo;

    public ConversationListItem(String type, String contentType, String text, String photo) {
        this.type = type;
        this.contentType = contentType;
        this.text = text;
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getPhotoUrl() {
        return photo;
    }

    public String getContentType() {
        return contentType;
    }
}
