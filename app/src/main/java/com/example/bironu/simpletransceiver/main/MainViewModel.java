package com.example.bironu.simpletransceiver.main;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.bironu.simpletransceiver.BR;
import com.example.bironu.simpletransceiver.CommonUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainViewModel extends BaseObservable {
    private String mLocalIpAddress;
    private String mForwardIpAddress;
    private List<String> mForwardIpAddressList = new ArrayList<>();
    private boolean mReceiveRtp;
    private boolean mSendMode;
    private boolean mSpeakerMode;

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

    public void setLocalIpAddress() {
        InetAddress ipAddress = CommonUtils.getIPAddress();
        if (ipAddress != null) {
            setLocalIpAddress(ipAddress.toString());
        }
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
    public List<String> getForwardIpAddressList() {
        return mForwardIpAddressList;
    }

    public void setForwardIpAddressList(List<String> forwardIpAddressList) {
        mForwardIpAddressList = forwardIpAddressList;
        this.notifyPropertyChanged(BR.forwardIpAddressList);
    }
    public void addForwardIpAddress(String ipAddress) {
        mForwardIpAddressList.add(ipAddress);
        this.notifyPropertyChanged(BR.forwardIpAddressList);
    }
    public void removeForwardIpAddress(int i) {
        mForwardIpAddressList.remove(i);
        this.notifyPropertyChanged(BR.forwardIpAddressList);
    }

    @Bindable
    public boolean isSendMode() {
        return mSendMode;
    }

    public void setSendMode(boolean sendMode) {
        mSendMode = sendMode;
        this.notifyPropertyChanged(BR.sendMode);
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
