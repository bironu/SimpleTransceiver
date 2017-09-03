package com.example.bironu.simpletransceiver.activitys.main.domain;

import android.database.Cursor;

import java.net.InetAddress;

/**
 * Created by unko on 2017/08/28.
 */

public interface MainPresenter {
    void setSpeakerMode(boolean isChecked);
    void setSendStatus(boolean isChecked);
    void updateLocalIpAddress(InetAddress ipAddress);
    void setForwardIpAddressCursor(Cursor cursor);
    void setIsReceiveRtp(boolean b);
    void setForwardIpAddress(String ipAddress);
}
