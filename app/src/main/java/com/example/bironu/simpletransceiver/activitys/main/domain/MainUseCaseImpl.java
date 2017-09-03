package com.example.bironu.simpletransceiver.activitys.main.domain;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.bironu.simpletransceiver.activitys.FinishActivityEvent;
import com.example.bironu.simpletransceiver.activitys.StartPreferenceActivityEvent;
import com.example.bironu.simpletransceiver.data.db.SendTargetTable;
import com.example.bironu.simpletransceiver.service.IRtpServiceBinder;

import org.greenrobot.eventbus.EventBus;

/**
 *
 ユースケースに必要なロジック処理を記述する
 どのデータをどのように取得するかここで実装する
 UIには直接関与しない(View,ViewControllerから直接参照されない)

 */

public class MainUseCaseImpl implements MainUseCase {
    private final MainRepository mRepository;
    private final MainPresenter mPresenter;
    private IRtpServiceBinder mBinder;

    public MainUseCaseImpl(@NonNull MainRepository repository, @NonNull MainPresenter presenter) {
        mRepository = repository;
        mPresenter = presenter;
        mRepository.setCursorLoadListener(this);
    }

    @Override
    public void addForwardIpAddress(String ipAddress) {
        if (!TextUtils.isEmpty(ipAddress)) {
            mRepository.addForwardIpAddress(ipAddress);
            mPresenter.setForwardIpAddress("");
        }
    }

    @Override
    public void removeForwardIpAddress(long id) {
        mRepository.removeForwardIpAddress(id);
    }

    @Override
    public void changeSpeakerMode(boolean isChecked) {
        mRepository.setSpeakerMode(isChecked);
        mPresenter.setSpeakerMode(mRepository.getSpeakerMode());
    }

    @Override
    public void startSendRtp() {
        if (mBinder != null) {
            if (!mBinder.beginRtpSender()) {
                mPresenter.setSendStatus(false);
            }
        }
        else {
            mPresenter.setSendStatus(false);
        }
    }

    @Override
    public void stopSendRtp() {
        if (mBinder != null) {
            mBinder.endRtpSender();
        }
    }

    @Override
    public void onServiceConnected(IRtpServiceBinder iBinder) {
        mBinder = iBinder;
        mRepository.queryCursor(SendTargetTable.CONTENT_URI);
    }

    @Override
    public void onServiceDisconnected() {
        mBinder = null;
    }

    @Override
    public void startPreferencesActivity() {
        EventBus.getDefault().post(new StartPreferenceActivityEvent());
    }

    @Override
    public void finishMainActivity() {
        EventBus.getDefault().post(new FinishActivityEvent());
    }

    @Override
    public void changeConnectivity() {
        mPresenter.updateLocalIpAddress(mRepository.getLocalIpAddress());
    }

    @Override
    public void startReceiveRtp() {
        mPresenter.setIsReceiveRtp(true);
    }

    @Override
    public void stopReceiveRtp() {
        mPresenter.setIsReceiveRtp(false);
    }

    @Override
    public void initialize() {
        mPresenter.setSpeakerMode(mRepository.isSpeakerMode());
    }

    @Override
    public void dispose() {
        mPresenter.setForwardIpAddressCursor(null);
    }

    @Override
    public void onCursorLoadFinish(int id, Cursor cursor) {
        if (mBinder != null) {
            mBinder.onCursorLoadFinish(id, cursor);
        }
        switch(id) {
            case SendTargetTable.LOADER_ID:
                mPresenter.setForwardIpAddressCursor(cursor);
                break;

            default:
                break;
        }
    }
}
