package com.tobri.first;

import java.lang.*;

import android.database.Cursor;
import android.database.sqlite.*;

/**
 * Created by studat on 27.11.13.
 */
public class DBConnector {
    protected SQLiteDatabase localHistory;
    protected String dbName;
    protected String table;

    public DBConnector() {
        this.dbName = "LocalHistory";
        this.table  = "LocalUser";
        this.localHistory = SQLiteDatabase.openDatabase(this.dbName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }
}
