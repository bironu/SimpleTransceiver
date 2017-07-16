package com.example.bironu.simpletransceiver.service;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.support.annotation.NonNull;

import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.codecs.ulaw;
import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.common.DataInputter;
import com.example.bironu.simpletransceiver.common.DataOutputter;
import com.example.bironu.simpletransceiver.common.DataRelayer;
import com.example.bironu.simpletransceiver.common.Job;
import com.example.bironu.simpletransceiver.common.JobWorker;
import com.example.bironu.simpletransceiver.common.Worker;
import com.example.bironu.simpletransceiver.preference.Preferences;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * RtpServiceの処理中枢。
 */
class RtpServiceBinderImpl extends Binder
implements IRtpServiceBinder
{
    public static final String TAG = RtpServiceBinderImpl.class.getSimpleName();

    private final Context mContext;
    private final ExecutorService mExec = Executors.newCachedThreadPool();
    private final List<Worker> mWorkerList = new ArrayList<>();
    private Codec mCodec;
    private InetAddress mLocalInetAddress;
    private DataRelayer mMic2Packet;
    private DataRelayer mPacket2Speaker;
    private Worker mCtrlPacketReceiver;
    private final RtpSession mRtpSession = new RtpSession();

    RtpServiceBinderImpl(@NonNull Context context) {
        mContext = context;
        mCodec = new ulaw();
        mCodec.open();
    }

    public boolean beginRtpReceiver(CtrlPacketStart start, InetAddress remoteAddress) {
        boolean result = false;
        try {
            CommonUtils.logd(TAG, "beginRtpReceiver session level = "+mRtpSession.getLevel()+", pakcet session level = "+start.getSessionLevel());
            if(mRtpSession.beginSession(start.getSessionLevel())) {
                CommonUtils.logd(TAG, "beginRtpReceiver OK!!!!!!!");
                // 送信or受信を判定して強制終了　セッション無しなら何もせず
                endRtpReceiver();
                endRtpSender();
                mContext.sendBroadcast(new Intent(RtpService.ACTION_BEGIN_RTP_RECEIVE));
                sendCtrlPacket(start);
                mRtpSession.setSessionParam(start);
                Preferences prefs = new Preferences(mContext);
                final int rtpPort = prefs.getRtpPort();

                RtpPacketInputter packetIn = new RtpPacketInputter(rtpPort, mLocalInetAddress, mRtpSession, remoteAddress);
                DataOutputter speakerOut = new DecryptSpeakerOutputter(mCodec, mRtpSession);

                mPacket2Speaker = new DataRelayer(packetIn);
                List<PacketOutputter.SendTarget> targetList = mRtpSession.getRtpSendTargetList();
                if(targetList != null && targetList.size() > 0) {
                    PacketOutputter packetOut = new PacketOutputter(0, mLocalInetAddress);
                    for(PacketOutputter.SendTarget target : targetList) {
                        packetOut.addSendTarget(target.address, target.port);
                    }
                    mPacket2Speaker.addDataOutputter(packetOut);
                }
                mPacket2Speaker.addDataOutputter(speakerOut);
                mWorkerList.add(mPacket2Speaker);
                mExec.execute(mPacket2Speaker);
                result = true;
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void endRtpReceiver(CtrlPacketStop stop) {
        CommonUtils.logd(TAG, "endRtpReceiver session ssrc = "+mRtpSession.getSsrc()+", packet ssrc = "+stop.getSsrc());
        if(mRtpSession.getSsrc() == stop.getSsrc()) {
            endRtpReceiver();
            sendCtrlPacket(stop);
        }
    }

    public void endRtpReceiver() {
        if(mPacket2Speaker != null) {
            mContext.sendBroadcast(new Intent(RtpService.ACTION_END_RTP_RECEIVE));
            mPacket2Speaker.halt();
            mWorkerList.remove(mPacket2Speaker);
            mPacket2Speaker = null;
            mRtpSession.stopReceiveSession();
        }
    }

    public boolean beginRtpSender() {
        boolean result = false;
        try{
            Preferences prefs = new Preferences(mContext);
            final int accountLevel = prefs.getAccountLevel();
            CommonUtils.logd(TAG, "beginRtpSender session level = "+mRtpSession.getLevel()+", account level = "+accountLevel);
            if(mRtpSession.beginSession(accountLevel)) {
                CommonUtils.logd(TAG, "beginRtpSender OK!!!!!!!");
                // 送信or受信を判定して強制終了　セッション無しなら何もせず
                endRtpReceiver();
                endRtpSender();
                mRtpSession.setSessionParam(accountLevel, mCodec);

                // こいつを先に作るとAES暗号鍵が作られる
                DataInputter micIn = new EncryptMicInputter(mCodec, mRtpSession);
                PacketOutputter packetOut = new RtpPacketOutputter(0, mLocalInetAddress, mRtpSession);

                List<PacketOutputter.SendTarget> rtpTargetList = mRtpSession.getRtpSendTargetList();
                for(PacketOutputter.SendTarget target : rtpTargetList) {
                    packetOut.addSendTarget(target.address, target.port);
                }

                CtrlPacketStart packet = new CtrlPacketStart(mRtpSession);
                CommonUtils.logd(TAG, "sendCtrlPacketStart");
                sendCtrlPacket(packet);

                mMic2Packet = new DataRelayer(micIn);
                mMic2Packet.addDataOutputter(packetOut);
                mWorkerList.add(mMic2Packet);
                mExec.execute(mMic2Packet);
                result = true;
            }
            else {
                CommonUtils.logd(TAG, "beginRtpSender NG!!!!!!!");
            }
        }
        catch(SocketException e) {
            e.printStackTrace();
        }
//		catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
        return result;
    }

    public void endRtpSender() {
        CommonUtils.logd(TAG, "endRtpSender call");
        if(mMic2Packet != null) {
            mMic2Packet.halt();
            mWorkerList.remove(mMic2Packet);
            mMic2Packet = null;
            CtrlPacketStop packet = new CtrlPacketStop(mRtpSession);
            CommonUtils.logd(TAG, "sendCtrlPacketStop");
            sendCtrlPacket(packet);
            mRtpSession.stopSendSession();
        }
    }

    public void beginCtrlReceiver() {
        Preferences prefs = new Preferences(mContext);
        final int ctrlPort = prefs.getCtrlPort();

        try {
            Job job = new CtrlPacketReceiveJob(ctrlPort, mLocalInetAddress, this);
            mCtrlPacketReceiver = new JobWorker(job);
            mWorkerList.add(mCtrlPacketReceiver);
            mExec.execute(mCtrlPacketReceiver);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void endCtrlReceiver() {
        CommonUtils.logd(TAG, "endCtrlReceiver() call.");
        if(mCtrlPacketReceiver != null) {
            mCtrlPacketReceiver.halt();
            CommonUtils.logd(TAG, "mCtrlPacketReceiver.halt() call");
            mWorkerList.remove(mCtrlPacketReceiver);
            mCtrlPacketReceiver = null;
        }
    }

    public void addSendTarget(String address, int ctrlPort, int rtpPort) {
        mRtpSession.addSendTarget(address, ctrlPort, rtpPort);
    }

    public void removeSendTarget(String address, int ctrlPort, int rtpPort) {
        mRtpSession.removeSendTarget(address, ctrlPort, rtpPort);
    }

    public void removeSendTarget(int location) {
        mRtpSession.removeSendTarget(location);
    }

    public void clearSendTarget() {
        mRtpSession.clearSendTarget();
    }

    public List<PacketOutputter.SendTarget> getCtrlSendTargetList() {
        return mRtpSession.getCtrlSendTargetList();
    }

    public List<PacketOutputter.SendTarget> getRtpSendTargetList() {
        return mRtpSession.getRtpSendTargetList();
    }

    public List<String> getAddressList() {
        return mRtpSession.getAddressList();
    }

    public synchronized void setLocalIpAddress() {
        InetAddress localInetAddress = CommonUtils.getIPAddress();
        CommonUtils.logd(TAG, "setLocalIpAddress old address = "+mLocalInetAddress+", new address"+localInetAddress);
        if(localInetAddress != null) {
            if(!localInetAddress.equals(mLocalInetAddress)) {
                mLocalInetAddress = localInetAddress;
                endRtpSender();
                endRtpReceiver();
                endCtrlReceiver();
                beginCtrlReceiver();
            }
        }
        else {
            endRtpSender();
            endRtpReceiver();
            endCtrlReceiver();
        }
    }

    public synchronized void sendCtrlPacket(CtrlPacket packet) {
        CommonUtils.logd(TAG, "sendCtrlPacket call");
        try {
            CtrlPacketSendJob job = new CtrlPacketSendJob(mLocalInetAddress, packet);
            job.addSendTarget(mRtpSession.getCtrlSendTargetList());
            JobWorker sender = new JobWorker(job);
            mWorkerList.add(sender);
            mExec.execute(sender);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        for(Worker worker : mWorkerList) {
            worker.halt();
        }
        mExec.shutdown();
        try {
            if(!mExec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                mExec.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            mExec.shutdownNow();
        }
        if(mCodec != null) {
            mCodec.close();
            mCodec = null;
        }
    }
}
