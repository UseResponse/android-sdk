package com.useresponse.sdk.api;

public class ChatMessagesQuery {
    private int chatId;
    private int page = 1;
    private int topId = 0;

    public ChatMessagesQuery(int chatId) {
        this.chatId = chatId;
    }

    public int getChatId() {
        return chatId;
    }

    public ChatMessagesQuery setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPage() {
        return page;
    }

    public int getTopId() {
        return topId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }
}
