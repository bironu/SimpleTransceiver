package com.example.bironu.simpletransceiver.main;

import java.io.IOException;
import java.net.SocketException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.DataInputter;
import com.example.bironu.simpletransceiver.codecs.Codec;

public class MicInputter
implements DataInputter
{
	public static final String TAG = MicInputter.class.getSimpleName();
	
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private final int MIN_BUF_SIZE;

	private final byte[] mBuffer;
	private final AudioRecord mAudioRecord;
	
	public MicInputter(Codec codec) throws SocketException {
		MIN_BUF_SIZE = AudioRecord.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
		mBuffer = new byte[codec.frame_size()*2];
		mAudioRecord = new AudioRecord(AudioSource.DEFAULT, codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, MIN_BUF_SIZE);
		aec(mAudioRecord);
		mAudioRecord.startRecording();
	}

	short a1, a2, a3, a4, a5, a6, a7;
	@Override
	public int input() throws IOException {
		final int length = mAudioRecord.read(mBuffer, 0, mBuffer.length);
		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "mic read " + length + " byte");
		for(int i = 0; i < length; i+=2) {
			final short tmp = (short)((int)(mBuffer[i] & 0xff) + (((int)(mBuffer[i+1] & 0xff) << 8)));
			short avg = (short) ((a1 + a2 + a3 + a4 + a5 + a6 + a7 + Math.abs(tmp)) / 8);
			a1 = a2;
			a2 = a3;
			a3 = a4;
			a4 = a5;
			a5 = a6;
			a6 = a7;
			a7 = avg;
			if(avg < 512) {
				avg = 0;
			}
			if(tmp < 0) {
				avg = (short) -avg;
			}
			mBuffer[i] = (byte)(avg & 0xff);
			mBuffer[i+1] = (byte)((avg & 0xff00) >> 8);
		}
		return length;
	}
	
	@Override
	public byte[] getBuffer() {
		return mBuffer;
	}
	
	@Override
	public void close() {
		//mAudioRecord.stop();
		mAudioRecord.release();
	}
	
	private static void aec(AudioRecord ar) {
		if (AcousticEchoCanceler.isAvailable()) {
	        AcousticEchoCanceler aec = AcousticEchoCanceler.create(ar.getAudioSessionId());
	        if (aec != null && !aec.getEnabled()) {
	        	aec.setEnabled(true);
	        }
		}
		if (NoiseSuppressor.isAvailable()) {
	        NoiseSuppressor noise = NoiseSuppressor.create(ar.getAudioSessionId());
	        if (noise != null && !noise.getEnabled()) {
	        	noise.setEnabled(true);
	        }
		}
	}
	
}
