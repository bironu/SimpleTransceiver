package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.database.Cursor;

import java.net.InetAddress;

/**
 * MainPreseterのinterface.
 */
public interface MainPresenter
{
    void setSpeakerMode(boolean isChecked);

    void setSendStatus(boolean isChecked);

    void updateLocalIpAddress(InetAddress ipAddress);

    void setForwardIpAddressCursor(Cursor cursor);

    void setIsReceiveRtp(boolean b);

    void setForwardIpAddress(String ipAddress);
}
