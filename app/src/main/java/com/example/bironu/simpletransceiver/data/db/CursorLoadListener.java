package com.example.bironu.simpletransceiver.data.db;

import android.database.Cursor;

/**
 * Created by unko on 2017/09/02.
 */

public interface CursorLoadListener {
    void onCursorLoadFinish(int id, Cursor cursor);
}
