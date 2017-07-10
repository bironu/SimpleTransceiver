package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.common.DataInputter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class PacketInputter
implements DataInputter
{
	public static final String TAG = PacketInputter.class.getSimpleName();
	
	private final DatagramSocket mSocket;
	private final byte[] mBuffer;
	private final DatagramPacket mPacket;
	
	public interface Filter {
		boolean filtering(DatagramPacket packet);
	}
	private List<Filter> mFilterList = new ArrayList<>();
	
	public PacketInputter(int port, InetAddress addr, int bufSize) throws SocketException {
		mSocket = new DatagramSocket(port, addr);
		mBuffer = new byte[bufSize];
		mPacket = new DatagramPacket(mBuffer, mBuffer.length);
		CommonUtils.logd(TAG, "socket LocalAddress = "+mSocket.getLocalAddress().toString());
		CommonUtils.logd(TAG, "socket LocalSocketAddress = "+mSocket.getLocalSocketAddress().toString());
		CommonUtils.logd(TAG, "socket LocalPort = "+mSocket.getLocalPort());
		CommonUtils.logd(TAG, "socket SendBufferSize = "+mSocket.getSendBufferSize());
		CommonUtils.logd(TAG, "socket ReceiveBufferSize = "+mSocket.getReceiveBufferSize());
	}
	
	public void setTimeout(int timeout) {
		try {
			mSocket.setSoTimeout(timeout);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void addFilter(Filter filter) {
		mFilterList.add(filter);
	}
	
	public void removeFilter(Filter filter) {
		mFilterList.remove(filter);
	}

	public void clearFilter() {
		mFilterList.clear();
	}
	
	@Override
	public int input() throws IOException {
		int result = 0;
		try{
			mPacket.setLength(mBuffer.length);
			mSocket.receive(mPacket);
			result = mPacket.getLength();
			CommonUtils.logd(TAG, "packet receive " + mPacket.getLength() + " bytes");
			CommonUtils.logd(TAG, "socket receive : addr = "+mPacket.getAddress()+ ":"+mPacket.getPort());
			for(Filter filter : mFilterList) {
				// フィルタに引っかかったら
				if(filter.filtering(mPacket)) {
					// パケット破棄して終了
					result = 0;
					break;
				}
			}
		}
		catch(SocketTimeoutException e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	@Override
	public byte[] getBuffer() {
		return mBuffer;
	}

	@Override
	public void close() {
		mSocket.close();
	}
	
	public DatagramPacket getPacket() {
		return mPacket;
	}
}
