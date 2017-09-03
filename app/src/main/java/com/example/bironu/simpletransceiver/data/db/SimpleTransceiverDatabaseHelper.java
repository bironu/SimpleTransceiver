package com.example.bironu.simpletransceiver.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */

public class SimpleTransceiverDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "simple_transceiver.db";
    private static final int DB_VER = 1;

    public SimpleTransceiverDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createTable(db);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            dropTable(db);
            createTable(db);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL(SendTargetTable.CREATE_QUERY);
    }

    private void dropTable(SQLiteDatabase db) {
        db.execSQL(SendTargetTable.DROP_QUERY);
    }
}
