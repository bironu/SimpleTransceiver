package com.example.bironu.simpletransceiver.service;

public class CtrlPacketStop extends CtrlPacket {
	public static final int PACKET_LENGTH = 16;

	public CtrlPacketStop(RtpSession session) {
		super(PACKET_LENGTH);
		this.setControlType(CtrlPacket.CTRL_TYPE_STOP);
		this.setPayloadType(session.getPayloadType());
		this.setSequenceNumber(session.getSeqNum());
		this.setTimestamp(session.getTimeStamp());
		this.setSsrc(session.getSsrc());
	}
	
	public CtrlPacketStop(byte[] buffer, int length) {
		super(buffer, length);
	}

}
