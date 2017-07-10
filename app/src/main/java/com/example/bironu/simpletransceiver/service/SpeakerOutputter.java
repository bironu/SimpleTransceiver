package com.example.bironu.simpletransceiver.service;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.example.bironu.simpletransceiver.codecs.Codec;
import com.example.bironu.simpletransceiver.common.CommonSettings;
import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.common.DataOutputter;

import java.io.IOException;

public class SpeakerOutputter
implements DataOutputter
{
	public static final String TAG = SpeakerOutputter.class.getSimpleName();
	
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
	
	private final AudioTrack mAudioTrack;

	public SpeakerOutputter(Codec codec) {
		final int MIN_BUF_SIZE = AudioTrack.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, codec.samp_rate(),
				CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, MIN_BUF_SIZE * 2, AudioTrack.MODE_STREAM);
		short[] buf = new short[MIN_BUF_SIZE/2];
		CommonUtils.noise(buf, buf.length, 1024);
		mAudioTrack.write(buf, 0, buf.length);
		mAudioTrack.play();
		CommonUtils.logd(TAG, "MIN_BUF_SIZE = "+MIN_BUF_SIZE);
	}

	@Override
	public void output(byte[] buf, int length) throws IOException {
		final int writeLength = mAudioTrack.write(buf, 0, length);
		CommonUtils.logd(TAG, "speaker write "+writeLength+" byte");
	}

	@Override
	public void close() {
		mAudioTrack.stop();
	}
}
