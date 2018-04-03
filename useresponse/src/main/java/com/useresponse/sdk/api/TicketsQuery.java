package com.useresponse.sdk.api;

public class TicketsQuery {
    private int page = 1;
    private String search;

    public TicketsQuery setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPage() {
        return page;
    }

    public String getSearch() {
        return search != null ? search : "";
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
