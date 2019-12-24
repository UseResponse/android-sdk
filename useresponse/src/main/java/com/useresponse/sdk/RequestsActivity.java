package com.useresponse.sdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.useresponse.sdk.api.Api;
import com.useresponse.sdk.api.Chat;
import com.useresponse.sdk.api.Chats;
import com.useresponse.sdk.api.ChatsQuery;
import com.useresponse.sdk.api.Ticket;
import com.useresponse.sdk.api.Tickets;
import com.useresponse.sdk.api.TicketsQuery;
import com.useresponse.sdk.requests_list.RequestsListAdapter;
import com.useresponse.sdk.requests_list.RequestsListChat;
import com.useresponse.sdk.requests_list.RequestsListInterface;
import com.useresponse.sdk.requests_list.RequestsListTicket;
import com.useresponse.sdk.utils.Cache;
import com.useresponse.sdk.utils.UseResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RequestsActivity extends AppCompatActivity {

    public static boolean needRefresh = false;

    private ListView requestsList;
    private ProgressBar loader;
    private boolean loadingPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        setTitle(getString(R.string.requests_header_title));
        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
        }

        if (!UseResponse.init(this)) {
            return;
        }

        requestsList = (ListView)findViewById(R.id.requestsList);
        loader = (ProgressBar)findViewById(R.id.requestsLoader);

        if (Cache.getAllTickets() == null || Cache.getAllChats() == null) {
            requestsList.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            (new LoadTask(1)).execute();
        } else {
            renderRequests();
        }

        FloatingActionButton newRequest = (FloatingActionButton)findViewById(R.id.requestNew);
        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UseResponse.hasModule(RequestsActivity.this, "chat")) {
                    Intent intent = new Intent(RequestsActivity.this, RequestActivity.class);
                    intent.putExtra("requestType", "chat");
                    intent.putExtra("requestId", 0);
                    RequestsActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(RequestsActivity.this, CreateTicketActivity.class);
                    RequestsActivity.this.startActivity(intent);
                }
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

        FloatingActionsMenu newRequestMenu = (FloatingActionsMenu)findViewById(R.id.requestNewMenu);

        if (UseResponse.hasModule(RequestsActivity.this, "chat") && UseResponse.hasModule(RequestsActivity.this, "helpdesk")) {
            newRequest.setVisibility(View.GONE);
            newRequestMenu.setVisibility(View.VISIBLE);
        } else {
            newRequestMenu.setVisibility(View.GONE);
            newRequest.setVisibility(View.VISIBLE);
        }

        requestsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (
                    firstVisibleItem+visibleItemCount == totalItemCount &&
                    totalItemCount != 0 &&
                    !loadingPage &&
                    (Cache.getCurrentPage() < Cache.getChatPages() || Cache.getCurrentPage() < Cache.getTicketPages())
                ) {
                    Cache.setCurrentPage(Cache.getCurrentPage() + 1);
                    (new LoadTask(Cache.getCurrentPage())).execute();
                }
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
                (new LoadTask(1)).execute();
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
        UseResponse.processButtonBack(this);

        Tickets tickets = Cache.getAllTickets();
        Chats chats = Cache.getAllChats();
        final ArrayList<RequestsListInterface> requestsArray = new ArrayList<>();

        for (Ticket ticket : tickets.getTickets()) {
            requestsArray.add(new RequestsListTicket(ticket));
        }

        for (Chat chat : chats.getChats()) {
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
        private int page;

        public LoadTask(int page) {
            this.page = page;
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadingPage = true;

            try {
                UseResponse.initIdentity(RequestsActivity.this, false);

                if (page <= Cache.getTicketPages()) {
                    Tickets tickets = UseResponse.hasIdentity(RequestsActivity.this)
                            ? Api.getTickets(new TicketsQuery().setPage(page)) : new Tickets();

                    if (page == 1 || Cache.getAllTickets() == null) {
                        Cache.setAllTickets(tickets);
                    } else {
                        Cache.getAllTickets().getTickets().addAll(tickets.getTickets());
                    }

                    if (page == 1) {
                        Cache.setTicketPages(tickets.getTotalPages());
                    }
                }

                if (page <= Cache.getChatPages()) {
                    Chats chats = UseResponse.hasIdentity(RequestsActivity.this)
                            ? Api.getChats(new ChatsQuery().setPage(page)) : new Chats();

                    if (page == 1 || Cache.getAllChats() == null) {
                        Cache.setAllChats(chats);
                    } else {
                        Cache.getAllChats().getChats().addAll(chats.getChats());
                    }

                    if (page == 1) {
                        Cache.setChatPages(chats.getTotalPages());
                    }
                }
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            loadingPage = false;

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
