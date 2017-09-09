package com.example.bironu.simpletransceiver.activitys.main.presentation;

/**
 * ViewModelのgetterだけ集めたinterface.
 */
public interface GetViewModel
{
    boolean isReceiveRtp();

    String getLocalIpAddress();

    String getForwardIpAddress();

    boolean isSendStatus();

    boolean isSpeakerMode();
}
