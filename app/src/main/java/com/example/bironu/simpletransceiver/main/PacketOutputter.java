package com.example.bironu.simpletransceiver.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.DataOutputter;

public class PacketOutputter
implements DataOutputter
{
	public static final String TAG = PacketOutputter.class.getSimpleName();
	
	private final DatagramSocket mSocket;
	private final DatagramPacket mPacket = new DatagramPacket(new byte[0], 0);
	private final Object mLock = new Object();
	
	public static class SendTarget {
		public InetAddress address;
		public int port;
		public SendTarget(InetAddress address, int port) {
			this.address = address;
			this.port = port;
		}
	}
	private ArrayList<SendTarget> mSendTargetList = new ArrayList<SendTarget>();

	public void addSendTarget(InetAddress address, int port) {
		addSendTarget(new SendTarget(address, port));
	}

	public void addSendTarget(SendTarget target) {
		synchronized(mLock) {
			mSendTargetList.add(target);
		}
	}

	public void addSendTarget(List<SendTarget> targetList) {
		synchronized(mLock) {
			mSendTargetList.addAll(targetList);
		}
	}
	
	public void clearSendTarget() {
		synchronized(mLock) {
			mSendTargetList.clear();
		}
	}
	
	public PacketOutputter(int port, InetAddress address) throws SocketException {
		mSocket = new DatagramSocket(port, address);
	}
	
	public PacketOutputter(DatagramSocket socket) {
		mSocket = socket;
	}

	@Override
	public void output(byte[] buf, int length) throws IOException {
		synchronized(mLock) {
			for(SendTarget target : mSendTargetList) {
				mPacket.setData(buf, 0, length);
				mPacket.setAddress(target.address);
				mPacket.setPort(target.port);
				mSocket.send(mPacket);
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "packet send target = " + target.address + ":" + target.port);
			}
		}
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "packet send " + length + " bytes");
	}

	@Override
	public void close() {
		mSocket.disconnect();
		mSocket.close();
	}

}
