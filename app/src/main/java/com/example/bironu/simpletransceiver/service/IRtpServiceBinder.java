package com.example.bironu.simpletransceiver.service;

import android.os.IBinder;

import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;

import java.net.InetAddress;

/**
 * RtpService操作用バインダーのインターフェイス。
 */
public interface IRtpServiceBinder extends AutoCloseable, CursorLoadListener, IBinder {
    boolean beginRtpReceiver(CtrlPacketStart start, InetAddress remoteAddress);
    void endRtpReceiver(CtrlPacketStop stop);
    void endRtpReceiver();
    boolean beginRtpSender();
    void endRtpSender();
    void beginCtrlReceiver();
    void endCtrlReceiver();
    void setLocalIpAddress();
    void sendCtrlPacket(CtrlPacket packet);
}
