package com.example.bironu.simpletransceiver.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.example.bironu.simpletransceiver.common.CommonSettings;

/**
 * Rtpの送受信を行うサービス。
 */
public class RtpService extends Service
{
    public static final String TAG = RtpService.class.getSimpleName();
    public static final String ACTION_BEGIN_RTP_RECEIVE = CommonSettings.BASE_PACKAGE + ".action.BEGIN_RTP_RECEIVE";
    public static final String ACTION_END_RTP_RECEIVE = CommonSettings.BASE_PACKAGE + ".action.END_RTP_RECEIVE";

    private RtpServiceBroadcastReceiver mBroadcastReceiver;
    private IRtpServiceBinder mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new RtpServiceBinderImpl(this.getApplicationContext());
        mBinder.setLocalIpAddress();
        mBroadcastReceiver = new RtpServiceBroadcastReceiver(mBinder);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mBinder.endRtpReceiver();
        mBinder.endRtpSender();
        mBinder.endCtrlReceiver();
        try {
            mBinder.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.unregisterReceiver(mBroadcastReceiver);
        mBroadcastReceiver = null;

        super.onDestroy();
    }
}
