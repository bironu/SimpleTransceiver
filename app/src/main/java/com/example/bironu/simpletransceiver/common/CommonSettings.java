package com.example.bironu.simpletransceiver.common;

import android.media.AudioFormat;
import android.util.Log;

public interface CommonSettings {
	int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	int DEBUG_LEVEL = Log.DEBUG;
	
	String BASE_PACKAGE = CommonSettings.class.getPackage().getName();

}
