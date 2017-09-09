package com.example.bironu.simpletransceiver.activitys.main.domain;

import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;
import com.example.bironu.simpletransceiver.service.IRtpServiceBinder;

/**
 * UseCaseのinterface.
 */
public interface MainUseCase extends CursorLoadListener
{
    void addForwardIpAddress(String ipAddress);

    void changeSpeakerMode(boolean isChecked);

    void startSendRtp();

    void stopSendRtp();

    void removeForwardIpAddress(long id);

    void onServiceConnected(IRtpServiceBinder iBinder);

    void onServiceDisconnected();

    void startPreferencesActivity();

    void finishMainActivity();

    void changeConnectivity();

    void startReceiveRtp();

    void stopReceiveRtp();

    void initialize();

    void dispose();
}
