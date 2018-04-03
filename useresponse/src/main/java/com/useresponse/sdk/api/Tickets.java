package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tickets extends PaginationAbstract {
    private ArrayList<Ticket> tickets = new ArrayList<>();

    public Tickets() {
        super();
    }

    public Tickets(JSONObject object) throws JSONException {
        super(object);

        if (!object.isNull("data")) {
            JSONArray responseChats = object.getJSONArray("data");

            for (int i = 0; i < responseChats.length(); i++) {
                tickets.add(new Ticket(responseChats.getJSONObject(i)));
            }
        }
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }
}
