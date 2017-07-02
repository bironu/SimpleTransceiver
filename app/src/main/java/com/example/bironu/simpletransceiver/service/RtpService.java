package com.example.bironu.simpletransceiver.service;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.CommonUtils;
import com.example.bironu.simpletransceiver.DataInputter;
import com.example.bironu.simpletransceiver.DataOutputter;
import com.example.bironu.simpletransceiver.DataRelayer;
import com.example.bironu.simpletransceiver.Job;
import com.example.bironu.simpletransceiver.JobWorker;
import com.example.bironu.simpletransceiver.Preferences;
import com.example.bironu.simpletransceiver.Worker;
import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.codecs.ulaw;
import com.example.bironu.simpletransceiver.main.PacketOutputter;

public class RtpService extends Service
{
	public static final String TAG = RtpService.class.getSimpleName();
	public static final String ACTION_BEGIN_RTP_RECEIVE = CommonSettings.BASE_PACKAGE+".action.BEGIN_RTP_RECEIVE";
	public static final String ACTION_END_RTP_RECEIVE = CommonSettings.BASE_PACKAGE+".action.END_RTP_RECEIVE";
	
	private final ExecutorService mExec = Executors.newCachedThreadPool();
	private final List<Worker> mWorkerList = new ArrayList<Worker>();
	private Codec mCodec;
	private InetAddress mLocalInetAddress;
	private DataRelayer mMic2Packet;
	private DataRelayer mPacket2Speaker;
	private Worker mCtrlPacketReceiver;
	private final RtpSession mRtpSession = new RtpSession();
	
	private static class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE.equals(action)) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive android.net.conn.CONNECTIVITY_CHANGE !!!");
				if(context instanceof RtpService) {
					((RtpService) context).setLocalIpAddress();
				}
			}
			else {
			}
		}
	}
	private MyBroadcastReceiver mBroadcastReceiver;
	
	public class RtpServiceBinder extends Binder {
		public void beginRtpReceiver(CtrlPacketStart start, InetAddress remoteAddress) {
			RtpService.this.beginRtpReceiver(start, remoteAddress);
		}

		public void endRtpReceiver(CtrlPacketStop stop) {
			RtpService.this.endRtpReceiver(stop);
		}

		public void endRtpReceiver() {
			RtpService.this.endRtpReceiver();
		}

		public boolean beginRtpSender() {
			return RtpService.this.beginRtpSender();
		}
		
		public void endRtpSender() {
			RtpService.this.endRtpSender();
		}

		public void beginCtrlReceiver() {
			RtpService.this.beginCtrlReceiver();
		}
		
		public void endCtrlReceiver() {
			RtpService.this.endCtrlReceiver();
		}
		
		public void addSendTarget(String address, int ctrlPort, int rtpPort, int imagePort) {
			mRtpSession.addSendTarget(address, ctrlPort, rtpPort, imagePort);
		}
		
		public void removeSendTarget(String address, int ctrlPort, int rtpPort, int imagePort) {
			mRtpSession.removeSendTarget(address, ctrlPort, rtpPort, imagePort);
		}
		
		public void removeSendTarget(int location) {
			mRtpSession.removeSendTarget(location);
		}
		
		public void clearSendTarget() {
			mRtpSession.clearSendTarget();
		}
		
		public List<PacketOutputter.SendTarget> getCtrlSendTargetList() {
			return mRtpSession.getCtrlSendTargetList();
		}
		
		public List<PacketOutputter.SendTarget> getRtpSendTargetList() {
			return mRtpSession.getRtpSendTargetList();
		}
		
		public List<PacketOutputter.SendTarget> getImageSendTargetList() {
			return mRtpSession.getImageSendTargetList();
		}
	
		public List<String> getAddressList() {
			return mRtpSession.getAddressList();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new RtpServiceBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mCodec = new ulaw();
		mCodec.open();
		mBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE);
		this.registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setLocalIpAddress();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		endRtpReceiver();
		endRtpSender();
		endCtrlReceiver();
		for(Worker worker : mWorkerList) {
			worker.halt();
		}
		mExec.shutdown();
		try {
			if(!mExec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
				mExec.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			mExec.shutdownNow();
		}
		if(mCodec != null) {
			mCodec.close();
			mCodec = null;
		}
		this.unregisterReceiver(mBroadcastReceiver);
		mBroadcastReceiver = null;
	}
	
	synchronized boolean beginRtpReceiver(CtrlPacketStart start, InetAddress remoteAddress) {
		boolean result = false;
		try {
			if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "beginRtpReceiver session level = "+mRtpSession.getLevel()+", pakcet session level = "+start.getSessionLevel());
			if(mRtpSession.beginSession(start.getSessionLevel())) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "beginRtpReceiver OK!!!!!!!");
				// 送信or受信を判定して強制終了　セッション無しなら何もせず
				endRtpReceiver();
				endRtpSender();
				this.sendBroadcast(new Intent(ACTION_BEGIN_RTP_RECEIVE));
				sendCtrlPacket(start);
				mRtpSession.setSessionParam(start);
				Preferences prefs = new Preferences(this.getApplicationContext());
				final int rtpPort = prefs.getRtpPort();
	
				RtpPacketInputter packetIn = new RtpPacketInputter(rtpPort, mLocalInetAddress, mRtpSession, remoteAddress);
				DataOutputter speakerOut = new DecodingSpeakerOutputter(mCodec, mRtpSession);
	
				mPacket2Speaker = new DataRelayer(packetIn);
				List<PacketOutputter.SendTarget> targetList = mRtpSession.getRtpSendTargetList();
				if(targetList != null && targetList.size() > 0) {
					PacketOutputter packetOut = new PacketOutputter(0, mLocalInetAddress);
					for(PacketOutputter.SendTarget target : targetList) {
						packetOut.addSendTarget(target.address, target.port);
					}
					mPacket2Speaker.addDataOutputter(packetOut);
				}
				mPacket2Speaker.addDataOutputter(speakerOut);
				mWorkerList.add(mPacket2Speaker);
				mExec.execute(mPacket2Speaker);
				result = true;
			}
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	synchronized void endRtpReceiver(CtrlPacketStop stop) {
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "endRtpReceiver session ssrc = "+mRtpSession.getSsrc()+", packet ssrc = "+stop.getSsrc());
		if(mRtpSession.getSsrc() == stop.getSsrc()) {
			endRtpReceiver();
			sendCtrlPacket(stop);
		}
	}

	synchronized void endRtpReceiver() {
		if(mPacket2Speaker != null) {
			this.sendBroadcast(new Intent(ACTION_END_RTP_RECEIVE));
			mPacket2Speaker.halt();
			mWorkerList.remove(mPacket2Speaker);
			mPacket2Speaker = null;
			mRtpSession.stopReceiveSession();
		}
	}

	synchronized boolean beginRtpSender() {
		boolean result = false;
		try{
			Preferences prefs = new Preferences(this.getApplicationContext());
			final int accountLevel = prefs.getAccountLevel();
			if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "beginRtpSender session level = "+mRtpSession.getLevel()+", account level = "+accountLevel);
			if(mRtpSession.beginSession(accountLevel)) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "beginRtpSender OK!!!!!!!");
				// 送信or受信を判定して強制終了　セッション無しなら何もせず
				endRtpReceiver();
				endRtpSender();
				mRtpSession.setSessionParam(accountLevel, mCodec);

				// こいつを先に作るとAES暗号鍵が作られる
				DataInputter micIn = new EncodingMicInputter(mCodec, prefs.getFilterThreshold(), mRtpSession);
				PacketOutputter packetOut = new RtpPacketOutputter(0, mLocalInetAddress, mRtpSession);

				List<PacketOutputter.SendTarget> rtpTargetList = mRtpSession.getRtpSendTargetList();
				for(PacketOutputter.SendTarget target : rtpTargetList) {
					packetOut.addSendTarget(target.address, target.port);
				}
				
				CtrlPacketStart packet = new CtrlPacketStart(mRtpSession);
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "sendCtrlPacketStart");
				sendCtrlPacket(packet);

				mMic2Packet = new DataRelayer(micIn);
				mMic2Packet.addDataOutputter(packetOut);
				mWorkerList.add(mMic2Packet);
				mExec.execute(mMic2Packet);
				result = true;
			}
			else {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "beginRtpSender NG!!!!!!!");
			}
		}
		catch(SocketException e) {
			e.printStackTrace();
		}
