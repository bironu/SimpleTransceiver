package com.example.bironu.simpletransceiver.service;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.CommonUtils;
import com.example.bironu.simpletransceiver.DataOutputter;
import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;

public class DecodingSpeakerOutputter
implements DataOutputter
{
	public static final String TAG = DecodingSpeakerOutputter.class.getSimpleName();
	
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;

	private Cipher mCipher;

	private final AudioTrack mAudioTrack;
	private final Codec mCodec;
	private final short[] mDecodeBuffer;
	private final RtpSession mRtpSession;

	public DecodingSpeakerOutputter(Codec codec, RtpSession rtpSession) {
		mRtpSession = rtpSession;
		final int MIN_BUF_SIZE = AudioTrack.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, codec.samp_rate(),
				CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, MIN_BUF_SIZE, AudioTrack.MODE_STREAM);
		mCodec = codec;
		initDecryptCipher();
		
		ToneGenerator tg = new ToneGenerator(android.media.AudioManager.STREAM_VOICE_CALL, ToneGenerator.MAX_VOLUME);
		tg.startTone(ToneGenerator.TONE_DTMF_0, 100);
		
		mDecodeBuffer = new short[codec.frame_size()];
		CommonUtils.noise(mDecodeBuffer, mDecodeBuffer.length, 1024);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.play();
		
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "MIN_BUF_SIZE = "+MIN_BUF_SIZE);
	}

	StringBuilder sb = new StringBuilder(6 * 160);
	@Override
	public void output(byte[] buf, int length) throws IOException {
		if(mCipher == null) {
			return;
		}
		try {
			// bufはRTPパケットそのまんま
			mCipher.doFinal(buf, RtpPacket.HEADER_LENGTH, length - RtpPacket.HEADER_LENGTH, buf, RtpPacket.HEADER_LENGTH);
			final int len = mCodec.decode(buf, mDecodeBuffer, mCodec.frame_size());
			if(CommonSettings.DEBUG_LEVEL >= Log.INFO) {
				sb.setLength(0);
				for(int i = 0; i < len; ++i) {
					sb.append(mDecodeBuffer[i]).append(',');
				}
			}
			final int writeLength = mAudioTrack.write(mDecodeBuffer, 0, len);
			if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "speaker write "+(writeLength*2)+" byte");
			if(CommonSettings.DEBUG_LEVEL >= Log.INFO) Log.d(TAG, sb.toString());
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
	}

	@Override
	public void close() {
		mAudioTrack.release();
	}

	private void initDecryptCipher() {
		SecretKeySpec sksSpec = new SecretKeySpec(mRtpSession.getKey(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(mRtpSession.getIV());
		try {
			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, sksSpec, ivSpec);
		}
		catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			mRtpSession.setKey(null);
			mRtpSession.setIV(null);
			mCipher = null;
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
//		byte[] bkey = mRtpSession.getKey();
//		Log.d(TAG, "encoded key length = "+bkey.length);
//		for(int i = 0; i < bkey.length; i += 4) {
//			for(int j = i; j < 4; ++j) {
//				sb.append(Integer.toHexString(bkey[j] & 0xff)).append(' ');
//			}
//			sb.append('\n');
//		}
//		Log.d(TAG, sb.toString());
//
//		sb.setLength(0);
//		byte[] biv = mRtpSession.getIV();
//		Log.d(TAG, "encoded IV length = "+biv.length);
//		for(int i = 0; i < biv.length; i += 4) {
//			for(int j = i; j < 4; ++j) {
//				sb.append(Integer.toHexString(biv[j] & 0xff)).append(' ');
//			}
//			sb.append('\n');
//		}
//		Log.d(TAG, sb.toString());
	}
}
