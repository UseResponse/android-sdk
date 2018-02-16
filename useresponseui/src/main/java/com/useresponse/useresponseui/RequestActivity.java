package com.useresponse.useresponseui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.useresponse.useresponseui.conversation.ConversationListAdapter;
import com.useresponse.useresponseui.conversation.ConversationListItem;
import com.useresponse.useresponseui.utils.Cache;
import com.useresponse.useresponseui.utils.UseResponse;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import useresponseapi.ChatMessagesQuery;
import useresponseapi.CommentForm;
import useresponseapi.ModelChat;
import useresponseapi.ModelChatIterator;
import useresponseapi.ModelChatMessage;
import useresponseapi.ModelChatMessageIterator;
import useresponseapi.ModelComment;
import useresponseapi.ModelCommentIterator;
import useresponseapi.ModelMessage;
import useresponseapi.ModelMessageIterator;
import useresponseapi.ModelTicket;
import useresponseapi.ModelTicketIterator;
import useresponseapi.Useresponseapi;

public class RequestActivity extends AppCompatActivity {
    private ListView conversationList;
    private ProgressBar conversationLoader;
    private boolean activityActive = false;
    private ModelChat activeChat;
    private ArrayList<ConversationListItem> activeChatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setTitle(getString(R.string.kb_header_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!UseResponse.init(this)) {
            return;
        }

        conversationList = (ListView)findViewById(R.id.conversationList);
        conversationLoader = (ProgressBar)findViewById(R.id.conversationLoader);

        Intent intent = getIntent();
        String requestType = intent.getStringExtra("requestType");
        int requestId = intent.getIntExtra("requestId", 0);
        boolean requestFound = false;

        TextView fullTicketSubject = findViewById(R.id.fullTicketSubject);
        TextView fullTicketStatus = findViewById(R.id.fullTicketStatus);
        TextView fullTicketDate = findViewById(R.id.fullTicketDate);
        View fullTicketDetailsEnd = findViewById(R.id.fullTicketDetailsEnd);

        switch (requestType) {
            case "ticket":
                ModelTicketIterator tickets = Cache.getAllTickets();
                ModelTicket ticket = null;

                fullTicketSubject.setVisibility(View.VISIBLE);
                fullTicketStatus.setVisibility(View.VISIBLE);
                fullTicketDate.setVisibility(View.VISIBLE);
                fullTicketDetailsEnd.setVisibility(View.VISIBLE);

                if (tickets != null) {
                    while (tickets.isValid()) {
                        ModelTicket activeTicket = tickets.getNext();

                        if (activeTicket.getId() == requestId) {
                            ticket = activeTicket;
                            break;
                        }
                    }
                }

                if (ticket == null) {
                    try {
                        UseResponse.initIdentity(this, false);
                        ticket = Useresponseapi.getTicket(requestId);
                    } catch (Exception e) {
                        Log.e("GoLog", e.getMessage());
                    }
                }

                if (ticket != null) {
                    requestFound = true;
                    fullTicketSubject.setText(ticket.getTitle());
                    fullTicketStatus.setText(ticket.getStatus().getTitle());
                    fullTicketStatus.setTextColor(Color.parseColor(ticket.getStatus().getColor().getText()));
                    fullTicketStatus.setBackgroundColor(Color.parseColor(ticket.getStatus().getColor().getBackground()));
                    fullTicketDate.setText(new SimpleDateFormat("MMM dd", Locale.US).format(new Date(ticket.getCreatedAt() * 1000L)));
                    (new LoadTicketCommentsTask(ticket)).execute();
                } else {
                    Log.e("GoLog", "Ticket is not found");
                }

                break;
            case "chat":
                fullTicketSubject.setVisibility(View.GONE);
                fullTicketStatus.setVisibility(View.GONE);
                fullTicketDate.setVisibility(View.GONE);
                fullTicketDetailsEnd.setVisibility(View.GONE);

                if (requestId > 0) {
                    ModelChatIterator chats = Cache.getAllChats();
                    activeChat = null;

                    if (chats != null) {
                        while (chats.isValid()) {
                            ModelChat chat = chats.getNext();

                            if (chat.getId() == requestId) {
                                activeChat = chat;
                                break;
                            }
                        }
                    }

                    if (activeChat == null) {
                        try {
                            UseResponse.initIdentity(this, false);
                            activeChat = Useresponseapi.getChat(requestId);
                        } catch (Exception e) {
                            Log.e("GoLog", e.getMessage());
                        }
                    }

                    if (activeChat != null) {
                        requestFound = true;
                        (new LoadChatMessagesTask(1)).execute();
                    } else {
                        Log.e("GoLog", "Chat is not found");
                    }
                } else {
                    conversationLoader.setVisibility(View.GONE);
                    conversationList.setVisibility(View.VISIBLE);
                    activeChat = new ModelChat();
                    activeChatMessages = new ArrayList<>();
                    requestFound = true;
                    initChatMessaging();
                }

                break;
        }

        if (!requestFound) {
            Toast.makeText(this, R.string.request_is_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationsService.setOnCommentListener(null);
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

    private class LoadTicketCommentsTask extends AsyncTask<Void, Void, Void> {
        private String error;
        private ModelTicket ticket;
        private ModelCommentIterator comments = null;

        LoadTicketCommentsTask(ModelTicket ticket) {
            super();
            this.ticket = ticket;
        }

        @Override
        protected void onPreExecute() {
            conversationList.setVisibility(View.GONE);
            conversationLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                comments = Useresponseapi.getComments(ticket.getId());
            } catch (Exception e) {
                error = e.getMessage();
                Log.e("GoLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (comments == null) {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            conversationLoader.setVisibility(View.GONE);
            conversationList.setVisibility(View.VISIBLE);

            final ArrayList<ConversationListItem> commentsArray = new ArrayList<>();

            ModelMessageIterator messages = ticket.getMessages();

            while (messages.isValid()) {
                ModelMessage message = messages.getNext();
                commentsArray.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
            }

            while (comments.isValid()) {
                ModelComment comment = comments.getNext();
                messages = comment.getMessages();
                boolean mine = ticket.getAuthor().getId() == comment.getAuthor().getId();

                while (messages.isValid()) {
                    ModelMessage message = messages.getNext();

                    if (mine) {
                        commentsArray.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
                    } else {
                        commentsArray.add(new ConversationListItem("incoming", message.getType(), message.getContent(), comment.getAuthor().getAvatar().getMedium()));
                    }
                }
            }

            conversationList.setAdapter(new ConversationListAdapter(RequestActivity.this, commentsArray));

            final ImageButton send = (ImageButton) findViewById(R.id.requestConversationSendButton);
            final EditText input = (EditText)findViewById(R.id.conversationSendInput);

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = input.getText().toString();

                    if (message.length() > 0) {
                        input.setText("");
                        commentsArray.add(new ConversationListItem("outgoing", "text", message, null));
                        BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                        adapter.notifyDataSetChanged();
                        conversationList.setSelection(adapter.getCount() - 1);
                        (new PostTicketCommentTask(ticket.getId(), message)).execute();
                    }
                }
            });

            NotificationsService.setOnCommentListener(new NotificationsService.OnCommentListener() {
                @Override
                public void received(int ticketId, String content, String photo) {
                    if ((int)ticket.getId() != ticketId) {
                        return;
                    }

                    ModelMessageIterator messages = Useresponseapi.bbCodeToMessages(content);

                    while (messages.isValid()) {
                        ModelMessage message = messages.getNext();
                        commentsArray.add(new ConversationListItem("incoming", message.getType(), message.getContent(), photo));
                    }

                    // otherwise it throws "Only the original thread that created a view hierarchy can touch its views."
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                            adapter.notifyDataSetChanged();
                            conversationList.setSelection(adapter.getCount() - 1);
                        }
                    });
                }

                @Override
                public boolean isValid(int ticketId) {
                    return activityActive && (int)ticket.getId() == ticketId;
                }
            });
        }
    }

    private class PostTicketCommentTask extends AsyncTask<Void, Void, Void> {
        long ticketId;
        String message;
        String error = null;

        PostTicketCommentTask(long ticketId, String message) {
            this.ticketId = ticketId;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CommentForm form = new CommentForm();
                form.setObjectId(this.ticketId);
                form.setContent(this.message);
                Useresponseapi.createComment(form);
            } catch (Exception e) {
                error = e.getMessage();
                Log.e("GoLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (error != null) {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadChatMessagesTask extends AsyncTask<Void, Void, Void> {
        private String error;
        private ModelChatMessageIterator messages = null;
        private int page = 1;

        LoadChatMessagesTask(int page) {
            super();
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            conversationList.setVisibility(View.GONE);
            conversationLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ChatMessagesQuery query = new ChatMessagesQuery();
                query.setChatId(activeChat.getId());
                query.setPage(page);
                messages = Useresponseapi.getChatMessages(query);
            } catch (Exception e) {
                error = e.getMessage();
                Log.e("GoLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (messages == null) {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            conversationLoader.setVisibility(View.GONE);
            conversationList.setVisibility(View.VISIBLE);

            activeChatMessages = new ArrayList<>();

            while (messages.isValid()) {
                ModelChatMessage message = messages.getNext();

                if (activeChat.getAuthor().getId() == message.getAuthor().getId()) {
                    activeChatMessages.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
                } else {
                    activeChatMessages.add(new ConversationListItem("incoming", message.getType(), message.getContent(), message.getAuthor().getAvatar().getMedium()));
                }
            }

            initChatMessaging();
        }
    }

    private void initChatMessaging() {
        final ImageButton send = (ImageButton) findViewById(R.id.requestConversationSendButton);
        final EditText input = (EditText)findViewById(R.id.conversationSendInput);

        conversationList.setAdapter(new ConversationListAdapter(RequestActivity.this, activeChatMessages));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = input.getText().toString();

                if (message.length() > 0) {
                    input.setText("");
                    activeChatMessages.add(new ConversationListItem("outgoing", "text", message, null));
                    BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                    adapter.notifyDataSetChanged();
                    conversationList.setSelection(adapter.getCount() - 1);

                    try {
                        JSONObject msgData = new JSONObject();
                        msgData.put("conversation", activeChat.getId());
                        msgData.put("type", "text");
                        msgData.put("content", message);
                        msgData.put("token", "mobile" + String.valueOf(System.currentTimeMillis()));

                        NotificationsService.sendChatMessage("mobile.message", msgData);
                    } catch (Exception e) {
                        Log.e("GoLog", e.getMessage());
                    }
                }
            }
        });

        NotificationsService.setOnChatMessageListener(new NotificationsService.OnChatMessageListener() {
            @Override
            public void received(int chatId, String type, String content, String fileName, String photo) {
                if ((int)activeChat.getId() != chatId) {
                    return;
                }

                activeChatMessages.add(new ConversationListItem("incoming", type, content, photo));

                // otherwise it throws "Only the original thread that created a view hierarchy can touch its views."
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                        adapter.notifyDataSetChanged();
                        conversationList.setSelection(adapter.getCount() - 1);
                    }
                });
            }

            @Override
            public boolean isValid(int chatId) {
                return activityActive && (int)activeChat.getId() == chatId;
            }

            @Override
            public void setChatId(int chatId) {
                if (activeChat.getId() == 0) {
                    activeChat.setId(chatId);
                }
            }
        });
    }
}
