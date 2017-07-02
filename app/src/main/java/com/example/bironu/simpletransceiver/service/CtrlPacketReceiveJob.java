package com.example.bironu.simpletransceiver.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.Job;
import com.example.bironu.simpletransceiver.main.PacketInputter;

public class CtrlPacketReceiveJob
implements Job
{
	public static final String TAG = CtrlPacketReceiveJob.class.getSimpleName();
	
	private final PacketInputter mPacketInputter;
	private final RtpService.RtpServiceBinder mBinder;
	private final CtrlPacket mCtrlPacket = new CtrlPacket(null, 0);

	public CtrlPacketReceiveJob(int port, InetAddress addr, RtpService.RtpServiceBinder binder) throws SocketException {
		mPacketInputter = new PacketInputter(port, addr, CtrlPacketStart.PACKET_LENGTH);
		mBinder = binder;
	}

	@Override
	public boolean action() throws InterruptedException {
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "CtrlPacketReceiveJob#action() call.");
		boolean result = true;
		try {
			final int length = mPacketInputter.input();
			final DatagramPacket packet = mPacketInputter.getPacket();
			
			mCtrlPacket.setBuffer(mPacketInputter.getBuffer(), length);
			if(mCtrlPacket.getVersion() == 2) {
				switch(mCtrlPacket.getControlType()) {
				case CtrlPacket.CTRL_TYPE_START:{
					if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive CtrlPacket.CTRL_TYPE_START");
					CtrlPacketStart start = new CtrlPacketStart(mPacketInputter.getBuffer(), length);
					mBinder.beginRtpReceiver(start, packet.getAddress());
					break;
				}
				case CtrlPacket.CTRL_TYPE_STOP:{
					if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive CtrlPacket.CTRL_TYPE_STOP");
					CtrlPacketStop stop = new CtrlPacketStop(mPacketInputter.getBuffer(), length);
					mBinder.endRtpReceiver(stop);
					break;
				}
				default:
					if(CommonSettings.DEBUG_LEVEL >= Log.WARN) Log.w(TAG, "Invalid type = "+mCtrlPacket.getControlType());
					break;
				}
			}
			else {
				if(CommonSettings.DEBUG_LEVEL >= Log.WARN) Log.w(TAG, "Invalid version = "+mCtrlPacket.getVersion());
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
