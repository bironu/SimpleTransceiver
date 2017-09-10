package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.io.Job;
import com.example.bironu.simpletransceiver.io.PacketInputter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

class CtrlPacketReceiveJob
        implements Job
{
    public static final String TAG = CtrlPacketReceiveJob.class.getSimpleName();

    private final PacketInputter mPacketInputter;
    private final IRtpServiceBinder mBinder;
    private final CtrlPacket mCtrlPacket = new CtrlPacket(null, 0);

    public CtrlPacketReceiveJob(int port, InetAddress addr, IRtpServiceBinder binder) throws SocketException {
        mPacketInputter = new PacketInputter(port, addr, CtrlPacketStart.PACKET_LENGTH);
        mBinder = binder;
    }

    @Override
    public boolean action() throws InterruptedException {
        CommonUtils.logd(TAG, "CtrlPacketReceiveJob#action() call.");
        boolean result = true;
        try {
            final int length = mPacketInputter.input();
            final DatagramPacket packet = mPacketInputter.getPacket();

            mCtrlPacket.setBuffer(mPacketInputter.getBuffer(), length);
            if (mCtrlPacket.getVersion() == 2) {
                switch (mCtrlPacket.getControlType()) {
                case CtrlPacket.CTRL_TYPE_START: {
                    CommonUtils.logd(TAG, "receive CtrlPacket.CTRL_TYPE_START");
                    CtrlPacketStart start = new CtrlPacketStart(mPacketInputter.getBuffer(), length);
                    mBinder.beginRtpReceiver(start, packet.getAddress());
                    break;
                }
                case CtrlPacket.CTRL_TYPE_STOP: {
                    CommonUtils.logd(TAG, "receive CtrlPacket.CTRL_TYPE_STOP");
                    CtrlPacketStop stop = new CtrlPacketStop(mPacketInputter.getBuffer(), length);
                    mBinder.endRtpReceiver(stop);
                    break;
                }
                default:
                    CommonUtils.logw(TAG, "Invalid type = " + mCtrlPacket.getControlType());
                    break;
                }
            }
            else {
                CommonUtils.logw(TAG, "Invalid version = " + mCtrlPacket.getVersion());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() {
        mPacketInputter.close();
    }

}
