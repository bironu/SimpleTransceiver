package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.CommonUtils;
import com.example.bironu.simpletransceiver.main.PacketInputter;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class RtpPacketInputter
extends PacketInputter
{
	public static final String TAG = RtpPacketInputter.class.getSimpleName();
	
	private static final int PACKET_SIZE = 360;
	private static final int MAX_DROPOUT = 3000;
	private static final int MAX_MISORDER = 100;

	private static final int MAX_TIMEOUT_COUNT = 10;

	private final RtpPacket mRtpPacket;
	private final RtpSession mRtpSession;
	private int mTimeoutCount = 0;
	private int mBadSequenceNumber = -1;
	private int mWrapAroundCount = 0;
	private long mLastReceiveTime = 0;
	private int mTimeoutLimit = 0;
	
	private byte[] mLastPacketBuffer = new byte[PACKET_SIZE];
	private int mLastPacketLength;
	
	private static class IpAddressFilter implements PacketInputter.Filter {
		private final InetAddress mInetAddress;
		
		public IpAddressFilter(InetAddress address) {
			mInetAddress = address;
		}

		@Override
		public boolean filtering(DatagramPacket packet) {
			// trueならフィルタに引っかかる=パケット破棄
			// ここでは指定のIPアドレス:ポート番号以外から来たパケットがフィルタリングされる
			return !mInetAddress.equals(packet.getAddress());
		}
	}
	
	public RtpPacketInputter(int port, InetAddress addr, RtpSession rtpSession, InetAddress remoteAddress) throws SocketException {
		super(port, addr, PACKET_SIZE);
		super.addFilter(new IpAddressFilter(remoteAddress));
		mRtpPacket = new RtpPacket(this.getBuffer());
		mRtpSession = rtpSession;
		initParam();
	}

	@Override
	public int input() throws IOException {
		int result = super.input();
		CommonUtils.logd(TAG, "packet receive "+result+" byte");
		// セッション確立していなければ捨てる
		if(!mRtpSession.isSession()) {
			CommonUtils.logd(TAG, "invalid session. session level = "+mRtpSession.getSessionLevel());
			initParam();
			this.close();
			return 0;
		}
		
		// タイムアウトしていたら
		if(result < 0) {
			CommonUtils.logd(TAG, "packet timeout.");
			this.setTimeout(mTimeoutLimit);
			if(++mTimeoutCount > MAX_TIMEOUT_COUNT) {
				CommonUtils.logd(TAG, "timeout count over. thread stop. ");
				initParam();
				this.close();
				mRtpSession.stopReceiveSession();
				// TODO どうにかしてここで画面にセッション終了を知らせてあげないとならない
				return 0;
			}
			else {
				result = mLastPacketLength;
				System.arraycopy(mLastPacketBuffer, 0, this.getBuffer(), 0, result);
				mTimeoutLimit += 20;
				CommonUtils.logd(TAG, "timeout limit = "+mTimeoutLimit);
			}
		}
		else if(result == 0) {
			return 0;
		}
		else {
			// RTPパケットじゃなければ捨てる
			if(mRtpPacket.getVersion() != 2) {
				CommonUtils.logd(TAG, "invalid rtp version.");
				return 0;
			}
			// 知らないSSRCだったら捨てる
			final long ssrc = mRtpPacket.getSsrc();
			if(ssrc != mRtpSession.getSsrc()){
				CommonUtils.logd(TAG, "invalid ssrc.");
				return 0;
			}
			// 知らないペイロードタイプだったら捨てる
			final int payloadType = mRtpPacket.getPayloadType();
			if(payloadType != mRtpSession.getPayloadType()) {
				CommonUtils.logd(TAG, "invalid payload.");
				return 0;
			}
			
			final long timeStamp = mRtpPacket.getTimestamp();
			mRtpSession.setTimeStamp(timeStamp);
			mTimeoutCount = 0;

			final int sequenceNumber = mRtpPacket.getSequenceNumber();
			final int udelta = (sequenceNumber - mRtpSession.getSeqNum()) & 0xffff;
			if(udelta < MAX_DROPOUT) {
				// シーケンス番号がオーバーフローして一周した
				if(sequenceNumber < mRtpSession.getSeqNum()) {
					++mWrapAroundCount;
				}
				mRtpSession.setSeqNum(sequenceNumber);
			}
			else if(udelta <= (65535 - MAX_MISORDER)) {
				// シーケンス番号が大幅に飛んだ
				if(sequenceNumber == mBadSequenceNumber) {
					// 連続した番号の二つのパケットを受信
					// ネットワークの向こう側の送信者が通知せずに再開した
					mRtpSession.setSeqNum(sequenceNumber);
				}
				else {
					mBadSequenceNumber = sequenceNumber + 1;
				}
			}
			else {
				// パケットが重複、あるいは順番が正しくない
				if(sequenceNumber <= mRtpSession.getSeqNum()) {
					return 0;
				}
			}
			
			final long now = System.currentTimeMillis();
			final long sub = now - mLastReceiveTime;
			CommonUtils.logd(TAG, "now - mLastReceiveTime = "+sub);
			if(mLastReceiveTime > 0) {
				mTimeoutLimit -= (sub - 20);
				if(mTimeoutLimit <= 0) {
					mTimeoutLimit = 1;
				}
			}
			else {
				mTimeoutLimit = 100;
			}
			CommonUtils.logd(TAG, "timeout limit = "+mTimeoutLimit);
			this.setTimeout(mTimeoutLimit);
			mLastReceiveTime = now;
			
			System.arraycopy(this.getBuffer(), 0, mLastPacketBuffer, 0, result);
		}
		return result;
	}
	
	private void initParam() {
		mTimeoutCount = 0;
		mLastReceiveTime = 0;
		mBadSequenceNumber = -1;
		mWrapAroundCount = 0;
		this.setTimeout(0);
	}
}
