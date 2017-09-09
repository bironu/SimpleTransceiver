package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.database.Cursor;

/**
 * MainViewModelのsetterだけ集めたinterface.
 */
public interface SetViewModel
{
    void setIsReceiveRtp(boolean receiveRtp);

    void setLocalIpAddress(String ipAddress);

    void setForwardIpAddress(String forwardIpAddress);

    void setForwardIpAddressCursor(Cursor cursor);

    void setSendStatus(boolean sendStatus);

    void setSpeakerMode(boolean checkSpeaker);
}
