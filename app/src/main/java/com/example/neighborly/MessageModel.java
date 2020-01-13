package com.example.neighborly;

import java.util.Date;

public class MessageModel {
    private String senderUid;
    private String sender;
    private String text;
    private long sentTime;
    private String id;

    public MessageModel() {
    }

    public MessageModel(String sender, String text, String senderUid) {
        this.sender = sender;
        this.text = text;
        this.sentTime = new Date().getTime();
        this.senderUid = senderUid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
