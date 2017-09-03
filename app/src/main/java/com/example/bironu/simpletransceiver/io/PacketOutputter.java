package com.example.bironu.simpletransceiver.io;

import com.example.bironu.simpletransceiver.common.CommonUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PacketOutputter
implements DataOutputter
	, AutoCloseable
{
	public static final String TAG = PacketOutputter.class.getSimpleName();
	
	private final DatagramSocket mSocket;
	private final DatagramPacket mPacket;

	public PacketOutputter(int srcPort, InetAddress srcAddress, int dstPort, InetAddress dstAddress) throws SocketException {
		mSocket = new DatagramSocket(srcPort, srcAddress);
		mPacket = new DatagramPacket(new byte[0], 0, dstAddress, dstPort);
	}
	
	@Override
	public void output(byte[] buf, int length) throws IOException {
		mPacket.setData(buf, 0, length);
		mSocket.send(mPacket);
		CommonUtils.logd(TAG, "packet send " + length + " bytes");
	}

	@Override
	public void close() {
		mSocket.disconnect();
		mSocket.close();
	}
}
