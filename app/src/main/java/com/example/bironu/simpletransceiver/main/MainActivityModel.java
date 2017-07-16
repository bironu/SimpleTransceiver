package com.example.bironu.simpletransceiver.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.preference.Preferences;
import com.example.bironu.simpletransceiver.preference.PreferencesActivity;
import com.example.bironu.simpletransceiver.service.IRtpServiceBinder;
import com.example.bironu.simpletransceiver.service.RtpService;

import java.net.InetAddress;

/**
 * MainActivityの各種イベントや操作に応じて状態を管理するクラス。
 */
class MainActivityModel {
    public static final String TAG = MainActivityModel.class.getSimpleName();

    private final Activity mActivity;
    private final MainViewModel mMainViewModel;
    private IRtpServiceBinder mRtpServiceBinder;

    /**
     * コンストラクタ。
     * @param activity 管理対象となるActivity
     * @param mainViewModel 管理対象となるViewとbindしたクラス
     */
    MainActivityModel(Activity activity, MainViewModel mainViewModel) {
        mActivity = activity;
        mMainViewModel = mainViewModel;
    }

    /**
     * 送信先IPアドレスを追加する。
     */
    void addForwardIpAddress() {
        final String ipAddress = mMainViewModel.getForwardIpAddress();
        if(!TextUtils.isEmpty(ipAddress)) {
            mMainViewModel.setForwardIpAddress("");
            if(mRtpServiceBinder != null) {
                Preferences prefs = new Preferences(mActivity.getApplicationContext());
                final int rtpPort = prefs.getRtpPort();
                final int ctrlPort = prefs.getCtrlPort();
                mRtpServiceBinder.addSendTarget(ipAddress, ctrlPort, rtpPort);
                mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
            }
        }
    }

    /**
     * スピーカーモードを設定する。
     * @param isChecked 設定するスピーカーモード、{@code true}ならスピーカーON、{@code false}ならスピーカーOFF
     */
    void setSpeakerMode(boolean isChecked) {
        AudioManager am = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn(isChecked);
    }

    /**
     * 音声の送信ステータスを変更する。
     * @param isChecked 音声の送信ステータス、{@code true}なら送信開始、{@code false}なら送信終了
     */
    void changeSendStatus(boolean isChecked) {
        if (mRtpServiceBinder != null) {
            if (isChecked) {
                // 送信開始失敗したら
                if (!mRtpServiceBinder.beginRtpSender()) {
                    // 送信取りやめ
                    mMainViewModel.setSendStatus(false);
                }
            }
            else {
                mRtpServiceBinder.endRtpSender();
            }
        }
    }

    /**
     * 指定の送信先IPアドレスを削除する。
     * @param position 削除したい送信先IPアドレスリストの位置
     */
    void removeForwardIpAddressItem(int position) {
        if(mRtpServiceBinder != null) {
            mRtpServiceBinder.removeSendTarget(position);
            mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
        }
    }

    /**
     *　サービスにバインドした時に呼ばれる。
     * @param componentName バインドしたサービスの名前？
     * @param iBinder サービスのバインダ
     */
    void onServiceConnected(ComponentName componentName, IRtpServiceBinder iBinder) {
        CommonUtils.logd(TAG, "onServiceConnected : "+componentName);
        mRtpServiceBinder = iBinder;
        mMainViewModel.setForwardIpAddressList(mRtpServiceBinder.getAddressList());
    }

    /**
     * バインドしたサービスが不慮の事故で切断された時に呼ばれる。
     * @param componentName 切断されたサービスの名前？
     */
    void onServiceDisconnected(ComponentName componentName) {
        CommonUtils.logd(TAG, "onServiceDisconnected : "+componentName);
        mRtpServiceBinder = null;
    }

    /**
     * BroadcastIntentを受信したときに呼ばれる。
     * @param intent 受信したBroadcastIntent
     */
    void onReceive(Intent intent) {
        final String action = intent.getAction();
        CommonUtils.logd(TAG, "receive " + action);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            final String ipAddress = getLocalIpAddress();
            mMainViewModel.setLocalIpAddress(ipAddress);
        }
        else if(RtpService.ACTION_BEGIN_RTP_RECEIVE.equals(action)) {
            mMainViewModel.setIsReceiveRtp(true);
            mMainViewModel.setSendStatus(false);
        }
        else if(RtpService.ACTION_END_RTP_RECEIVE.equals(action)) {
            mMainViewModel.setIsReceiveRtp(false);
        }
        //else {
        //	// do nothing
        //}
    }

    /**
     * 設定画面を呼び出す。
     */
    void startPreferencesActivity() {
        mActivity.startActivity(new Intent(mActivity, PreferencesActivity.class));
    }

    /**
     * メイン画面を終了する。
     */
    void finishMainActivity() {
        mActivity.finish();
    }

    /**
     * 自分のIPアドレスを取得する。
     * @return IPアドレス
     */
    private String getLocalIpAddress() {
        String result = null;
        InetAddress ipAddress = CommonUtils.getIPAddress();
        if (ipAddress != null) {
            result = ipAddress.toString();
        }
        return result;
    }


}
