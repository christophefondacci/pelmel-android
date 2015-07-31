package com.nextep.pelmel.model.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cfondacci on 30/07/15.
 */
public class Message extends RealmObject {

    @PrimaryKey
    private String messageKey;
    private Date messageDate;
    private boolean unread;
    private String messageImageKey;
    private String messageImageUrl;
    private String messageImageThumbUrl;
    private String messageText;
    @Index
    private String toItemKey;
    private MessageRecipient from;
    private MessageRecipient replyTo;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getMessageImageKey() {
        return messageImageKey;
    }

    public void setMessageImageKey(String messageImageKey) {
        this.messageImageKey = messageImageKey;
    }

    public String getMessageImageUrl() {
        return messageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        this.messageImageUrl = messageImageUrl;
    }

    public String getMessageImageThumbUrl() {
        return messageImageThumbUrl;
    }

    public void setMessageImageThumbUrl(String messageImageThumbUrl) {
        this.messageImageThumbUrl = messageImageThumbUrl;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getToItemKey() {
        return toItemKey;
    }

    public void setToItemKey(String toItemKey) {
        this.toItemKey = toItemKey;
    }

    public MessageRecipient getFrom() {
        return from;
    }

    public void setFrom(MessageRecipient from) {
        this.from = from;
    }

    public MessageRecipient getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(MessageRecipient replyTo) {
        this.replyTo = replyTo;
    }
}
