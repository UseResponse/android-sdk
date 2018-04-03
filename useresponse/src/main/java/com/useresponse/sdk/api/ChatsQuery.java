package com.useresponse.sdk.api;

public class ChatsQuery {
    private int page = 1;

    public ChatsQuery setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPage() {
        return page;
    }
}
