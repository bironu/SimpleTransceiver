package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.data.Entity;
import com.example.bironu.simpletransceiver.io.Job;
import com.example.bironu.simpletransceiver.io.PacketOutputter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class CtrlPacketSendJob
implements Job
{
	private static final int REPEAT_COUNT = 3;
	
	private final List<PacketOutputter> mPacketOutputterList;
	private final CtrlPacket mPacket;
	private int mCounter = 0;

	public CtrlPacketSendJob(int srcPort, InetAddress srcAddress, List<Entity.SendTarget> targetList, CtrlPacket packet) throws SocketException {
		mPacketOutputterList = new ArrayList<>(targetList.size());
		for (Entity.SendTarget target : targetList) {
            try {
                mPacketOutputterList.add(new PacketOutputter(srcPort, srcAddress, target.getCtrlPort(), InetAddress.getByName(target.getIpAddressString())));
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
		mPacket = packet;
	}

	@Override
	public boolean action() throws InterruptedException {
		boolean result = false;
		try {
            for (PacketOutputter outputter : mPacketOutputterList) {
                outputter.output(mPacket.getBuffer(), mPacket.getLength());
            }
			result = ++mCounter >= REPEAT_COUNT;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void close() {
        for (PacketOutputter outputter : mPacketOutputterList) {
            outputter.close();
        }
    }
}
