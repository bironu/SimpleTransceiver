package com.example.bironu.simpletransceiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

/**
 * Sipのアカウント設定
 */
public class Preferences {

	private final SharedPreferences mPref;
	private final Resources mRes;
	private final Object mLock = new Object();

	public Preferences(Context context){
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
		mRes = context.getResources();
	}

	public void setRtpPort(int port){
		synchronized(mLock) {
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString(mRes.getString(R.string.pref_key_rtp_port), String.valueOf(port));
			editor.commit();
		}
	}

	public int getRtpPort() {
		synchronized(mLock) {
			return Integer.parseInt(mPref.getString(mRes.getString(R.string.pref_key_rtp_port), mRes.getString(R.string.pref_default_value_rtp_port)));
		}
	}

	public void setCtrlPort(int port){
		synchronized(mLock) {
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString(mRes.getString(R.string.pref_key_ctrl_port), String.valueOf(port));
			editor.commit();
		}
	}

	public int getCtrlPort() {
		synchronized(mLock) {
			return Integer.parseInt(mPref.getString(mRes.getString(R.string.pref_key_ctrl_port), mRes.getString(R.string.pref_default_value_ctrl_port)));
		}
	}

	public void setImagePort(int port){
		synchronized(mLock) {
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString(mRes.getString(R.string.pref_key_image_port), String.valueOf(port));
			editor.commit();
		}
	}

	public int getImagePort() {
		synchronized(mLock) {
			return Integer.parseInt(mPref.getString(mRes.getString(R.string.pref_key_image_port), mRes.getString(R.string.pref_default_value_image_port)));
		}
	}
	
	public void setFilterThreshold(int threshold){
		synchronized(mLock) {
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString(mRes.getString(R.string.pref_key_filter_threshold), String.valueOf(threshold));
			editor.commit();
		}
	}

	public int getFilterThreshold() {
		synchronized(mLock) {
			return Integer.parseInt(mPref.getString(mRes.getString(R.string.pref_key_filter_threshold), mRes.getString(R.string.pref_default_value_filter_threshold)));
		}
	}

	public void setAccountLevel(int level){
		synchronized(mLock) {
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString(mRes.getString(R.string.pref_key_account_level), String.valueOf(level));
			editor.commit();
		}
	}

	public int getAccountLevel() {
		synchronized(mLock) {
			return Integer.parseInt(mPref.getString(mRes.getString(R.string.pref_key_account_level), mRes.getString(R.string.pref_default_value_account_level)));
		}
	}
	
	
}
