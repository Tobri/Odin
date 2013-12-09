package com.tobri.first;

import java.lang.*;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by studat on 27.11.13.
 * Hinweis: muss von SQLiteOpenHelper abgeleitet werden, sonst Fehler beim Öffnen der Datenbank
 */
public class DBConnector extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int    DATABASE_VERSION    = 1;

    // Database Name
    private static final String DATABASE_NAME       = "messagesManager";

    // Messages table name
    private static final String TABLE_MESSAGES      = "messages";

    // Messages Table Columns names
    private static final String KEY_ID              = "id";
    private static final String KEY_SENDER          = "name";
    private static final String KEY_RCVR            = "receiver";
    private static final String KEY_RCVD            = "received";
    private static final String KEY_TEXT            = "text";
    private static final String KEY_ADDITIONAL      = "additional";

    public DBConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE =
                "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID         + " INTEGER PRIMARY KEY,"
                + KEY_SENDER     + " TEXT,"
                + KEY_RCVR       + " TEXT,"
                + KEY_RCVD       + " TEXT,"
                + KEY_TEXT       + " TEXT,"
                + KEY_ADDITIONAL + " TEXT"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new message
    void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, message.getSender());
        values.put(KEY_RCVR, message.getReceiver());
        values.put(KEY_RCVD, message.getReceived());
        values.put(KEY_TEXT, message.getMessage());
        values.put(KEY_ADDITIONAL, message.getAdditional().toString());

        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single message
    Message getMessage(int id) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGES,
                new String[] { KEY_ID, KEY_SENDER, KEY_RCVR, KEY_RCVD, KEY_TEXT, KEY_ADDITIONAL },
                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Message message = new Message(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                new JSONArray(cursor.getString(5))
        );
        // return message
        return message;
    }

    // Getting All Messages
    public List<Message> getAllMessages() throws JSONException {
        List<Message> messageList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Integer.parseInt(cursor.getString(0)));
                message.setSender(cursor.getString(1));
                message.setReceiver(cursor.getString(2));
                message.setReceived(cursor.getString(3));
                message.setMessage(cursor.getString(4));
                message.setAdditional(new JSONArray(cursor.getString(5)));
                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        // return message list
        return messageList;
    }

    // Getting All Messages From Specific Sender
    public List<Message> getAllMessages(String sender) throws JSONException {
        List<Message> messageList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES
                + " WHERE " + KEY_SENDER + " LIKE '" + sender + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Integer.parseInt(cursor.getString(0)));
                message.setSender(cursor.getString(1));
                message.setReceiver(cursor.getString(2));
                message.setReceived(cursor.getString(3));
                message.setMessage(cursor.getString(4));
                message.setAdditional(new JSONArray(cursor.getString(5)));
                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        // return message list
        return messageList;
    }

    // Getting All Senders
    public List<String> getAllSenders() {
        List<String> senderList = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT " + KEY_SENDER
                + " FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                senderList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return senderList;
    }

    // Updating single message
    public int updateMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, message.getSender());
        values.put(KEY_RCVR, message.getReceiver());
        values.put(KEY_RCVD, message.getReceived());
        values.put(KEY_TEXT, message.getMessage());
        values.put(KEY_ADDITIONAL, message.getAdditional().toString());

        // updating row
        return db.update(TABLE_MESSAGES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getId()) });
    }

    // Deleting single message
    public void deleteMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getId()) });
        db.close();
    }

    // Getting messages Count
    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
