package com.useresponse.sdk.utils;

import android.content.Context;

import com.useresponse.sdk.api.Article;
import com.useresponse.sdk.api.Chat;
import com.useresponse.sdk.api.Chats;
import com.useresponse.sdk.api.CustomField;
import com.useresponse.sdk.api.Tickets;

import java.util.ArrayList;

public class Cache {
    private static ArrayList<Article> allArticles;
    private static Tickets allTickets;
    private static Chats allChats;
    private static ArrayList<CustomField> ticketCustomFields;
    private static ImagesLoader imagesLoader = null;
    private static PhotosLoader photosLoader = null;
    private static int currentPage = 1;
    private static int chatPages = 1;
    private static int ticketPages = 1;

    public static void clear()
    {
        allArticles = null;
        allTickets = null;
        allChats = null;
        ticketCustomFields = null;
        imagesLoader = null;
        photosLoader = null;
        currentPage = 1;
        chatPages = 1;
        ticketPages = 1;
    }

    public static void setAllArticles(ArrayList<Article> articles) {
        allArticles = articles;
    }

    public static void setAllTickets(Tickets tickets) {
        allTickets = tickets;
    }

    public static void setAllChats(Chats chats) {
        allChats = chats;
    }

    public static ArrayList<Article> getAllArticles() {
        return allArticles;
    }

    public static Tickets getAllTickets() {
        return allTickets;
    }

    public static Chats getAllChats() {
        return allChats;
    }

    public static ArrayList<CustomField> getTicketCustomFields() {
        return ticketCustomFields;
    }

    public static void setTicketCustomFields(ArrayList<CustomField> ticketCustomFields) {
        Cache.ticketCustomFields = ticketCustomFields;
    }

    public static ImagesLoader getImagesLoader() {
        if (imagesLoader == null) {
            imagesLoader = new ImagesLoader();
        }

        return imagesLoader;
    }

    public static PhotosLoader getPhotosLoader(Context context) {
        if (photosLoader == null) {
            photosLoader = new PhotosLoader(context.getResources());
        }

        return photosLoader;
    }

    public static void touchChat(int id) {
        if (allChats == null) {
            return;
        }

        ArrayList<Chat> chats = allChats.getChats();

        if (chats.size() <= 1) {
            return;
        }

        for (int i = 0; i < chats.size(); i++) {
            Chat chat = chats.get(i);

            if (chats.get(i).getId() == id) {
                chat.setUpdatedAt((int)(System.currentTimeMillis() / 1000L));
                break;
            }
        }
    }

    public static void setCurrentPage(int page) {
        currentPage = page;
    }

    public static int getCurrentPage() {
        return currentPage;
    }

    public static void setChatPages(int pages) {
        chatPages = pages;
    }

    public static int getChatPages() {
        return chatPages;
    }

    public static void setTicketPages(int pages) {
        ticketPages = pages;
    }

    public static int getTicketPages() {
        return ticketPages;
    }
}
