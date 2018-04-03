package com.useresponse.sdk.api;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

public class ApiHelper {
    public static ArrayList<Category> getArticlesCategories(ArrayList<Article> articles) {
        ArrayList<Category> categories = new ArrayList<>();
        SparseBooleanArray catIds = new SparseBooleanArray();

        for (Article article : articles) {
            if (!catIds.get(article.getCategory().getId())) {
                categories.add(article.getCategory());
                catIds.put(article.getCategory().getId(), true);
            }
        }

        return categories;
    }

    public static boolean hasArticlesType(ArrayList<Article> articles, String type) {
        for (Article article : articles) {
            if (article.getType().getSlug().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<Message> bbCodeToMessages(String text) {
        ArrayList<Message> messages = new ArrayList<>();

        final String explodeToken = "---UREXPLODETOKEN---";
        final String imgToken = "---URIMGTOKEN---";

        text = text.replaceAll("\\[li](.*?)\\[/li]", "• $1\n");
        text = text.replaceAll("\\[ul]|\\[/ul]|\\[ol]|\\[/ol]|\\[pre]|\\[/pre]", explodeToken);
        text = text.replaceAll("\\[img](.*?)\\[/img]", explodeToken + imgToken + "$1" + explodeToken);
        text = text.replaceAll("\\[url=(.*?)](.*?)\\[/url]", "$2 ($1)");
        text = text.replaceAll("\\[.*?]", "");
        text = text.replaceAll("\n+", "\n");

        String[] parts = TextUtils.split(text, explodeToken);

        for (String part : parts) {
            part = part.trim();

            if (part.length() > 0) {
                Message message = new Message();

                if (part.length() > imgToken.length() && part.indexOf(imgToken) == 0) {
                    message.setType("image");
                    message.setContent(part.substring(imgToken.length()));
                } else {
                    message.setType("text");
                    message.setContent(part);
                }

                messages.add(message);
            }
        }

        return messages;
    }
}
