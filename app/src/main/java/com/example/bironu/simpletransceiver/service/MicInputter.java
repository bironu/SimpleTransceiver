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

class MicInputter
implements DataInputter
{
	public static final String TAG = MicInputter.class.getSimpleName();
	
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    private final Codec mCodec;
	private final byte[] mBuffer;
    private final short[] mReadBuffer;
	private final AudioRecord mAudioRecord;
    private AcousticEchoCanceler mAcousticEchoCanceler;
    private NoiseSuppressor mNoiseSuppressor;

	public MicInputter(Codec codec) throws SocketException {
		final int minBufSize = AudioRecord.getMinBufferSize(codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT);
        mCodec = codec;
		mBuffer = new byte[codec.frame_size() * 2 + RtpPacket.MAX_HEADER_LENGTH];
        mReadBuffer = new short[codec.frame_size()];
		mAudioRecord = new AudioRecord(AudioSource.DEFAULT, codec.samp_rate(), CHANNEL_CONFIG, CommonSettings.AUDIO_FORMAT, minBufSize);
		aec();
		mAudioRecord.startRecording();
	}

	@Override
	public int input() throws IOException {
        final int length = mAudioRecord.read(mReadBuffer, 0, mReadBuffer.length);
        CommonUtils.logd(TAG, "mic read " + length*2 + " byte");
//		for(int i = 0; i < length; i+=2) {
//			// この辺で独自のフィルタとかかけられるかも
//		}
        final int encResult = mCodec.encode(mReadBuffer, 0, mBuffer, length);
        CommonUtils.logd(TAG, "encode result " + length*2 + " byte -> "+encResult+" byte");
		return encResult;
	}
	
	@Override
	public byte[] getBuffer() {
		return mBuffer;
	}
	
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
}
