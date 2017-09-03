package com.example.bironu.simpletransceiver.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.bironu.simpletransceiver.common.CommonUtils;

/**
 *
 */

public class SimpleTransceiverContentProvider extends ContentProvider {
    // Authority
    public static final String AUTHORITY = SimpleTransceiverContentProvider.class.getName().toLowerCase(); //"com.example.bironu.simpletransceiver.data.db.simpletransceivercontentprovider";

    private static final int SEND_TARGETS = 1;
    private static final int SEND_TARGET_ID = 2;

    // 利用者がメソッドを呼び出したURIに対応する処理を判定処理に使用します
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SendTargetTable.TABLE_NAME, SEND_TARGETS);
        sUriMatcher.addURI(AUTHORITY, SendTargetTable.TABLE_NAME + "/#", SEND_TARGET_ID);
    }

    // DBHelperのインスタンス
    private SimpleTransceiverDatabaseHelper mDBHelper;

    // コンテンツプロバイダの作成
    @Override
    public boolean onCreate() {
        mDBHelper = new SimpleTransceiverDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case SEND_TARGETS:
                qb.setTables(SendTargetTable.TABLE_NAME);
                break;
            case SEND_TARGET_ID:
                qb.setTables(SendTargetTable.TABLE_NAME);
                qb.appendWhere(SendTargetTable._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String insertTable;
        switch (sUriMatcher.match(uri)) {
            case SEND_TARGETS:
                insertTable = SendTargetTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final long rowId = db.insert(insertTable, null, values);
        if (rowId >= 0) {
            Uri returnUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(returnUri, null);
            return returnUri;
        } else {
            throw new IllegalArgumentException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String updateTable;
        switch (sUriMatcher.match(uri)) {
            case SEND_TARGETS:
                updateTable = SendTargetTable.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int count = db.update(updateTable, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        CommonUtils.logd("ContentProvider", "delete");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SEND_TARGETS:
                count = db.delete(SendTargetTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // コンテントタイプ取得
    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case SEND_TARGETS:
                return SendTargetTable.CONTENT_TYPE;
            case SEND_TARGET_ID:
                return SendTargetTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }}
