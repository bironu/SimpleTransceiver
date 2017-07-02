package com.example.bironu.simpletransceiver;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import android.util.Log;

public class CommonUtils {

	public static void noise(short[] lin, int len, int power) {
		Random rnd = new Random();
		for (int i = 0; i < len; ++i) {
			lin[i] = (short)(rnd.nextInt(power*2)-power);
		}
	}

	public static InetAddress getIPAddress () {
		InetAddress result = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress instanceof Inet4Address) {
						result = inetAddress;
					}
				}
			}
		}
		catch (SocketException ex) {
			Log.i("SocketException ", ex.toString());
		}
		return result;
	}

	
//	public static void logv(String tag, String msg) {
//		if(CommonSettings.DEBUG_LEVEL >= Log.VERBOSE) Log.v(tag, msg);
//	}
//	
//	public static void logd(String tag, String msg) {
//		if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(tag, msg);
//	}
//	
//	public static void logi(String tag, String msg) {
//		if(CommonSettings.DEBUG_LEVEL >= Log.INFO) Log.i(tag, msg);
//	}
//
//	public static void logw(String tag, String msg) {
//		if(CommonSettings.DEBUG_LEVEL >= Log.WARN) Log.w(tag, msg);
//	}
//
//	public static void loge(String tag, String msg) {
//		if(CommonSettings.DEBUG_LEVEL >= Log.ERROR) Log.e(tag, msg);
//	}
}
