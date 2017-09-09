package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.database.Cursor;

import java.net.InetAddress;

/**
 * UseCaseから受け取ったデータをViewへ渡す。
 * Viewがどうなっているか知らない。
 */
public class MainPresenterImpl implements MainPresenter {

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
