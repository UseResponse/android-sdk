package com.useresponse.sdk.requests_list;

import com.useresponse.sdk.api.Status;
import com.useresponse.sdk.api.Ticket;

public class RequestsListTicket implements RequestsListInterface {
    private Ticket ticket;

    public RequestsListTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String getRowType() {
        return "ticket";
    }

    @Override
    public String getTitle() {
        return ticket.getAuthor().getName();
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
        return ticket.getId();
    }

    @Override
    public int getUpdatedAt() {
        return ticket.getLatestActivity();
    }

    public Status getStatus() {
        return ticket.getStatus();
    }
}
