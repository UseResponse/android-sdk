package com.useresponse.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.useresponse.sdk.utils.UseResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class NotificationsService extends Service {

    private static final int PUSH_INTERFACE_MOBILE = 3;
    private static OnCommentListener onComment = null;
    private static OnChatMessageListener onChatMessage = null;
    private NotificationManager manager;
    private int notificationId = 0;
    private static WebSocket commonWs;
    private static WebSocket chatWs;
    private static int chatWsRequestId = 0;
    private static int chatWsRegisterId;
    private static int newConversationId;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (commonWs == null) {
            runCommonWs();
        }

        if (chatWs == null) {
            runChatWs();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendChatMessage(Context context, String action, JSONObject data) {
        if (chatWs != null) {
            try {
                JSONObject message = new JSONObject();
                message.put("id", ++chatWsRequestId);
                message.put("action", action);
                message.put("community", UseResponse.getBaseUrl(context));
                message.put("token", UseResponse.getApiKey());

                if (data != null) {
                    message.put("data", data);

                    if (action.equals("mobile.message") && data.getInt("conversation") == 0) {
                        newConversationId = chatWsRequestId;
                    }
                }

                chatWs.sendText(message.toString());
            } catch (JSONException e) {
                Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        }
    }

    public static void setOnCommentListener(OnCommentListener listener) {
        NotificationsService.onComment = listener;
    }

    public static void setOnChatMessageListener(OnChatMessageListener listener) {
        NotificationsService.onChatMessage = listener;
    }

    public interface OnCommentListener {
        public void received(int ticketId, String content, String photo, JSONArray attachments);
        public boolean isValid(int ticketId);
    }

    public interface OnChatMessageListener {
        public void received(int chatId, String type, String content, String fileName, String photo);
        public boolean isValid(int chatId);
        public void setChatId(int chatId);
    }

    public interface OnChatUploadedFileListener {
        public void received(int chatId, String type, String content, String fileName, String photo);
    }

    private void runCommonWs() {
        try {
            commonWs = new WebSocketFactory().createSocket(UseResponse.getPushWsUrl(NotificationsService.this));

            commonWs.setPingInterval(60 * 1000);

            commonWs.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    if (!text.equals("{\"action\":\"ping\"}")) {
                        Log.d("UrLog", text);
                    }

                    JSONObject message = new JSONObject(text);

                    if (message.getString("action").equals("new_comment_mobile_app")) {
                        JSONObject comment = message.getJSONObject("options");
                        JSONObject object = comment.getJSONObject("object");

                        if (NotificationsService.onComment != null) {
                            Log.d("UrLog", "New Notification. Listener is set.");

                            NotificationsService.onComment.received(
                                    object.getInt("id"),
                                    comment.getString("content"),
                                    comment.getJSONObject("author").getString("photo"),
                                    comment.getJSONArray("attachments")
                            );
                        }

                        if (NotificationsService.onComment == null || !NotificationsService.onComment.isValid(object.getInt("id"))) {
                            Log.d("UrLog", "New Notification. Pushing....");

                            Intent intent = new Intent(NotificationsService.this, RequestActivity.class);
                            intent.putExtra("requestType", "ticket");
                            intent.putExtra("requestId", object.getInt("id"));
                            PendingIntent pIntent = PendingIntent.getActivity(NotificationsService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Notification.Builder builder = new Notification.Builder(NotificationsService.this);
                            builder.setAutoCancel(false);
                            builder.setTicker("New Reply");
                            builder.setContentTitle("New Reply");
                            builder.setContentText(object.getString("title"));
                            builder.setContentIntent(pIntent);
                            builder.setOngoing(false);
                            builder.setAutoCancel(true);
                            builder.setSmallIcon(R.drawable.ic_type_chat);

                            if (android.os.Build.VERSION.SDK_INT >= 26) {
                                String channelId = "my_channel_01";
                                CharSequence channelName = "Comments";
                                String description = "This channel receives new comment notifications";
                                int channelImportance = NotificationManager.IMPORTANCE_LOW;
                                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, channelImportance);
                                mChannel.setDescription(description);
                                mChannel.enableVibration(true);
                                manager.createNotificationChannel(mChannel);
                                builder.setChannelId(channelId);
                            }

                            Notification notification = builder.build();
                            manager.notify(++notificationId, notification);
                        }
                    }
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    try {
                        JSONObject register = new JSONObject();
                        register.put("action", "register");
                        register.put("token", UseResponse.getApiKey());
                        register.put("community", UseResponse.getBaseUrl(NotificationsService.this));
                        register.put("interface", PUSH_INTERFACE_MOBILE);
                        websocket.sendText(register.toString());
                    } catch (JSONException e) {
                        Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                    }
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    commonWs = null;
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    commonWs = null;
                }
            });

            commonWs.connectAsynchronously();
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }

    private void runChatWs() {
        try {
            chatWs = new WebSocketFactory().createSocket(UseResponse.getChatWsUrl(NotificationsService.this));

            chatWs.setPingInterval(60 * 1000);

            chatWs.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    if (!text.equals("{\"action\":\"ping\"}")) {
                        Log.d("UrLog", text);
                    }

                    JSONObject message = new JSONObject(text);
                    processIncomingMessage(message);
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    NotificationsService.sendChatMessage(NotificationsService.this, "mobile.register", null);
                    chatWsRegisterId = chatWsRequestId;
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    chatWs = null;
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    chatWs = null;
                }
            });

            chatWs.connectAsynchronously();
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }

    private void processIncomingMessage(JSONObject message) {
        try {
            if (message.has("id") && message.has("success") && message.has("updates")) {
                if (message.getBoolean("success") && !message.isNull("updates") && message.getInt("id") == chatWsRegisterId) {
                    JSONArray updates = message.getJSONArray("updates");

                    for (int i = 0; i < updates.length(); i++) {
                        processIncomingMessage(updates.getJSONObject(i));
                    }
                }

                if (message.has("success") && message.has("message") && !message.getBoolean("success")) {
                    Log.e("UrLog", message.getString("message"));
                }

                return;
            }

            if (
                    NotificationsService.onChatMessage != null &&
                    message.has("id") &&
                    message.has("success") &&
                    message.has("conversation") &&
                    message.getBoolean("success") &&
                    newConversationId > 0
            ) {
                chatWsRequestId = 0;
                NotificationsService.onChatMessage.setChatId(message.getInt("conversation"));
                return;
            }

            switch (message.getString("action")) {
                case "update.message":
                    JSONObject msgData = message.getJSONObject("data");
                    int chatId = msgData.getInt("conversation");
                    String chatType = msgData.getString("type");
                    String chatContent = msgData.getString("content");

                    if (chatType.equals("article")) {
                        chatType = "text";
                        try {
                            JSONObject article = new JSONObject(chatContent);
                            chatContent = article.getString("title") + "\n" + article.getString("link");
                        } catch (JSONException e) {
                            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
                        }
                    }

                    if (NotificationsService.onChatMessage != null) {
                        Log.d("UrLog", "New Chat Message. Listener is set.");

                        NotificationsService.onChatMessage.received(
                                chatId,
                                chatType,
                                chatContent,
                                msgData.getString("fileName"),
                                msgData.getString("userPhoto")
                        );
                    }

                    if (NotificationsService.onChatMessage == null || !NotificationsService.onChatMessage.isValid(chatId)) {
                        Log.d("UrLog", "New Chat Message. Pushing....");

                        Intent intent = new Intent(NotificationsService.this, RequestActivity.class);
                        intent.putExtra("requestType", "chat");
                        intent.putExtra("requestId", chatId);
                        PendingIntent pIntent = PendingIntent.getActivity(NotificationsService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification.Builder builder = new Notification.Builder(NotificationsService.this);
                        builder.setAutoCancel(false);
                        builder.setTicker("New Chat Message");
                        builder.setContentTitle("New Chat Message");
                        builder.setContentText(chatType.equals("text") ? chatContent : chatType);
                        builder.setContentIntent(pIntent);
                        builder.setOngoing(false);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_type_chat);

                        if (android.os.Build.VERSION.SDK_INT >= 26) {
                            String channelId = "my_channel_01";
                            CharSequence channelName = "Chats";
                            String description = "This channel receives new chat messages";
                            int channelImportance = NotificationManager.IMPORTANCE_LOW;
                            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, channelImportance);
                            mChannel.setDescription(description);
                            mChannel.enableVibration(true);
                            manager.createNotificationChannel(mChannel);
                            builder.setChannelId(channelId);
                        }

                        Notification notification = builder.build();
                        manager.notify(++notificationId, notification);
                    }

                    break;
            }
        } catch (Exception e) {
            Log.e("UrLog", e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }
}