//		catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		return result;
	}
	
	synchronized void endRtpSender() {
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "endRtpSender call");
		if(mMic2Packet != null) {
			mMic2Packet.halt();
			mWorkerList.remove(mMic2Packet);
			mMic2Packet = null;
			CtrlPacketStop packet = new CtrlPacketStop(mRtpSession);
			if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "sendCtrlPacketStop");
			sendCtrlPacket(packet);
			mRtpSession.stopSendSession();
		}
	}

	synchronized void beginCtrlReceiver() {
		Preferences prefs = new Preferences(this.getApplicationContext());
		final int ctrlPort = prefs.getCtrlPort();

		try {
			Job job = new CtrlPacketReceiveJob(ctrlPort, mLocalInetAddress, new RtpServiceBinder());
			mCtrlPacketReceiver = new JobWorker(job);
			mWorkerList.add(mCtrlPacketReceiver);
			mExec.execute(mCtrlPacketReceiver);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	synchronized void endCtrlReceiver() {
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "endCtrlReceiver() call.");
		if(mCtrlPacketReceiver != null) {
			mCtrlPacketReceiver.halt();
			if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "mCtrlPacketReceiver.halt() call");
			mWorkerList.remove(mCtrlPacketReceiver);
			mCtrlPacketReceiver = null;
		}
	}
	
	public synchronized void sendCtrlPacket(CtrlPacket packet) {
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "sendCtrlPacket call");
		try {
			CtrlPacketSendJob job = new CtrlPacketSendJob(mLocalInetAddress, packet);
			job.addSendTarget(mRtpSession.getCtrlSendTargetList());
			JobWorker sender = new JobWorker(job);
			mWorkerList.add(sender);
			mExec.execute(sender);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	synchronized void setLocalIpAddress() {
		InetAddress localInetAddress = CommonUtils.getIPAddress();
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "setLocalIpAddress old address = "+mLocalInetAddress+", new address"+localInetAddress);
		if(localInetAddress != null) {
			if(!localInetAddress.equals(mLocalInetAddress)) {
				mLocalInetAddress = localInetAddress;
				endRtpSender();
				endRtpReceiver();
				endCtrlReceiver();
				beginCtrlReceiver();
			}
		}
		else {
			endRtpSender();
			endRtpReceiver();
			endCtrlReceiver();
		}
	}

}