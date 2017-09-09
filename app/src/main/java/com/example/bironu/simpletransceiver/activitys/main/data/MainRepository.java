package com.example.bironu.simpletransceiver.activitys.main.data;

import android.net.Uri;

import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;

import java.net.InetAddress;

/**
 * Repositoryのinterface.
 */
public interface MainRepository
{
    void addForwardIpAddress(String ipAddress);

    void setSpeakerMode(boolean isChecked);

    boolean getSpeakerMode();

    void removeForwardIpAddress(long id);

    InetAddress getLocalIpAddress();

    int getRtpPort();

    int getCtrlPort();

    boolean isSpeakerMode();

    void setCursorLoadListener(CursorLoadListener listener);

    void queryCursor(Uri uri);
}
