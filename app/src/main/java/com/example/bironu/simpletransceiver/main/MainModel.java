package com.example.bironu.simpletransceiver.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.Preferences;
import com.example.bironu.simpletransceiver.service.RtpService;

/**
 *
 */
class MainModel {
    public static final String TAG = MainModel.class.getSimpleName();

    private final Activity mActivity;
    private final MainViewModel mMainViewModel;
    private RtpService.RtpServiceBinder mRtpServiceBinder;

    /**
     *
     * @param activity
     * @param mainViewModel
     */
    MainModel(Activity activity, MainViewModel mainViewModel) {
        mActivity = activity;
        mMainViewModel = mainViewModel;
    }

    /**
     *
     */
    void onClickAddForwardIpAddress() {
        final String ipAddress = mMainViewModel.getForwardIpAddress();
        if(!TextUtils.isEmpty(ipAddress)) {
            mMainViewModel.setForwardIpAddress("");
            if(mRtpServiceBinder != null) {
                Preferences prefs = new Preferences(mActivity.getApplicationContext());
                final int rtpPort = prefs.getRtpPort();
                final int imagePort = prefs.getImagePort();
                final int ctrlPort = prefs.getCtrlPort();
                mRtpServiceBinder.addSendTarget(ipAddress, ctrlPort, rtpPort, imagePort);
                mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
            }
        }
    }

    /**
     *
     * @param isChecked
     */
    void onCheckedChangedCheckSpeaker(boolean isChecked) {
        AudioManager am = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(isChecked);
    }

    void onCheckedChangedToggleSend(boolean isChecked) {
        if (mRtpServiceBinder != null) {
            if (isChecked) {
                // 送信開始失敗したら
                if (!mRtpServiceBinder.beginRtpSender()) {
                    // 送信取りやめ
                    mMainViewModel.setIsSend(false);
                }
            }
            else {
                mRtpServiceBinder.endRtpSender();
            }
        }
    }

    /**
     *
     * @param position
     */
    void onItemClickListForwardIpAddress(int position) {
        if(mRtpServiceBinder != null) {
            mRtpServiceBinder.removeSendTarget(position);
            mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
        }
    }

    /**
     *
     * @param componentName
     * @param iBinder
     */
    void onServiceConnected(ComponentName componentName, RtpService.RtpServiceBinder iBinder) {
        Log.d(TAG, "onServiceConnected : "+componentName);
        mRtpServiceBinder = iBinder;
        mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
    }

    /**
     *
     * @param componentName
     */
    void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected : "+componentName);
        mRtpServiceBinder = null;
    }

    /**
     *
     * @param intent
     */
    void onReceive(Intent intent) {
        final String action = intent.getAction();
        if(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE.equals(action)) {
            if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive android.net.conn.CONNECTIVITY_CHANGE !!!");
            mMainViewModel.setLocalIpAddress();
        }
        else if(RtpService.ACTION_BEGIN_RTP_RECEIVE.equals(action)) {
            if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive RtpService.ACTION_BEGIN_RTP_RECEIVE !!!");
            mMainViewModel.setIsReceiveRtp(true);
            mMainViewModel.setIsSend(false);
        }
        else if(RtpService.ACTION_END_RTP_RECEIVE.equals(action)) {
            if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive RtpService.ACTION_END_RTP_RECEIVE !!!");
            mMainViewModel.setIsReceiveRtp(false);
        }
        //else {
        //	// do nothing
        //}
    }
}
