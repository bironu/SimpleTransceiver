package com.example.bironu.simpletransceiver.service;

import android.os.IBinder;

import java.net.InetAddress;
import java.util.List;

/**
 * RtpService操作用バインダーのインターフェイス。
 */
public interface IRtpServiceBinder extends AutoCloseable, IBinder {
    boolean beginRtpReceiver(CtrlPacketStart start, InetAddress remoteAddress);
    void endRtpReceiver(CtrlPacketStop stop);
    void endRtpReceiver();
    boolean beginRtpSender();
    void endRtpSender();
    void beginCtrlReceiver();
    void endCtrlReceiver();
    void addSendTarget(String address, int ctrlPort, int rtpPort);
    void removeSendTarget(String address, int ctrlPort, int rtpPort);
    void removeSendTarget(int location);
    void clearSendTarget();
    List<PacketOutputter.SendTarget> getCtrlSendTargetList();
    List<PacketOutputter.SendTarget> getRtpSendTargetList();
    List<String> getAddressList();
    void setLocalIpAddress();
    void sendCtrlPacket(CtrlPacket packet);
}
