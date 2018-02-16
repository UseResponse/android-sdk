package com.useresponse.useresponseui.requests_list;

import useresponseapi.ModelStatus;
import useresponseapi.ModelTicket;

public class RequestsListTicket implements RequestsListInterface {
    private ModelTicket ticket;

    public RequestsListTicket(ModelTicket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String getRowType() {
        return "ticket";
    }

    @Override
    public String getTitle() {
        return ticket.getAuthor().getShortName();
    }

    @Override
    public String getDescription() {
        return ticket.getTitle();
    }

    @Override
    public String getPhotoUrl() {
        return ticket.getAuthor().getAvatar().getMedium();
    }

    @Override
    public int getId() {
        return (int)ticket.getId();
    }

    @Override
    public int getUpdatedAt() {
        return (int)ticket.getLatestActivity();
    }

    public ModelStatus getStatus() {
        return ticket.getStatus();
    }
}
