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
}
