package com.example.bironu.simpletransceiver.service;

class CtrlPacket
{
    public static final int CTRL_TYPE_START = 1;
    public static final int CTRL_TYPE_STOP = 2;

    private byte[] mBuffer;
    private int mLength;

    public CtrlPacket(byte[] buffer, int length) {
        setBuffer(buffer, length);
    }

    public CtrlPacket(int size) {
        setBuffer(new byte[size], size);
        setVersion(2);
    }

    public void setBuffer(byte[] buffer, int length) {
        mBuffer = buffer;
        mLength = length;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public void setLength(int length) {
        mLength = length;
    }


    public int getLength() {
        return mLength;
    }

    // version (V): 2 bits
    // Control Type : 6 bit
    // marker (M): 1 bit
    // payload type (PT): 7 bits
    // sequence number: 16 bits
    // timestamp: 32 bits
    // SSRC: 32 bits
    // Frame Size : 32bit

    public int getVersion() {
        return (mBuffer[0] >> 6 & 0x03);
    }

    public void setVersion(int v) {
        mBuffer[0] = (byte) ((mBuffer[0] & 0x3F) | ((v & 0x03) << 6));
    }

    public int getControlType() {
        return (mBuffer[0] & 0x3f);
    }

    public void setControlType(int type) {
        mBuffer[0] = (byte) ((mBuffer[0] & 0xc0) | (type & 0x3f));
    }

    public boolean isMarker() {
        return (mBuffer[1] & 0x80) != 0;
    }

    public void setMarker(boolean mark) {
        if (mark) {
            mBuffer[1] |= 0x80;
        }
        else {
            mBuffer[1] &= 0x7f;
        }
    }

    public int getPayloadType() {
        return (mBuffer[1] & 0x7F);
    }

    public void setPayloadType(int pt) {
        mBuffer[1] = (byte) ((mBuffer[1] & 0x80) | (pt & 0x7F));
    }

    public int getSequenceNumber() {
        return getInt(mBuffer, 2, 4);
    }

    public void setSequenceNumber(int sn) {
        setInt(sn, mBuffer, 2, 4);
    }

    public long getTimestamp() {
        return getLong(mBuffer, 4, 8);
    }

    public void setTimestamp(long timestamp) {
        setLong(timestamp, mBuffer, 4, 8);
    }

    public long getSsrc() {
        return getLong(mBuffer, 8, 12);
    }

    public void setSsrc(long ssrc) {
        setLong(ssrc, mBuffer, 8, 12);
    }

    // *********************** Private and Static ***********************

//	/** Gets int value */
//	private static int getInt(byte b) {
//		return ((int) b + 256) % 256;
//	}

    /**
     * Gets long value
     */
    protected static long getLong(byte[] data, int begin, int end) {
        long n = 0;
        for (; begin < end; begin++) {
            n <<= 8;
            n += data[begin] & 0xFF;
        }
        return n;
    }

    /**
     * Sets long value
     */
    protected static void setLong(long n, byte[] data, int begin, int end) {
        for (end--; end >= begin; end--) {
            data[end] = (byte) (n % 256);
            n >>= 8;
        }
    }

    /**
     * Gets Int value
     */
    protected static int getInt(byte[] data, int begin, int end) {
        return (int) getLong(data, begin, end);
    }

    /**
     * Sets Int value
     */
    protected static void setInt(int n, byte[] data, int begin, int end) {
        setLong(n, data, begin, end);
    }
}
