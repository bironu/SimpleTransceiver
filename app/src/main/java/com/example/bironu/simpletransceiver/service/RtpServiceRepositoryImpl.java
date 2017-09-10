package com.example.bironu.simpletransceiver.service;

import android.content.Context;

import com.example.bironu.simpletransceiver.data.DataStore;
import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;

import java.net.InetAddress;

/**
 * UseCaseで取得したいデータのCRUD相当のI/Fを記述する
 * データ取得に必要なDataStoreへデータ処理のリクエストを行う
 * Repositoryは、データを扱うI/Fを定義するがどうやってデータを扱うか知らない
 */

public class RtpServiceRepositoryImpl implements RtpServiceRepository
{
    private final DataStore mDataStore;

    public RtpServiceRepositoryImpl(Context context) {
        mDataStore = new DataStore(context);
    }

    @Override
    public InetAddress getLocalIpAddress() {
        return mDataStore.getLocalIpAddress();
    }

    @Override
    public int getRtpPort() {
        return mDataStore.getRtpPort();
    }

    @Override
    public int getCtrlPort() {
        return mDataStore.getCtrlPort();
    }

//    @Override
//    public Cursor getSendTargetCursor() {
//        return mDataStore.getSendTargetCursor();
//    }
//
//    @Override
//    public List<Entity.SendTarget> getSendTargetList() {
//        List<Entity.SendTarget> result = null;
//        try(final Cursor cursor = getSendTargetCursor()) {
//            if (cursor != null && cursor.getCount() > 0) {
//                final int rtpPort = getRtpPort();
//                final int ctrlPort = getCtrlPort();
//                final int indexName = cursor.getColumnIndex(SendTargetTable.COLUMN_NAME);
//                final int indexAddress = cursor.getColumnIndex(SendTargetTable.COLUMN_ADDRESS);
//                result = new ArrayList<>(cursor.getCount());
//                while (cursor.moveToNext()) {
//                    result.add(new Entity.SendTarget(cursor.getString(indexName), cursor.getString(indexAddress), rtpPort, ctrlPort));
//                }
//            }
//        }
//        return result;
//    }

    @Override
    public void setCursorLoadListener(CursorLoadListener listener) {
        mDataStore.setCursorLoadListener(listener);
    }
}
