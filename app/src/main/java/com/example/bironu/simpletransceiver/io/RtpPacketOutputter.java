package com.example.bironu.simpletransceiver.io;

import com.example.bironu.simpletransceiver.common.CommonSettings;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;
import com.example.bironu.simpletransceiver.service.RtpSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class RtpPacketOutputter
extends PacketOutputter
{
	public static final String TAG = RtpPacketOutputter.class.getSimpleName();
	
	private final RtpPacket mRtpPacket;
	private final RtpSession mRtpSession;
	
	public RtpPacketOutputter(int srcPort, InetAddress srcAddress, int dstPort, InetAddress dstAddress, RtpSession rtpSession) throws SocketException {
		super(srcPort, srcAddress, dstPort, dstAddress);
		mRtpPacket = new RtpPacket(new byte[CommonSettings.DATAGRAM_PACKET_SIZE]);
		mRtpSession = rtpSession;
	}

	@Override
	public void output(byte[] buf, int length) throws IOException {
		mRtpPacket.setVersion(2);
		mRtpPacket.setPayloadType(mRtpSession.getPayloadType());
		mRtpPacket.setSsrc(mRtpSession.getSsrc());
		mRtpPacket.setTimestamp(mRtpSession.getNextTimeStamp());
		mRtpPacket.setSequenceNumber(mRtpSession.getNextSeqNum());
		mRtpPacket.setPayload(buf, length);
		super.output(mRtpPacket.getPacket(), mRtpPacket.getPacketLength());
	}
}
