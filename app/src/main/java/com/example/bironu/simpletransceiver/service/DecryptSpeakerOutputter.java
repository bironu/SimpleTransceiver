package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.rtp.RtpPacket;

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

public class DecryptSpeakerOutputter
extends SpeakerOutputter
{
	public static final String TAG = DecryptSpeakerOutputter.class.getSimpleName();
	
	private Cipher mCipher;
	private final RtpSession mRtpSession;
    private final RtpPacket mRtpPacket;

	public DecryptSpeakerOutputter(Codec codec, RtpSession rtpSession) {
		super(codec);
		mRtpSession = rtpSession;
        mRtpPacket = new RtpPacket(null);
		initDecryptCipher();
	}

	StringBuilder sb = new StringBuilder(6 * 160);
	@Override
	public void output(byte[] buf, int length) throws IOException {
		if(mCipher == null) {
			return;
		}
		try {
			// bufはRTPパケットそのまんま
            mRtpPacket.setBuffer(buf, length);
			mCipher.doFinal(mRtpPacket.getPacket(), mRtpPacket.getHeaderLength(), mRtpPacket.getPayloadLength(), mRtpPacket.getPacket(), mRtpPacket.getHeaderLength());
			super.output(buf, length);
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
