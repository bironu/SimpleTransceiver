package com.example.bironu.simpletransceiver.io;

import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.service.RtpSession;

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

public class EncryptMicInputter
extends MicInputter
{
	public static final String TAG = EncryptMicInputter.class.getSimpleName();
	
	private Cipher mCipher;
	private final RtpSession mRtpSession;

	public EncryptMicInputter(Codec codec, RtpSession rtpSession) throws SocketException {
		super(codec);
		mRtpSession = rtpSession;
		initEncryptCipher();
	}

	@Override
	public int input() throws IOException {
		final int encResult = super.input();
		int result = 0;
		try {
			if(mCipher != null){
				byte[] buf = this.getBuffer();
				int cryptoLength = mCipher.doFinal(buf, 0, encResult, buf, 0);
				CommonUtils.logd(TAG, "crypto length = " + cryptoLength + " byte");
				result = cryptoLength;
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
	
    private void initEncryptCipher() {
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
	private static Key genKey() {
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
