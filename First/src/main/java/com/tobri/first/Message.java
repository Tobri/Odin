package com.tobri.first;

import java.util.Date;

/**
 * Created by studat on 02.12.13.
 */
public class Message {
    protected String    sender;
    protected Date      received;
    protected String    message;
    protected String    optInfo;

    public Message(String sender, Date received, String message) {
        this.sender     = sender;
        this.received   = received;
        this.message    = message;
        this.optInfo    = null;
    }

    public Message(String sender, Date received, String message, String optInfo) {
        this.sender     = sender;
        this.received   = received;
        this.message    = message;
        this.optInfo    = optInfo;
    }

    public String getOptInfo() {
        return optInfo;
    }

    public void setOptInfo(String optInfo) {
        this.optInfo = optInfo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
