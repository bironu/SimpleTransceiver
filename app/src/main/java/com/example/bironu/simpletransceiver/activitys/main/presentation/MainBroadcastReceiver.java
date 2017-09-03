package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.bironu.simpletransceiver.common.CommonUtils;

/**
 * BroadcastIntentを受け取ってMainModelへ通知するためのクラス。
 */
public class MainBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = MainBroadcastReceiver.class.getSimpleName();
    private final MainController mController;

    public MainBroadcastReceiver(MainController controller) {
        mController = controller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        CommonUtils.logd(TAG, "receive " + action);
        mController.onMainBroadcastReceive(action, intent.getExtras());
    }
}
