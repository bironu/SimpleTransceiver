package com.example.bironu.simpletransceiver.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * BroadcastIntentを受け取ってMainModelへ通知するためのクラス。
 */
public class MainActivityBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = MainActivityBroadcastReceiver.class.getSimpleName();
    private final MainActivityModel mMainActivityModel;

    /**
     * コンストラクタ。
     * @param mainActivityModel MainActivityのModel
     */
    public MainActivityBroadcastReceiver(@NonNull MainActivityModel mainActivityModel) {
        mMainActivityModel = mainActivityModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mMainActivityModel.onReceive(intent);
    }
}
