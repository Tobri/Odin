package com.tobri.first;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by studat on 27.11.13.
 * Hinweis: muss von SQLiteOpenHelper abgeleitet werden, sonst Fehler beim Öffnen der Datenbank
 */
public class DBConnector extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "messagesManager";

    // Messages table name
    private static final String TABLE_MESSAGES = "messages";

    // Messages Table Columns names
    private static final String KEY_ID          = "id";
    private static final String KEY_OBJECTID    = "objectid";
    private static final String KEY_SENDER      = "name";
    private static final String KEY_RCVR        = "receiver";
    private static final String KEY_RCVD        = "received";
    private static final String KEY_TEXT        = "text";
    private static final String KEY_ADDITIONAL  = "additional";

    public DBConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE =
                "CREATE TABLE " + TABLE_MESSAGES + "("
                        + KEY_ID            + " INTEGER PRIMARY KEY,"
                        + KEY_OBJECTID      + " TEXT,"
                        + KEY_SENDER        + " TEXT,"
                        + KEY_RCVR          + " TEXT,"
                        + KEY_RCVD          + " TEXT,"
                        + KEY_TEXT          + " TEXT,"
                        + KEY_ADDITIONAL    + " TEXT"
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
    long addMessage(Message message) {
        long ret;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OBJECTID, message.getObjectid());
        values.put(KEY_SENDER, message.getSender());
        values.put(KEY_RCVR, message.getReceiver());
        values.put(KEY_RCVD, message.getReceived());
        values.put(KEY_TEXT, message.getMessage());
        values.put(KEY_ADDITIONAL, message.getAdditional().toString());
        
        if (getMessageID(message.getObjectid()) != null) {
            ret = updateMessage(message);
        } else {
            // Inserting Row
            assert db != null;
            ret = db.insert(TABLE_MESSAGES, null, values);
        }

        db.close(); // Closing database connection
        return ret;
    }

    Long getMessageID(String objectid) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = null;
        
        try {
            assert db != null;
            cursor = db.query(TABLE_MESSAGES,
                    new String[]{KEY_ID},
                    KEY_OBJECTID + "=?", new String[]{objectid}, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
        } catch (NullPointerException npe) {
            Log.e("DBConnector: ", npe.getMessage());
        }

        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();

        try {
            return Long.decode(cursor.getString(0));
        } catch (Exception e) {
            return null;
        }
    }

    // Getting single message
    Message getMessage(Long id) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        try {
            assert db != null;
            cursor = db.query(TABLE_MESSAGES,
                new String[]{KEY_ID, KEY_OBJECTID, KEY_SENDER, KEY_RCVR, KEY_RCVD, KEY_TEXT, KEY_ADDITIONAL},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        } catch (NullPointerException npe) {
            Log.e("DBConnector: ", npe.getMessage());
        }

        if (cursor != null)
            cursor.moveToFirst();

        // return message
        return new Message(
                Long.decode(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                new JSONArray(cursor.getString(6))
        );
    }

    // Getting All Messages
    public List<Message> getAllMessages() throws JSONException {
        List<Message> messageList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Long.decode(cursor.getString(0)));
                message.setObjectid(cursor.getString(1));
                message.setSender(cursor.getString(2));
                message.setReceiver(cursor.getString(3));
                message.setReceived(cursor.getString(4));
                message.setMessage(cursor.getString(5));
                message.setAdditional(new JSONArray(cursor.getString(6)));

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
                + " WHERE " + KEY_SENDER + " LIKE '" + sender + "'"
                + " OR " + KEY_RCVR + " LIKE '" + sender + "'"
                + " ORDER BY " + KEY_RCVD;

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Long.decode(cursor.getString(0)));
                message.setObjectid(cursor.getString(1));
                message.setSender(cursor.getString(2));
                message.setReceiver(cursor.getString(3));
                message.setReceived(cursor.getString(4));
                message.setMessage(cursor.getString(5));
                message.setAdditional(new JSONArray(cursor.getString(6)));

                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return message list
        return messageList;
    }

    // Getting All Senders
    public List<String> getAllSenders(String username) {
        List<String> senderList = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT " + KEY_SENDER
                + " FROM " + TABLE_MESSAGES
                + " WHERE " + KEY_RCVR + " LIKE \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                senderList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return senderList;
    }

    // Updating single message
    public int updateMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OBJECTID, message.getObjectid());
        values.put(KEY_SENDER, message.getSender());
        values.put(KEY_RCVR, message.getReceiver());
        values.put(KEY_RCVD, message.getReceived());
        values.put(KEY_TEXT, message.getMessage());
        values.put(KEY_ADDITIONAL, message.getAdditional().toString());

        // updating row
        assert db != null;
        return db.update(TABLE_MESSAGES, values, KEY_ID + "=?",
                new String[]{String.valueOf(message.getId())});
    }

    // Deleting single message
    public void deleteMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_MESSAGES, KEY_ID + " = ?",
                new String[]{String.valueOf(message.getId())});
        db.close();
    }

    // Getting messages Count
    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        assert db != null;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();

        // return count
        return cursor.getCount();
    }

    public void dropAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_MESSAGES, KEY_ID + " >= ?", new String[]{"1"});
        db.close();
    }

}
