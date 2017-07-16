package com.example.bironu.simpletransceiver.service;

class CtrlPacketStart extends CtrlPacket {
	public static final int PACKET_LENGTH = 64;

	public CtrlPacketStart(	RtpSession session) {
		super(PACKET_LENGTH);
		this.setControlType(CtrlPacket.CTRL_TYPE_START);
		this.setPayloadType(session.getPayloadType());
		this.setSequenceNumber(session.getSeqNum());
		this.setTimestamp(session.getTimeStamp());
		this.setSsrc(session.getSsrc());
		this.setFrameSize(session.getFrameSize());
		this.setSessionLevel(session.getLevel());
		this.setKey(session.getKey());
		this.setIV(session.getIV());
	}

	public CtrlPacketStart(byte[] buffer, int length) {
		super(buffer, length);
	}
	
	public int getFrameSize() {
		return getInt(this.getBuffer(), 12, 14);
	}

	public void setFrameSize(int size) {
		setInt(size, this.getBuffer(), 12, 14);
	}
	
	public int getSessionLevel() {
		return getInt(this.getBuffer(), 14, 16);
	}

	public void setSessionLevel(int level) {
		setInt(level, this.getBuffer(), 14, 16);
	}
	
	public void setKey(byte[] key) {
		System.arraycopy(key, 0, this.getBuffer(), 16, 32);
	}
	
	public byte[] getKey() {
		byte[] result = new byte[32];
		System.arraycopy(this.getBuffer(), 16, result, 0, result.length);
		return result;
	}
	
	public void setIV(byte[] initVector) {
		System.arraycopy(initVector, 0, this.getBuffer(), 48, 16);
	}
	
	public byte[] getIV() {
		byte[] result = new byte[16];
		System.arraycopy(this.getBuffer(), 48, result, 0, result.length);
		return result;
	}

}
