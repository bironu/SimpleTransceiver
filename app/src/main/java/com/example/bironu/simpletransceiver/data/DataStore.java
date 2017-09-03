package com.example.bironu.simpletransceiver.data;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;
import com.example.bironu.simpletransceiver.data.db.SendTargetTable;
import com.example.bironu.simpletransceiver.data.preference.Preferences;

import java.net.InetAddress;

/**
 *
 データを実際に取得更新する処理を記述する
 サーバからデータを取得するか、DBやキャッシュのデータを使用するかどうかもここで判断する
 iOSでは、API通信、Realm、CoreData等を扱う実装に相当
 複数のDataStoreを扱う場合はFactoryパターンを用いてRepositoryがData種別を意識しない設計にする

 */

public class DataStore implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DataStore.class.getSimpleName();
    private final Context mContext;
    private CursorLoadListener mListener;

    public DataStore(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 自分のIPアドレスを取得する。
     * @return IPアドレス
     */
    public InetAddress getLocalIpAddress() {
        return CommonUtils.getIPAddress();
    }

    public int getRtpPort() {
        Preferences prefs = new Preferences(mContext);
        return prefs.getRtpPort();
    }

    public int getCtrlPort() {
        Preferences prefs = new Preferences(mContext);
        return prefs.getCtrlPort();
    }

    public void setSpeakerMode(boolean isChecked) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(isChecked);
    }

    public boolean isSpeakerMode() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return am.isSpeakerphoneOn();
    }

    public void addForwardIpAddress(String ipAddress) {
        final ContentValues values = new ContentValues();
        values.put(SendTargetTable.COLUMN_NAME, ipAddress);
        values.put(SendTargetTable.COLUMN_ADDRESS, ipAddress);
        mContext.getContentResolver().insert(SendTargetTable.CONTENT_URI, values);
    }

    public void removeForwardIpAddress(long id) {
        final String where = SendTargetTable._ID + "=?";
        final String[] selectionArgs = {String.valueOf(id)};
        mContext.getContentResolver().delete(SendTargetTable.CONTENT_URI, where, selectionArgs);
    }

    public Cursor getSendTargetCursor() {
        return mContext.getContentResolver().query(SendTargetTable.CONTENT_URI, null, null, null, null);
    }

    public void setCursorLoadListener(CursorLoadListener listener) {
        mListener = listener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CommonUtils.logd(TAG, "onCreateLoader call");
        switch(id) {
            case SendTargetTable.LOADER_ID:
                return new CursorLoader(mContext, SendTargetTable.CONTENT_URI, null, null, null, null);

            default:
                throw new IllegalArgumentException("Unknown loader id [" + id + "].");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        CommonUtils.logd(TAG, "onLoadFinished call");
        if (mListener != null) {
            mListener.onCursorLoadFinish(loader.getId(), data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        CommonUtils.logd(TAG, "onLoaderReset call");
        if (mListener != null) {
            mListener.onCursorLoadFinish(loader.getId(), null);
        }
    }

    public void queryCursor(Uri uri) {
        mContext.getContentResolver().notifyChange(uri, null);
    }
}
