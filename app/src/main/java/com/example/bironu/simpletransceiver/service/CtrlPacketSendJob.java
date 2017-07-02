package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.Job;
import com.example.bironu.simpletransceiver.main.PacketOutputter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class CtrlPacketSendJob
implements Job
{
	private static final int REPEAT_COUNT = 3;
	
	private final PacketOutputter mPacketOutputter;
	private final CtrlPacket mPacket;
	private int mCounter = 0;

	public CtrlPacketSendJob(InetAddress address, CtrlPacket packet) throws SocketException {
		mPacketOutputter = new PacketOutputter(0, address);
		mPacket = packet;
	}

	public void addSendTarget(InetAddress address, int port) {
		mPacketOutputter.addSendTarget(address, port);
	}

	public void addSendTarget(List<PacketOutputter.SendTarget> targetList) {
		mPacketOutputter.addSendTarget(targetList);
	}
	
	@Override
	public boolean action() throws InterruptedException {
		boolean result = false;
		try {
			mPacketOutputter.output(mPacket.getBuffer(), mPacket.getLength());
			result = ++mCounter >= REPEAT_COUNT;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void close() {
		mPacketOutputter.close();
	}
}
