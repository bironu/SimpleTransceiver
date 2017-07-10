package com.example.bironu.simpletransceiver.service;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;

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
	private final Codec mCodec;
	private final short[] mDecodeBuffer;

	public SpeakerOutputter(Codec codec) {
		final int minBufSize = AudioTrack.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, codec.samp_rate(),
				CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, minBufSize * 2, AudioTrack.MODE_STREAM);
		mCodec = codec;

        ToneGenerator tg = new ToneGenerator(android.media.AudioManager.STREAM_VOICE_CALL, ToneGenerator.MAX_VOLUME);
        tg.startTone(ToneGenerator.TONE_DTMF_0, 100);

        mDecodeBuffer = new short[codec.frame_size()];
		CommonUtils.noise(mDecodeBuffer, mDecodeBuffer.length, 1024);
		mAudioTrack.write(mDecodeBuffer, 0, mDecodeBuffer.length);
		mAudioTrack.play();
		CommonUtils.logd(TAG, "minBufSize = "+minBufSize);
	}

	@Override
	public void output(byte[] buf, int length) throws IOException {
		final int len = mCodec.decode(buf, mDecodeBuffer, mDecodeBuffer.length);
		final int writeLength = mAudioTrack.write(mDecodeBuffer, 0, len);
		CommonUtils.logd(TAG, "speaker write "+writeLength+" byte");
	}

	@Override
	public void close() {
		mAudioTrack.stop();
	}
}
