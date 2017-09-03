package com.example.bironu.simpletransceiver.activitys.main.data;

import android.net.Uri;

import com.example.bironu.simpletransceiver.activitys.main.domain.MainRepository;
import com.example.bironu.simpletransceiver.data.DataStore;
import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;

import java.net.InetAddress;

/**
 *
 UseCaseで取得したいデータのCRUD相当のI/Fを記述する
 データ取得に必要なDataStoreへデータ処理のリクエストを行う
 Repositoryは、データを扱うI/Fを定義するがどうやってデータを扱うか知らない

 */

public class MainRepositoryImpl implements MainRepository {
    private final DataStore mDataStore;

    public MainRepositoryImpl(DataStore dataStore) {
        mDataStore = dataStore;
    }

    @Override
    public void addForwardIpAddress(String ipAddress) {
        mDataStore.addForwardIpAddress(ipAddress);
    }

    @Override
    public void removeForwardIpAddress(long id) {
        mDataStore.removeForwardIpAddress(id);
    }

    @Override
    public void setSpeakerMode(boolean isChecked) {
        mDataStore.setSpeakerMode(isChecked);
    }

    @Override
    public boolean getSpeakerMode() {
        return mDataStore.isSpeakerMode();
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

    @Override
    public boolean isSpeakerMode() {
        return mDataStore.isSpeakerMode();
    }

    @Override
    public void setCursorLoadListener(CursorLoadListener listener) {
        mDataStore.setCursorLoadListener(listener);
    }

    @Override
    public void queryCursor(Uri uri) {
        mDataStore.queryCursor(uri);
    }
}
