package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.codecs.Codec;

import java.util.Random;

public class RtpSession
{
    public static final String TAG = RtpSession.class.getSimpleName();

    private static final int INVALID_SESSION_LEVEL = -1;

    private int mPayloadType;
    private long mSsrc;
    private int mSeqNum;
    private long mTimeStamp;
    private int mFrameSize;
    private final Random mRandom;
    private int mSessionLevel = INVALID_SESSION_LEVEL;
    private byte[] mIV;
    private byte[] mKey;

    public RtpSession() {
        this.mRandom = new Random(System.currentTimeMillis() + Thread.currentThread().getId() - Thread.currentThread().hashCode());
    }

    private synchronized void endSession() {
        mSessionLevel = INVALID_SESSION_LEVEL;
        init();
    }

    private synchronized void init() {
        mPayloadType = -1;
        mSsrc = -1;
        mSeqNum = -1;
        mTimeStamp = -1;
        mFrameSize = -1;
    }

    public synchronized boolean beginSession(int level) {
        // レベルで強制割り込み処理
        return mSessionLevel < level;
    }

    public synchronized boolean isSession() {
        return mSessionLevel > INVALID_SESSION_LEVEL;
    }

    public synchronized int getSessionLevel() {
        return mSessionLevel;
    }

    public synchronized void setPayloadType(int payloadType) {
        mPayloadType = payloadType;
    }

    public synchronized int getPayloadType() {
        return mPayloadType;
    }

    public synchronized int getNextSeqNum() {
        ++mSeqNum;
        mSeqNum &= 0xffff;
        return mSeqNum;
    }

    public synchronized void generateSeqNum() {
        setSeqNum(this.mRandom.nextInt());
    }

    public synchronized void setSeqNum(int seqNum) {
        this.mSeqNum = seqNum & 0xffff;
    }

    public synchronized int getSeqNum() {
        return mSeqNum;
    }

    public synchronized void generateSsrc() {
        setSsrc(this.mRandom.nextLong());
    }

    public synchronized void setSsrc(long ssrc) {
        mSsrc = ssrc & 0xffffffff;
    }

    public synchronized long getSsrc() {
        return mSsrc;
    }

    public synchronized void generateTimeStamp() {
        setTimeStamp(this.mRandom.nextLong());
    }

    public synchronized void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp & 0xffffffff;
    }

    public synchronized long getTimeStamp() {
        return mTimeStamp;
    }

    public synchronized long getNextTimeStamp() {
        if (mPayloadType == Codec.TYPE_G722) {
            mTimeStamp += mFrameSize / 2;
        }
        else {
            mTimeStamp += mFrameSize;
        }
        return mTimeStamp;
    }

    public synchronized void setFrameSize(int frameSize) {
        mFrameSize = frameSize;
    }

    public synchronized int getFrameSize() {
        return mFrameSize;
    }

    public synchronized void setSessionParam(CtrlPacketStart start) {
        setPayloadType(start.getPayloadType());
        setSeqNum(start.getSequenceNumber());
        setSsrc(start.getSsrc());
        setTimeStamp(start.getTimestamp());
        setFrameSize(start.getFrameSize());
        setLevel(start.getSessionLevel());
        setKey(start.getKey());
        setIV(start.getIV());
    }

    public synchronized void stopReceiveSession() {
        endSession();
    }

    public synchronized void setSessionParam(int level, Codec codec) {
        generateSsrc();
        generateSeqNum();
        generateTimeStamp();
        setLevel(level);
        setPayloadType(codec.number());
        setFrameSize(codec.frame_size());
    }

    public synchronized void stopSendSession() {
        endSession();
    }

    public synchronized void setKey(byte[] key) {
        mKey = key;
    }

    public synchronized byte[] getKey() {
        return mKey;
    }

    public synchronized void setIV(byte[] iv) {
        mIV = iv;
    }

    public synchronized byte[] getIV() {
        return mIV;
    }

    public synchronized void setLevel(int level) {
        mSessionLevel = level;
    }

    public synchronized int getLevel() {
        return mSessionLevel;
    }
}
