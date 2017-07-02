package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.main.PacketOutputter;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class RtpPacketOutputter
extends PacketOutputter
{
	public static final String TAG = RtpPacketOutputter.class.getSimpleName();
	
	private final RtpPacket mRtpPacket;
	private final RtpSession mRtpSession;
	
	public RtpPacketOutputter(int port, InetAddress address, RtpSession rtpSession) throws SocketException {
		super(port, address);
		mRtpPacket = new RtpPacket(null);
		mRtpSession = rtpSession;
	}

	@Override
	public void output(byte[] buf, int length) throws IOException {
		mRtpPacket.setBuffer(buf, length);
		mRtpPacket.setVersion(2);
		mRtpPacket.setPayloadType(mRtpSession.getPayloadType());
		mRtpPacket.setSsrc(mRtpSession.getSsrc());
		mRtpPacket.setTimestamp(mRtpSession.getNextTimeStamp());
		mRtpPacket.setSequenceNumber(mRtpSession.getNextSeqNum());
		super.output(buf, length);
	}
}
