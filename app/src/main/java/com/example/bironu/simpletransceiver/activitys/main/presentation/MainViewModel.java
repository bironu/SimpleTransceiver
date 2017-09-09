package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.bironu.simpletransceiver.BR;

/**
 * 各種Viewとbindingされたプロパティ群。
 */
public class MainViewModel extends BaseObservable
        implements SetViewModel
        , GetViewModel
{
    private String mLocalIpAddress;
    private String mForwardIpAddress;
    private boolean mReceiveRtp;
    private boolean mSendStatus;
    private boolean mSpeakerMode;
    private Cursor mCursor;

    @Bindable
    public boolean isReceiveRtp() {
        return mReceiveRtp;
    }

    public void setIsReceiveRtp(boolean receiveRtp) {
        mReceiveRtp = receiveRtp;
        this.notifyPropertyChanged(BR.receiveRtp);
    }

    @Bindable
    public String getLocalIpAddress() {
        return mLocalIpAddress;
    }

    public void setLocalIpAddress(String ipAddress) {
        mLocalIpAddress = ipAddress;
        this.notifyPropertyChanged(BR.localIpAddress);
    }

    @Bindable
    public String getForwardIpAddress() {
        return mForwardIpAddress;
    }

    public void setForwardIpAddress(String forwardIpAddress) {
        mForwardIpAddress = forwardIpAddress;
        this.notifyPropertyChanged(BR.forwardIpAddress);
    }

    @Bindable
    public Cursor getForwardIpAddressCursor() {
        return mCursor;
    }

    @Override
    public void setForwardIpAddressCursor(Cursor cursor) {
        mCursor = cursor;
        this.notifyPropertyChanged(BR.forwardIpAddressCursor);
    }

    @Bindable
    public boolean isSendStatus() {
        return mSendStatus;
    }

    public void setSendStatus(boolean sendStatus) {
        mSendStatus = sendStatus;
        this.notifyPropertyChanged(BR.sendStatus);
    }

    @Bindable
    public boolean isSpeakerMode() {
        return mSpeakerMode;
    }

    public void setSpeakerMode(boolean checkSpeaker) {
        this.mSpeakerMode = checkSpeaker;
        this.notifyPropertyChanged(BR.speakerMode);
    }

}
