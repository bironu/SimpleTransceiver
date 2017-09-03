package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.database.Cursor;

import com.example.bironu.simpletransceiver.activitys.main.domain.MainPresenter;

import java.net.InetAddress;

/**
 *
 Viewからイベントを受け取り、必要があればイベントに応じたUseCaseを実行する
 UseCaseから受け取ったデータをViewへ渡す
 Viewがどうなっているか知らない
 */

public class MainPresenterImpl implements MainPresenter {

    public interface SetViewModel {
        void setIsReceiveRtp(boolean receiveRtp);
        void setLocalIpAddress(String ipAddress);
        void setForwardIpAddress(String forwardIpAddress);
        void setForwardIpAddressCursor(Cursor cursor);
        void setSendStatus(boolean sendStatus);
        void setSpeakerMode(boolean checkSpeaker);
    }

    private final SetViewModel mSetter;

    public MainPresenterImpl(SetViewModel setter) {
        mSetter = setter;
    }

    @Override
    public void setSpeakerMode(boolean isChecked) {
        mSetter.setSpeakerMode(isChecked);
    }

    @Override
    public void setSendStatus(boolean isChecked) {
        mSetter.setSendStatus(isChecked);
    }

    @Override
    public void updateLocalIpAddress(InetAddress ipAddress) {
        if (ipAddress != null) {
            mSetter.setLocalIpAddress(ipAddress.toString());

        }
        else {
            mSetter.setLocalIpAddress(null);
        }
    }

    @Override
    public void setForwardIpAddressCursor(Cursor cursor) {
        mSetter.setForwardIpAddressCursor(cursor);
    }

    @Override
    public void setIsReceiveRtp(boolean b) {
        mSetter.setIsReceiveRtp(b);
    }

    @Override
    public void setForwardIpAddress(String ipAddress) {
        mSetter.setForwardIpAddress(ipAddress);
    }
}
