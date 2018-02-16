package com.useresponse.useresponseui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.useresponse.useresponseui.requests_list.RequestsListAdapter;
import com.useresponse.useresponseui.requests_list.RequestsListChat;
import com.useresponse.useresponseui.requests_list.RequestsListInterface;
import com.useresponse.useresponseui.requests_list.RequestsListTicket;
import com.useresponse.useresponseui.utils.Cache;
import com.useresponse.useresponseui.utils.UseResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import useresponseapi.ChatsQuery;
import useresponseapi.ModelChat;
import useresponseapi.ModelChatIterator;
import useresponseapi.ModelTicket;
import useresponseapi.ModelTicketIterator;
import useresponseapi.TicketsQuery;
import useresponseapi.Useresponseapi;

public class RequestsActivity extends AppCompatActivity {

    public static boolean needRefresh = false;

    ListView requestsList;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        setTitle(getString(R.string.requests_header_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!UseResponse.init(this)) {
            return;
        }

        requestsList = (ListView)findViewById(R.id.requestsList);
        loader = (ProgressBar)findViewById(R.id.requestsLoader);

        if (Cache.getAllTickets() == null || Cache.getAllChats() == null) {
            requestsList.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            (new LoadTask()).execute();
        } else {
            renderRequests();
        }

        FloatingActionButton newRequest = (FloatingActionButton)findViewById(R.id.requestNew);
        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, CreateTicketActivity.class);
                RequestsActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.menuNewTicket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, CreateTicketActivity.class);
                RequestsActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.menuNewChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, RequestActivity.class);
                intent.putExtra("requestType", "chat");
                intent.putExtra("requestId", 0);
                RequestsActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (RequestsActivity.needRefresh) {
            RequestsActivity.needRefresh = false;

            if (Cache.getAllTickets() == null || Cache.getAllChats() == null) {
                requestsList.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                (new LoadTask()).execute();
            } else {
                renderRequests();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void renderRequests() {
        ModelTicketIterator tickets = Cache.getAllTickets();
        ModelChatIterator chats = Cache.getAllChats();
        final ArrayList<RequestsListInterface> requestsArray = new ArrayList<>();

        while (tickets.isValid()) {
            ModelTicket ticket = tickets.getNext();
            requestsArray.add(new RequestsListTicket(ticket));
        }

        while (chats.isValid()) {
            ModelChat chat = chats.getNext();
            requestsArray.add(new RequestsListChat(chat));
        }

        Collections.sort(requestsArray, new Comparator<RequestsListInterface>() {
            @Override
            public int compare(RequestsListInterface o1, RequestsListInterface o2) {
                return o1.getUpdatedAt() < o2.getUpdatedAt() ? 1 : -1;
            }
        });

        loader.setVisibility(View.GONE);
        requestsList.setVisibility(View.VISIBLE);
        requestsList.setAdapter(new RequestsListAdapter(this, requestsArray));

        requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestsListInterface request = requestsArray.get(position);
                Intent intent = new Intent(RequestsActivity.this, RequestActivity.class);
                intent.putExtra("requestType", request.getRowType());
                intent.putExtra("requestId", request.getId());
                RequestsActivity.this.startActivity(intent);
            }
        });
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {
        private String error;

        @Override
        protected Void doInBackground(Void... params) {
            TicketsQuery ticketsQuery = new TicketsQuery();
            ChatsQuery chatsQuery = new ChatsQuery();
            ticketsQuery.setPage(1);
            chatsQuery.setPage(1);

            try {
                UseResponse.initIdentity(RequestsActivity.this, false);
                Cache.setAllTickets(
                        UseResponse.hasIdentity(RequestsActivity.this)
                                ? Useresponseapi.getTickets(ticketsQuery) : new ModelTicketIterator()
                );

                Cache.setAllChats(
                        UseResponse.hasIdentity(RequestsActivity.this)
                                ? Useresponseapi.getChats(chatsQuery) : new ModelChatIterator()
                );
            } catch (Exception e) {
                error = e.getMessage();
                Log.e("GoLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Cache.getAllTickets() != null && Cache.getAllChats() != null) {
                renderRequests();
            } else {
                Toast.makeText(RequestsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
