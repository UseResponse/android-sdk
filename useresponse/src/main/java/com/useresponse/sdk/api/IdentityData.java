package com.useresponse.sdk.api;

import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import java.util.ArrayList;

public class IdentityData {
    final public String AVATAR_TYPE_URL      = "url";
    final public String AVATAR_TYPE_CONTENT  = "content";
    final public String AVATAR_TYPE_GRAVATAR = "gravatar";

    private String token;
    private String email;
    private String id;
    private String firstName;
    private String lastName;
    private String avatarType;
    private String avatarContent;
    private SparseArray<String> properties = new SparseArray<>();

    public IdentityData(String token, String email) throws IllegalArgumentException {
        if (token == null || token.length() == 0 || email == null || email.length() == 0) {
            throw new IllegalArgumentException("Identity token or email is invalid");
        }

        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        ArrayList<String> names = new ArrayList<>();

        if (firstName != null && firstName.trim().length() > 0) {
            names.add(firstName.trim());
        }

        if (lastName != null && lastName.trim().length() > 0) {
            names.add(lastName.trim());
        }

        return TextUtils.join(" ", names);
    }

    public void setProperty(int id, String value) {
        properties.put(id, value);
    }

    public SparseArray<String> getProperties() {
        return properties;
    }

    public void setAvatar(String avatarType, String avatarContent) throws Exception {
        if (!avatarType.equals(AVATAR_TYPE_URL) && !avatarType.equals(AVATAR_TYPE_CONTENT) && !avatarType.equals(AVATAR_TYPE_GRAVATAR)) {
            throw new Exception("Invalid avatar type");
        }

        if (avatarType.equals(AVATAR_TYPE_CONTENT)) {
            byte[] contentArray = new byte[avatarContent.length()];

            for (int i = 0; i < avatarContent.length(); i++) {
                contentArray[i] = (byte)avatarContent.charAt(i);
            }

            avatarContent = Base64.encodeToString(contentArray, 0);
        }

        this.avatarType = avatarType;
        this.avatarContent = avatarContent;
    }

    public String getAvatarType() {
        return avatarType != null ? avatarType : "";
    }

    public String getAvatarContent() {
        return avatarContent != null ? avatarContent : "";
    }
}
