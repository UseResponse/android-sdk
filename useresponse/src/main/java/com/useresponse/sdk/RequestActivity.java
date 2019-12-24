package com.useresponse.sdk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.useresponse.sdk.api.Api;
import com.useresponse.sdk.api.ApiHelper;
import com.useresponse.sdk.api.Chat;
import com.useresponse.sdk.api.ChatMessage;
import com.useresponse.sdk.api.ChatMessages;
import com.useresponse.sdk.api.ChatMessagesQuery;
import com.useresponse.sdk.api.Chats;
import com.useresponse.sdk.api.ChatsQuery;
import com.useresponse.sdk.api.Comment;
import com.useresponse.sdk.api.CommentForm;
import com.useresponse.sdk.api.CommentsQuery;
import com.useresponse.sdk.api.FileForm;
import com.useresponse.sdk.api.Message;
import com.useresponse.sdk.api.Ticket;
import com.useresponse.sdk.api.Tickets;
import com.useresponse.sdk.api.TicketsQuery;
import com.useresponse.sdk.api.UploadedFile;
import com.useresponse.sdk.conversation.ConversationListAdapter;
import com.useresponse.sdk.conversation.ConversationListItem;
import com.useresponse.sdk.files.File;
import com.useresponse.sdk.files.Uploader;
import com.useresponse.sdk.utils.Cache;
import com.useresponse.sdk.utils.UseResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RequestActivity extends AppCompatActivity {
    private ListView conversationList;
    private ProgressBar conversationLoader;
    private boolean activityActive = false;
    private String requestType;
    private Chat activeChat;
    private Ticket activeTicket;
    private ArrayList<ConversationListItem> activeChatMessages;
    private EditText input;
    private ImageButton send;
    private ProgressDialog attachmentDialog;
    private ArrayList<ConversationListItem> commentsArray;

    private boolean loadingPage = false;
    private int messagePages = 1;
    private int currentPage = 1;
    private int topId = 0;
    private boolean chatIdRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setTitle(getString(R.string.kb_header_title));
        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
        }

        if (!UseResponse.init(this)) {
            return;
        }

        UseResponse.processButtonBack(this);

        commentsArray = new ArrayList<>();
        conversationList = (ListView)findViewById(R.id.conversationList);
        conversationLoader = (ProgressBar)findViewById(R.id.conversationLoader);

        Intent intent = getIntent();
        requestType = intent.getStringExtra("requestType");
        int requestId = intent.getIntExtra("requestId", 0);
        boolean requestFound = false;

        input = (EditText)findViewById(R.id.conversationSendInput);
        send = (ImageButton) findViewById(R.id.requestConversationSendButton);
        TextView fullTicketSubject = findViewById(R.id.fullTicketSubject);
        TextView fullTicketStatus = findViewById(R.id.fullTicketStatus);
        TextView fullTicketDate = findViewById(R.id.fullTicketDate);
        View fullTicketDetailsEnd = findViewById(R.id.fullTicketDetailsEnd);

        switch (requestType) {
            case "ticket":
                Tickets tickets = Cache.getAllTickets();

                fullTicketSubject.setVisibility(View.VISIBLE);
                fullTicketStatus.setVisibility(View.VISIBLE);
                fullTicketDate.setVisibility(View.VISIBLE);
                fullTicketDetailsEnd.setVisibility(View.VISIBLE);

                if (tickets != null) {
                    for (Ticket ticket : tickets.getTickets()) {
                        if (ticket.getId() == requestId) {
                            activeTicket = ticket;
                            break;
                        }
                    }
                }

                if (activeTicket == null) {
                    try {
                        UseResponse.initIdentity(this, false);
                        activeTicket = Api.getTicket(requestId);
                    } catch (Exception e) {
                        Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                        e.printStackTrace();
                    }
                }

                if (activeTicket != null) {
                    requestFound = true;
                    fullTicketSubject.setText(activeTicket.getTitle());
                    fullTicketStatus.setText(activeTicket.getStatus().getTitle());
                    fullTicketStatus.setTextColor(Color.parseColor(activeTicket.getStatus().getTextColor()));
                    fullTicketStatus.setBackgroundColor(Color.parseColor(activeTicket.getStatus().getBgColor()));
                    fullTicketDate.setText(new SimpleDateFormat("MMM dd", Locale.US).format(new Date(activeTicket.getCreatedAt() * 1000L)));
                    (new LoadTicketCommentsTask()).execute();
                } else {
                    Log.e("UrLog", "Ticket is not found");
                }

                break;
            case "chat":
                fullTicketSubject.setVisibility(View.GONE);
                fullTicketStatus.setVisibility(View.GONE);
                fullTicketDate.setVisibility(View.GONE);
                fullTicketDetailsEnd.setVisibility(View.GONE);

                if (requestId > 0) {
                    Chats chats = Cache.getAllChats();
                    activeChat = null;

                    if (chats != null) {
                        for (Chat chat : chats.getChats()) {
                            if (chat.getId() == requestId) {
                                activeChat = chat;
                                break;
                            }
                        }
                    }

                    if (activeChat == null) {
                        try {
                            UseResponse.initIdentity(this, false);
                            activeChat = Api.getChat(requestId);
                        } catch (Exception e) {
                            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                        }
                    }

                    if (activeChat != null) {
                        requestFound = true;
                        (new LoadChatMessagesTask(1)).execute();
                    } else {
                        Log.e("UrLog", "Chat is not found");
                        requestId = 0;
                    }
                }

                if (requestId == 0) {
                    conversationLoader.setVisibility(View.GONE);
                    conversationList.setVisibility(View.VISIBLE);
                    activeChat = new Chat();
                    activeChatMessages = new ArrayList<>();
                    requestFound = true;
                    initChatMessaging();
                }

                conversationList.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (firstVisibleItem == 0 && !loadingPage && currentPage < messagePages) {
                            (new LoadChatMessagesTask(++currentPage)).execute();
                        }
                    }
                });

                break;
        }

        if (!requestFound) {
            Toast.makeText(this, R.string.request_is_not_found, Toast.LENGTH_LONG).show();
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                send.setImageResource(s.length() > 0 ? R.drawable.ic_action_send : R.drawable.ic_action_attach_file);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RequestActivity.RESULT_OK || requestCode != Uploader.PICK_FILE_REQUEST || data == null) {
            return;
        }

        Uri selectedFileUri = data.getData();
        Log.d("UrLog", "File picked: " + selectedFileUri.toString());
        attachmentDialog = ProgressDialog.show(RequestActivity.this, "", "Uploading File...", true);
        File file = new File(RequestActivity.this, selectedFileUri);
        FileForm fileForm = new FileForm(file.getName(), file.getContent());

        switch (requestType) {
            case "ticket":
                PostTicketCommentTask postComment = new PostTicketCommentTask("");
                postComment.setFileForm(fileForm);
                postComment.execute();
                break;
            case "chat":
                String type = file.isImage() ? "image" : "document";
                PostChatMessageTask postMessage = new PostChatMessageTask(type, "");
                postMessage.setFileForm(fileForm);
                postMessage.execute();
                break;
            default:
                attachmentDialog.dismiss();
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
        private ArrayList<Comment> comments = null;

        @Override
        protected void onPreExecute() {
            conversationList.setVisibility(View.GONE);
            conversationLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                comments = Api.getComments(new CommentsQuery(activeTicket.getId()));
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
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

            ArrayList<Message> messages = activeTicket.getMessages();

            for (Message message : messages) {
                commentsArray.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
            }

            for (Comment comment : comments) {
                ArrayList<Message> commentMessages = comment.getMessages();
                boolean mine = activeTicket.getAuthor().getId() == comment.getAuthor().getId();

                for (Message message : commentMessages) {
                    if (mine) {
                        commentsArray.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
                    } else {
                        commentsArray.add(new ConversationListItem("incoming", message.getType(), message.getContent(), comment.getAuthor().getAvatar().getMedium()));
                    }
                }
            }

            conversationList.setAdapter(new ConversationListAdapter(RequestActivity.this, commentsArray));

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textMessage = input.getText().toString();

                    if (textMessage.length() > 0) {
                        input.setText("");
                        commentsArray.add(new ConversationListItem("outgoing", "text", textMessage, null));
                        BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                        adapter.notifyDataSetChanged();
                        conversationList.setSelection(adapter.getCount() - 1);
                        (new PostTicketCommentTask(textMessage)).execute();
                    } else {
                        Uploader.pickFile(RequestActivity.this);
                    }
                }
            });

            NotificationsService.setOnCommentListener(new NotificationsService.OnCommentListener() {
                @Override
                public void received(int ticketId, String content, String photo, JSONArray attachments) {
                    if (activeTicket.getId() != ticketId) {
                        return;
                    }

                    ArrayList<Message> messages = ApiHelper.bbCodeToMessages(content);

                    for (Message message : messages) {
                        commentsArray.add(new ConversationListItem("incoming", message.getType(), message.getContent(), photo));
                    }

                    for (int i = 0; i < attachments.length(); i++) {
                        try {
                            JSONObject attachment = attachments.getJSONObject(i);
                            com.useresponse.sdk.api.File file = new com.useresponse.sdk.api.File();
                            file.setExt(attachment.getString("ext"));
                            String type = file.isImage() ? "image" : "document";
                            commentsArray.add(new ConversationListItem("incoming", type, attachment.getString("url"), photo));
                        } catch (Exception e) {
                            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                        }
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

                    (new UpdateRequestsListTask()).execute();
                }

                @Override
                public boolean isValid(int ticketId) {
                    return activityActive && activeTicket.getId() == ticketId;
                }
            });
        }
    }

    private class PostTicketCommentTask extends AsyncTask<Void, Void, Void> {
        private String message;
        private String error = null;
        private FileForm fileForm = null;

        PostTicketCommentTask(String message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CommentForm form = new CommentForm(activeTicket.getId());
                form.setContent(this.message);

                if (fileForm != null) {
                    UploadedFile uploadedFile = Api.uploadFile(fileForm);
                    form.attachFile(uploadedFile.getToken(), fileForm.getName());

                    Comment comment = Api.createComment(form);
                    ArrayList<Message> messages = comment.getMessages();

                    for (Message message : messages) {
                        commentsArray.add(new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
                    }
                } else {
                    Api.createComment(form);
                }
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (fileForm != null && attachmentDialog != null) {
                attachmentDialog.dismiss();
            }

            if (error != null) {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            if (fileForm != null) {
                BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                adapter.notifyDataSetChanged();
                conversationList.setSelection(adapter.getCount() - 1);
            }

            (new UpdateRequestsListTask()).execute();
        }

        public void setFileForm(FileForm fileForm) {
            this.fileForm = fileForm;
        }
    }

    private class LoadChatMessagesTask extends AsyncTask<Void, Void, Void> {
        private String error;
        private ChatMessages messages = null;
        private int page = 1;

        LoadChatMessagesTask(int page) {
            super();
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            if (page == 1) {
                conversationList.setVisibility(View.GONE);
                conversationLoader.setVisibility(View.VISIBLE);
            } else {
                conversationLoader.setVisibility(View.GONE);
                conversationList.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadingPage = true;

            try {
                ChatMessagesQuery query = new ChatMessagesQuery(activeChat.getId()).setPage(page);

                if (topId > 0) {
                    query.setTopId(topId);
                }

                messages = Api.getChatMessages(query);
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            loadingPage = false;

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

            if (page == 1) {
                activeChatMessages = new ArrayList<>();

                for (int i = messages.getMessages().size() - 1; i >= 0; i--) {
                    try {
                        ChatMessage message = messages.getMessages().get(i);

                        if (topId < message.getId()) {
                            topId = message.getId();
                        }

                        String messageType    = message.getType();
                        String messageContent = message.getContent();

                        if (messageType.equals("join_chat")) {
                            continue;
                        }

                        if (activeChat.getAuthor().getId() == message.getAuthor().getId()) {
                            activeChatMessages.add(new ConversationListItem("outgoing", messageType, messageContent, null));
                        } else {
                            for (Message messagePart : message.getMessages()) {
                                ConversationListItem newChatItem = new ConversationListItem(
                                        "incoming",
                                        messagePart.getType(),
                                        messagePart.getContent(),
                                        message.getAuthor().getAvatar().getMedium()
                                );

                                if (message.getFileName() != null && message.getFileName().length() > 0) {
                                    newChatItem.setFileName(message.getFileName());
                                }

                                activeChatMessages.add(newChatItem);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                    }
                }

                initChatMessaging();
                messagePages = messages.getTotalPages();
            } else {
                // depends on order
                for (ChatMessage message : messages.getMessages()) {
                    try {
                        if (activeChat.getAuthor().getId() == message.getAuthor().getId()) {
                            activeChatMessages.add(0, new ConversationListItem("outgoing", message.getType(), message.getContent(), null));
                        } else {
                            for (Message messagePart : message.getMessages()) {
                                ConversationListItem newChatItem = new ConversationListItem(
                                        "incoming",
                                        messagePart.getType(),
                                        messagePart.getContent(),
                                        message.getAuthor().getAvatar().getMedium()
                                );

                                if (message.getFileName() != null && message.getFileName().length() > 0) {
                                    newChatItem.setFileName(message.getFileName());
                                }

                                activeChatMessages.add(0, newChatItem);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                    }
                }

                ((BaseAdapter)conversationList.getAdapter()).notifyDataSetChanged();
                // todo: stay at current list position. the next command should help, but doesn't :\
                conversationList.setSelection(messages.getMessages().size() - 1);
            }
        }
    }

    private class PostChatMessageTask extends AsyncTask<Void, Void, Void> {
        private String error;
        private String type;
        private String message;
        private FileForm fileForm;
        private UploadedFile uploadedFile;

        PostChatMessageTask(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public void setFileForm(FileForm fileForm) {
            this.fileForm = fileForm;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                UseResponse.initIdentity(RequestActivity.this, true);
                JSONObject msgData = new JSONObject();
                msgData.put("conversation", activeChat.getId());
                msgData.put("type", type);

                if (fileForm == null) {
                    msgData.put("content", message);
                } else {
                    uploadedFile = Api.uploadFile(fileForm);
                    msgData.put("content", uploadedFile.getUrl());
                    msgData.put("fileName", fileForm.getName());
                }

                msgData.put("token", "mobile" + String.valueOf(System.currentTimeMillis()));
                NotificationsService.sendChatMessage(RequestActivity.this, "mobile.message", msgData);
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (error == null) {
                if (fileForm != null) {
                    attachmentDialog.dismiss();
                    activeChatMessages.add(new ConversationListItem("outgoing", this.type, uploadedFile.getUrl(), null));
                    Cache.touchChat(activeChat.getId());
                    RequestsActivity.needRefresh = true;

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

                (new UpdateRequestsListTask()).execute();
            } else {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UpdateRequestsListTask extends AsyncTask<Void, Void, Void> {
        private String error;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Tickets tickets = UseResponse.hasIdentity(RequestActivity.this)
                        ? Api.getTickets(new TicketsQuery().setPage(1)) : new Tickets();

                Cache.setAllTickets(tickets);
                Cache.setTicketPages(tickets.getTotalPages());

                Chats chats = UseResponse.hasIdentity(RequestActivity.this)
                        ? Api.getChats(new ChatsQuery().setPage(1)) : new Chats();

                Cache.setAllChats(chats);
                Cache.setChatPages(chats.getTotalPages());
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (error != null) {
                Toast.makeText(RequestActivity.this, error, Toast.LENGTH_LONG).show();
                return;
            }

            RequestsActivity.needRefresh = true;
        }
    }

    private void initChatMessaging() {
        conversationList.setAdapter(new ConversationListAdapter(RequestActivity.this, activeChatMessages));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeChat.getId() == 0 && chatIdRequested) {
                    return;
                }

                String message = input.getText().toString();

                if (message.length() > 0) {
                    input.setText("");
                    activeChatMessages.add(new ConversationListItem("outgoing", "text", message, null));
                    BaseAdapter adapter = (BaseAdapter)conversationList.getAdapter();
                    adapter.notifyDataSetChanged();
                    conversationList.setSelection(adapter.getCount() - 1);

                    if (activeChat.getId() == 0) {
                        chatIdRequested = true;
                    }

                    Cache.touchChat(activeChat.getId());
                    RequestsActivity.needRefresh = true;

                    new PostChatMessageTask("text", message).execute();
                } else {
                    Uploader.pickFile(RequestActivity.this);
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    try {
                        JSONObject msgData = new JSONObject();
                        msgData.put("conversation", activeChat.getId());
                        NotificationsService.sendChatMessage(RequestActivity.this, "mobile.typing", msgData);
                    } catch (Exception e) {
                        Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        NotificationsService.setOnChatMessageListener(new NotificationsService.OnChatMessageListener() {
            @Override
            public void received(int chatId, String type, String content, String fileName, String photo) {
                if (activeChat.getId() != chatId) {
                    return;
                }

                if (type.equals("text")) {
                    ArrayList<Message> messages = ApiHelper.bbCodeToMessages(content);

                    for (Message message : messages) {
                        activeChatMessages.add(new ConversationListItem("incoming", message.getType(), message.getContent(), photo));
                    }
                } else {
                    ConversationListItem newItem = new ConversationListItem("incoming", type, content, photo);

                    if (fileName != null && fileName.length() > 0) {
                        newItem.setFileName(fileName);
                    }

                    activeChatMessages.add(newItem);
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

                (new UpdateRequestsListTask()).execute();
            }

            @Override
            public boolean isValid(int chatId) {
                return activityActive && activeChat.getId() == chatId;
            }

            @Override
            public void setChatId(int chatId) {
                chatIdRequested = false;

                if (activeChat.getId() == 0) {
                    activeChat.setId(chatId);
                    UseResponse.saveSingleChat(RequestActivity.this, chatId);
                    (new UpdateRequestsListTask()).execute();
                }
            }
        });
    }
}
