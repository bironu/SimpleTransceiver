package com.example.bironu.simpletransceiver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.bironu.simpletransceiver.common.CommonUtils;

/**
 *
 */
class RtpServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = RtpServiceBroadcastReceiver.class.getSimpleName();

    private final IRtpServiceBinder mBinder;

    RtpServiceBroadcastReceiver(IRtpServiceBinder binder) {
        mBinder = binder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        CommonUtils.logd(TAG, "receive " + action);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            mBinder.setLocalIpAddress();
        }
    }
}
