package com.useresponse.useresponseui.utils;

import useresponseapi.ModelArticleIterator;
import useresponseapi.ModelChatIterator;
import useresponseapi.ModelCustomFieldIterator;
import useresponseapi.ModelTicketIterator;

public class Cache {
    private static ModelArticleIterator allArticles;
    private static ModelTicketIterator allTickets;
    private static ModelChatIterator allChats;
    private static ModelCustomFieldIterator ticketCustomFields;

    public static void setAllArticles(ModelArticleIterator articles) {
        allArticles = articles;
    }

    public static void setAllTickets(ModelTicketIterator tickets) {
        allTickets = tickets;
    }

    public static void setAllChats(ModelChatIterator chats) {
        allChats = chats;
    }
    public static ModelArticleIterator getAllArticles() {
        if (allArticles != null) {
            allArticles.rewind();
        }

        return allArticles;
    }

    public static ModelTicketIterator getAllTickets() {
        if (allTickets != null) {
            allTickets.rewind();
        }

        return allTickets;
    }

    public static ModelChatIterator getAllChats() {
        if (allChats != null) {
            allChats.rewind();
        }

        return allChats;
    }

    public static ModelCustomFieldIterator getTicketCustomFields() {
        if (ticketCustomFields != null) {
            ticketCustomFields.rewind();
        }

        return ticketCustomFields;
    }

    public static void setTicketCustomFields(ModelCustomFieldIterator ticketCustomFields) {
        Cache.ticketCustomFields = ticketCustomFields;
    }
}
