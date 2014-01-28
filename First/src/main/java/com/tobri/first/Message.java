package com.tobri.first;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by studat on 02.12.13.
 */
public class Message {
    static String TAG_ID         = "id";
    static String TAG_SENDER     = "sender";
    static String TAG_RECEIVER   = "receiver";
    static String TAG_RECEIVED   = "received";
    static String TAG_MESSAGE    = "message";
    static String TAG_ADDITIONAL = "additional";

    protected Integer id;
    protected String sender;
    protected String receiver;
    protected String received;
    protected String message;
    protected JSONArray additional;

    public Message() {
    }

    public Message(Integer id, String sender, String receiver, String received, String message) throws JSONException {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.received = received;
        this.message = message;
        this.additional = new JSONArray();
    }

    public Message(Integer id, String sender, String receiver, String received, String message, JSONArray additional) throws JSONException {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.received = received;
        this.message = message;
        this.additional = additional;
    }

    public Message(JSONObject json) throws JSONException {
        if (!json.isNull(TAG_ID)) {
            this.id = json.getInt(TAG_ID);
        }
        this.sender = json.getString(TAG_SENDER);
        this.receiver = json.getString(TAG_RECEIVER);
        this.received = json.getString(TAG_RECEIVED);
        this.message = json.getString(TAG_MESSAGE);
        if (!json.isNull(TAG_ADDITIONAL)) {
            this.additional = json.getJSONArray(TAG_ADDITIONAL);
        } else {
            this.additional = new JSONArray();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public JSONArray getAdditional() {
        return additional;
    }

    public void setAdditional(JSONArray additional) {
        this.additional = additional;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject tmp = new JSONObject();

        tmp.put(TAG_ID, this.id);
        tmp.put(TAG_SENDER, this.sender);
        tmp.put(TAG_RECEIVER, this.receiver);
        tmp.put(TAG_RECEIVED, this.received);
        tmp.put(TAG_MESSAGE, this.message);
        tmp.put(TAG_ADDITIONAL, this.additional);

        return tmp;
    }

    /**
     * ToDo: Reimplement for final version!
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getSender() + ": " + this.getMessage();
    }
}
