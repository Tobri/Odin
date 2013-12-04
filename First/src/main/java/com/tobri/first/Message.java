package com.tobri.first;


/**
 * Created by studat on 02.12.13.
 */
public class Message {
    protected Integer   id;
    protected String    sender;
    protected String    receiver;
    protected String    received;
    protected String    message;
    protected String    additional;

    public Message() { }

    public Message(Integer id, String sender, String receiver, String received, String message) {
        this.sender     = sender;
        this.receiver   = receiver;
        this.received   = received;
        this.message    = message;
        this.additional = null;
    }

    public Message(Integer id, String sender, String receiver, String received, String message, String additional) {
        this.sender     = sender;
        this.receiver   = receiver;
        this.received   = received;
        this.message    = message;
        this.additional = additional;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
