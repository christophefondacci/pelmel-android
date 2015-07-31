package com.nextep.pelmel.model.db;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cfondacci on 30/07/15.
 */
public class MessageRecipient extends RealmObject {

    @PrimaryKey
    private String itemKey;
    private Date lastMessageDate;
    private boolean lastMessageDefined;
    private int unreadMessageCount;

    private String imageKey;
    private String imageUrl;
    private String imageThumbUrl;

    private String username;

    private RealmList<MessageRecipient> users;
    private RealmList<Message> messages;

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }


    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageThumbUrl() {
        return imageThumbUrl;
    }

    public void setImageThumbUrl(String imageThumbUrl) {
        this.imageThumbUrl = imageThumbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RealmList<MessageRecipient> getUsers() {
        return users;
    }

    public void setUsers(RealmList<MessageRecipient> users) {
        this.users = users;
    }

    public void setMessages(RealmList<Message> messages) {
        this.messages = messages;
    }

    public RealmList<Message> getMessages() {
        return messages;
    }

    public void setLastMessageDefined(boolean lastMessageDefined) {
        this.lastMessageDefined = lastMessageDefined;
    }

    public boolean isLastMessageDefined() {
        return lastMessageDefined;
    }
}
