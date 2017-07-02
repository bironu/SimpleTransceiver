package com.example.bironu.simpletransceiver;

import android.media.AudioFormat;
import android.util.Log;

public interface CommonSettings {
	public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	public static int DEBUG_LEVEL = Log.DEBUG;
	
	public static final String ACTION_NET_CONN_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String BASE_PACKAGE = CommonSettings.class.getPackage().getName();

}
