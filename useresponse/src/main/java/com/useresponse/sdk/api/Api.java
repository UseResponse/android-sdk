package com.useresponse.sdk.api;

import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Api {
    private static final int REQUEST_TIMEOUT = 15000;
    private static boolean sslConfigured = false;
    private static String apiUrl;
    private static String apiKey;
    private static IdentityData identityData = null;

    public static void setApiUrl(String apiUrl) {
        Api.apiUrl = apiUrl;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static void setApiKey(String apiKey) {
        Api.apiKey = apiKey;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static ArrayList<Article> getArticles(ArticlesQuery query) throws Exception {
        ArrayList<Article> result = new ArrayList<>();
        String url = Api.getApiUrl() + "/search.json?ownership=knowledge-base";

        if (query.getType().length() > 0) {
            url += "&type=" + URLEncoder.encode(query.getType(), "UTF-8");
        }

        if (query.getCategory() >= 0) {
            url += "&category_id" + String.valueOf(query.getCategory());
        }

        if (query.getSearch().length() > 0) {
            url += "&text=" + URLEncoder.encode(query.getSearch(), "UTF-8");
        }

        if (query.isFeatured()) {
            url += "&featured=1";
        }

        if (query.getForum() > 0) {
            url += "&forum_id=" + String.valueOf(query.getForum());
        }

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("knowledgeBase")) {
                JSONArray responseArticles = responseObjects.getJSONArray("knowledgeBase");

                for (int i = 0; i < responseArticles.length(); i++) {
                    result.add(new Article(responseArticles.getJSONObject(i)));
                }
            }
        }

        return result;
    }

    public static Article getArticle(int articleId) throws Exception {
        String url = Api.getApiUrl() + "/objects/" + String.valueOf(articleId) + ".json?apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            Article article = new Article(new JSONObject(responseJson));

            if (article.getId() > 0) {
                return article;
            }
        }

        return null;
    }

    public static Tickets getTickets(TicketsQuery query) throws Exception {
        String url = Api.getApiUrl() + "/tickets.json?bbcode=1&apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8")
                + "&page=" + String.valueOf(query.getPage());

        if (query.getSearch().length() > 0) {
            url += "&text=" + URLEncoder.encode(query.getSearch(), "UTF-8");
        }

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                return new Tickets(responseObjects.getJSONObject("success"));
            }
        }

        return new Tickets();
    }

    public static Ticket getTicket(int ticketId) throws Exception {
        String url = Api.getApiUrl() + "/objects/" + String.valueOf(ticketId) + ".json?bbcode=1&apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            Ticket ticket = new Ticket(new JSONObject(responseJson));

            if (ticket.getId() > 0) {
                return ticket;
            }
        }

        return null;
    }

    public static Ticket createTicket(TicketForm form) throws Exception {
        String url = Api.getApiUrl() + "/objects.json?mobile=1&apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = post(url, form.toJson());

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                Ticket ticket = new Ticket(responseObjects.getJSONObject("success"));

                if (ticket.getId() > 0) {
                    return ticket;
                }
            }
        }

        throw new Exception("Can not create ticket");
    }

    public static ArrayList<Comment> getComments(CommentsQuery query) throws Exception {
        ArrayList<Comment> result = new ArrayList<>();

        String url = Api.getApiUrl() + "/objects/" + String.valueOf(query.getObjectId())
                + "/comments.json?bbcode=1&apiKey=" + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                JSONArray responseComments = responseObjects.getJSONArray("success");

                for (int i = 0; i < responseComments.length(); i++) {
                    result.add(new Comment(responseComments.getJSONObject(i)));
                }
            }
        }

        return result;
    }

    public static Comment createComment(CommentForm form) throws Exception {
        String url = Api.getApiUrl() + "/comments.json?mobile=1&apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = post(url, form.toJson());

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                Comment comment = new Comment(responseObjects.getJSONObject("success"));

                if (comment.getId() > 0) {
                    return comment;
                }
            }
        }

        throw new Exception("Can not create comment");
    }

    public static UploadedFile uploadFile(FileForm form) throws Exception {
        String url = Api.getApiUrl() + "/upload-file.json?apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = post(url, form.toJson());

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                return new UploadedFile(responseObjects.getJSONObject("success"));
            }
        }

        throw new Exception("Can not create comment");
    }

    public static ArrayList<CustomField> getCustomFields(String fieldType) throws Exception {
        ArrayList<CustomField> result = new ArrayList<>();
        String url = Api.getApiUrl() + "/custom-fields/" + URLEncoder.encode(fieldType, "UTF-8") + ".json";
        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                JSONArray responseComments = responseObjects.getJSONArray("success");

                for (int i = 0; i < responseComments.length(); i++) {
                    result.add(new CustomField(responseComments.getJSONObject(i)));
                }
            }
        }

        return result;
    }

    public static Chats getChats(ChatsQuery query) throws Exception {
        String url = Api.getApiUrl() + "/chats.json?apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8")
                + "&page=" + String.valueOf(query.getPage());

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                return new Chats(responseObjects.getJSONObject("success"));
            }
        }

        return new Chats();
    }

    public static Chat getChat(int chatId) throws Exception {
        String url = Api.getApiUrl() + "/chats/" + String.valueOf(chatId) + ".json?apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8");

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                return new Chat(responseObjects.getJSONObject("success"));
            }
        }

        return null;
    }

    public static ChatMessages getChatMessages(ChatMessagesQuery query) throws Exception {
        String url = Api.getApiUrl() + "/chats/" + String.valueOf(query.getChatId()) + "/messages.json?apiKey="
                + URLEncoder.encode(Api.getApiKey(), "UTF-8")
                + "&page=" + String.valueOf(query.getPage());

        if (query.getTopId() > 0) {
            url += "&top_id=" + String.valueOf(query.getTopId());
        }

        String responseJson = get(url);

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                return new ChatMessages(responseObjects.getJSONObject("success"));
            }
        }

        return new ChatMessages();
    }

    public static void setIdentityData(IdentityData identityData) {
        Api.identityData = identityData;
    }

    public static IdentityData getIdentityData() {
        return identityData;
    }

    public static User getIdentity(boolean createIfNotExists) throws Exception {
        if (identityData != null) {
            String url = Api.getApiUrl() + "/sso/identity.json?token="
                    + URLEncoder.encode(identityData.getToken(), "UTF-8")
                    + "&email=" + URLEncoder.encode(identityData.getEmail(), "UTF-8");

            if (identityData.getId().length() > 0) {
                url += "&sso_id=" + URLEncoder.encode(identityData.getId(), "UTF-8");
            }

            String responseJson = get(url);

            if (responseJson.length() > 0) {
                JSONObject responseObjects = new JSONObject(responseJson);

                if (!responseObjects.isNull("success")) {
                    User user = new User(responseObjects.getJSONObject("success"));

                    if (user.getId() > 0) {
                        return user;
                    }
                }
            }
        }

        return createIfNotExists ? createUser() : null;
    }

    public static User createUser() throws Exception {
        String url = Api.getApiUrl() + "/register.json";
        JSONObject postData = new JSONObject();

        if (identityData != null) {
            if (identityData.getFullName().length() > 0) {
                postData.put("name", identityData.getFullName());
            }

            if (identityData.getEmail().length() > 0) {
                postData.put("email", identityData.getEmail());
            }

            if (identityData.getId().length() > 0) {
                postData.put("sso_id", identityData.getId());
            }

            if (identityData.getAvatarType().length() > 0 && identityData.getAvatarContent().length() > 0) {
                postData.put("avatar_type", identityData.getAvatarType());
                postData.put("avatar_content", identityData.getAvatarContent());
            }

            SparseArray<String> properties = identityData.getProperties();
            JSONObject postProperties = new JSONObject();

            for (int i = 0; i < properties.size(); i++) {
                int propId = properties.keyAt(i);
                postProperties.put("property_" + String.valueOf(propId), properties.get(propId));
            }

            if (properties.size() > 0) {
                postData.put("properties", postProperties);
            }
        }

        String responseJson = post(url, postData.toString());

        if (responseJson.length() > 0) {
            JSONObject responseObjects = new JSONObject(responseJson);

            if (!responseObjects.isNull("success")) {
                User user = new User(responseObjects.getJSONObject("success"));

                if (user.getId() > 0) {
                    return user;
                }
            }
        }

        throw new Exception("Can not create user");
    }

    public static void updateIdentity(String apiKey) throws Exception {
        if (identityData == null) {
            return;
        }

        if (apiKey == null) {
            apiKey = Api.getApiKey();
        }

        String url = Api.getApiUrl() + "/profile/update.json?apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        JSONObject postData = new JSONObject();

        if (identityData.getFullName().length() > 0) {
            postData.put("name", identityData.getFullName());
        }

        if (identityData.getEmail().length() > 0) {
            postData.put("email", identityData.getEmail());
        }

        if (identityData.getAvatarType().length() > 0 && identityData.getAvatarContent().length() > 0) {
            postData.put("avatar_type", identityData.getAvatarType());
            postData.put("avatar_content", identityData.getAvatarContent());
        }

        SparseArray<String> properties = identityData.getProperties();
        JSONObject postProperties = new JSONObject();

        for (int i = 0; i < properties.size(); i++) {
            int propId = properties.keyAt(i);
            postProperties.put("property_" + String.valueOf(propId), properties.get(propId));
        }

        if (properties.size() > 0) {
            postData.put("properties", postProperties);
        }

        post(url, postData.toString());
    }

    public static void subscribeDevice(String deviceToken) throws Exception {
        get(
                Api.getApiUrl() + "/push/subscribe-device.json?apiKey="
                        + URLEncoder.encode(apiKey, "UTF-8")
                        + "&platform=android&device_token="
                        + URLEncoder.encode(deviceToken, "UTF-8")
        );
    }

    public static TwilioVoice getTwilioVoice(TwilioVoiceQuery query) throws Exception {
        String url = Api.getApiUrl() + "/twilio/voice/token-app.json";

        if (query.getApiKey() != null) {
            url += "?apiKey=" + URLEncoder.encode(query.getApiKey(), "UTF-8");
        }

        String responseJson = get(url);

        return responseJson.length() > 0 ? new TwilioVoice(new JSONObject(responseJson)) : null;
    }

    public static TwilioVideo getTwilioVideo(TwilioVideoQuery query) throws Exception {
        String url = Api.getApiUrl() + "/twilio/video/token-app.json";

        if (query.getApiKey() != null) {
            url += "?apiKey=" + URLEncoder.encode(query.getApiKey(), "UTF-8");
        }

        String responseJson = get(url);

        return responseJson.length() > 0 ? new TwilioVideo(new JSONObject(responseJson)) : null;
    }

    private static String get(String url) throws Exception {
        Log.d("UrLog", "API GET request: " + url);
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setReadTimeout(REQUEST_TIMEOUT);
        conn.setConnectTimeout(REQUEST_TIMEOUT);

        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).setRequestMethod("GET");
            ((HttpURLConnection) conn).connect();
        }

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setRequestMethod("GET");
            configureSsl();
            ((HttpsURLConnection) conn).connect();
        }

        int responseCode = 0;
        String responseMessage = "Can not perform request";

        if (conn instanceof HttpURLConnection) {
            responseCode = ((HttpURLConnection) conn).getResponseCode();
            responseMessage = ((HttpURLConnection) conn).getResponseMessage();
        }

        if (conn instanceof HttpsURLConnection) {
            responseCode = ((HttpsURLConnection) conn).getResponseCode();
            responseMessage = ((HttpsURLConnection) conn).getResponseMessage();
        }

        if (responseCode == 404) {
            return "";
        }

        if (responseCode == 0 || responseCode >= 300) {
            throw new Exception(responseMessage);
        }

        InputStreamReader input = new InputStreamReader(conn.getInputStream());

        BufferedReader reader = new BufferedReader(input);
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;

        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }

        reader.close();
        input.close();

        return stringBuilder.toString();
    }

    private static String post(String url, String data) throws Exception {
        Log.d("UrLog", "API POST request: " + url);
        Log.d("UrLog", "API POST data: " + data);
        byte[] postData = data.getBytes(StandardCharsets.UTF_8);
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setReadTimeout(REQUEST_TIMEOUT);
        conn.setConnectTimeout(REQUEST_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setUseCaches(false);

        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).setRequestMethod("POST");
        }

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setRequestMethod("POST");
            configureSsl();
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = 0;
        String responseMessage = "Can not perform request";

        if (conn instanceof HttpURLConnection) {
            responseCode = ((HttpURLConnection) conn).getResponseCode();
            responseMessage = ((HttpURLConnection) conn).getResponseMessage();
        }

        if (conn instanceof HttpsURLConnection) {
            responseCode = ((HttpsURLConnection) conn).getResponseCode();
            responseMessage = ((HttpsURLConnection) conn).getResponseMessage();
        }

        if (responseCode == 404) {
            return "";
        }

        if (responseCode == 0 || responseCode >= 300) {
            throw new Exception(responseMessage);
        }

        InputStreamReader input = new InputStreamReader(conn.getInputStream());

        BufferedReader reader = new BufferedReader(input);
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;

        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }

        reader.close();
        input.close();

        return stringBuilder.toString();
    }

    private static void configureSsl() throws NoSuchAlgorithmException, KeyManagementException {
        if (sslConfigured) {
            return;
        }

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        sslConfigured = true;
    }
}
