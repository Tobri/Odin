package com.tobri.first;

import java.lang.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

/**
 * Created by studat on 27.11.13.
 */
public class DBConnector extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int    DATABASE_VERSION    = 1;

    // Database Name
    private static final String DATABASE_NAME       = "messagesManager";

    // Messages table name
    private static final String TABLE_CONTACTS      = "messages";

    // Messages Table Columns names
    private static final String KEY_ID              = "id";
    private static final String KEY_SENDER          = "name";
    private static final String KEY_RCVD            = "received";
    private static final String KEY_TEXT            = "text";
    private static final String KEY_ADDITIONAL      = "additional";

    public DBConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //    public Message[] getMessages() {
//        String [] cols = {""};
//        Cursor cur = this.localHistory.query(this.table, cols, null, null, null, null, "");
//    }
}
