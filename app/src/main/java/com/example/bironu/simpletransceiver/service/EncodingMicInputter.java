package com.example.bironu.simpletransceiver.service;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.common.CommonSettings;
import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.common.DataInputter;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;

import java.io.IOException;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public class EncodingMicInputter
implements DataInputter
{
	public static final String TAG = EncodingMicInputter.class.getSimpleName();
	
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private final int MIN_BUF_SIZE;

	private Cipher mCipher;

	private final int mThreshold;
	private final Codec mCodec;
	private final byte[] mBuffer;
	private final short[] mReadBuffer;
	private final AudioRecord mAudioRecord;
	private final RtpSession mRtpSession;
	private AcousticEchoCanceler mAcousticEchoCanceler;
	private NoiseSuppressor mNoiseSuppressor;


	public EncodingMicInputter(Codec codec, int threshold, RtpSession rtpSession) throws SocketException {
		MIN_BUF_SIZE = AudioRecord.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
		mThreshold = threshold;
		mCodec = codec;
		mBuffer = new byte[codec.frame_size() + RtpPacket.MAX_HEADER_LENGTH];
		mReadBuffer = new short[codec.frame_size()];
		mAudioRecord = new AudioRecord(AudioSource.DEFAULT, codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, MIN_BUF_SIZE);
		CommonUtils.logd(TAG, "AudioRecord.getState() = " + mAudioRecord.getState());
		aec();
		mAudioRecord.startRecording();
		mRtpSession = rtpSession;
		initEncryptCipher();
	}

	@Override
	public int input() throws IOException {
		final int length = mAudioRecord.read(mReadBuffer, 0, mReadBuffer.length);
		CommonUtils.logd(TAG, "mic read " + length*2 + " byte");
		if(mThreshold > 0) {
			for(int i = 0; i < length; ++i) {
				// この辺に独自のフィルタとか組み込めるかも
			}
		}
		else {
//			if(CommonSettings.DEBUG_LEVEL >= Log.INFO) {
//				sb.setLength(0);
//				for(int i = 0; i < length; ++i) {
//					sb.append(mReadBuffer[i]).append(',');
//				}
//			}
		}
		
		final int encResult = mCodec.encode(mReadBuffer, 0, mBuffer, length);
		int result = 0;
		CommonUtils.logd(TAG, "encode result " + length*2 + " byte -> "+encResult+" byte");
//		if(CommonSettings.DEBUG_LEVEL >= Log.INFO) Log.d(TAG, sb.toString());
		try {
			if(mCipher != null){
				int cryptoLength = mCipher.doFinal(mBuffer, RtpPacket.HEADER_LENGTH, encResult, mBuffer, RtpPacket.HEADER_LENGTH);
				CommonUtils.logd(TAG, "crypto length = " + cryptoLength + " byte");
				result = cryptoLength + RtpPacket.HEADER_LENGTH;
			}
		}
		catch (ShortBufferException e) {
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public byte[] getBuffer() {
		return mBuffer;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void close() {
		//mAudioRecord.stop();
		mAudioRecord.release();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if(mAcousticEchoCanceler != null) {
				mAcousticEchoCanceler.release();
				mAcousticEchoCanceler = null;
			}
			if(mNoiseSuppressor != null) {
				mNoiseSuppressor.release();
				mNoiseSuppressor = null;
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void aec() {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (AcousticEchoCanceler.isAvailable()) {
				CommonUtils.logd(TAG, "AcousticEchoCanceler isAvailable");
				mAcousticEchoCanceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
				if (mAcousticEchoCanceler != null && !mAcousticEchoCanceler.getEnabled()) {
					mAcousticEchoCanceler.setEnabled(true);
					CommonUtils.logd(TAG, "AcousticEchoCanceler enabled = "+mAcousticEchoCanceler.getEnabled());
				}
			}
			if (NoiseSuppressor.isAvailable()) {
				CommonUtils.logd(TAG, "NoiseSuppressor isAvailable");
				mNoiseSuppressor = NoiseSuppressor.create(mAudioRecord.getAudioSessionId());
				if (mNoiseSuppressor != null && !mNoiseSuppressor.getEnabled()) {
					mNoiseSuppressor.setEnabled(true);
					CommonUtils.logd(TAG, "NoiseSuppressor enabled = "+mNoiseSuppressor.getEnabled());
				}
			}
		}
	}

	public void initEncryptCipher() {
		Key key = genKey();
		try {
			if(key != null) {
				mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				mCipher.init(Cipher.ENCRYPT_MODE, key);
				mRtpSession.setKey(key.getEncoded());
				mRtpSession.setIV(mCipher.getIV());
				
//				StringBuilder sb = new StringBuilder(1024);
//
//				byte[] bkey = mRtpSession.getKey();
//				Log.d(TAG, "encoded key length = "+bkey.length);
//				for(int i = 0; i < bkey.length; i += 4) {
//					for(int j = i; j < 4; ++j) {
//						sb.append(Integer.toHexString(bkey[j] & 0xff)).append(' ');
//					}
//					sb.append('\n');
//				}
//				Log.d(TAG, sb.toString());
//				
//				sb.setLength(0);
//				byte[] biv = mRtpSession.getIV();
//				Log.d(TAG, "encoded IV length = "+biv.length);
//				for(int i = 0; i < biv.length; i += 4) {
//					for(int j = i; j < 4; ++j) {
//						sb.append(Integer.toHexString(biv[j] & 0xff)).append(' ');
//					}
//					sb.append('\n');
//				}
//				Log.d(TAG, sb.toString());
			}
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
			mRtpSession.setKey(null);
			mRtpSession.setIV(null);
			mCipher = null;
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			mRtpSession.setKey(null);
			mRtpSession.setIV(null);
			mCipher = null;
		}
		catch (NoSuchPaddingException e) {
			e.printStackTrace();
			mRtpSession.setKey(null);
			mRtpSession.setIV(null);
			mCipher = null;
		}
	}
	
	// 暗号化アルゴリズムに応じた鍵を生成する
	public static Key genKey() {
		Key key = null;
		try {
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			// 乱数の発生源を作成します 指定できるのはSHA1PRNGのみ
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// 第一引数にキー長のbit数を指定します
			generator.init(256, random);
			key = generator.generateKey();
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}

}
