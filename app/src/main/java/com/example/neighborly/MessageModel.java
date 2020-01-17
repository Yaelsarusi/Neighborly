package com.example.neighborly;

import java.util.Date;

public class MessageModel {
    private UserModel sender;
    private String text;
    private long sentTime;
    private String messageId;

    public MessageModel() {
    }

    public MessageModel(UserModel sender, String text) {
        this.sender = sender;
        this.text = text;
        this.sentTime = new Date().getTime();
    }

    public UserModel getSender() {
        return this.sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    public String getSenderUserPresentedName() {
        return this.sender.getUserPresentedName();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return this.sender.getId();
    }

    public long getSentTime() {
        return this.sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getSenderImageUri() {
        return this.sender.getImageUriString();
    }

}
