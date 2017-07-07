package com.example.bironu.simpletransceiver.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 *
 */
public class MainBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = MainBroadcastReceiver.class.getSimpleName();
    private final MainModel mMainModel;

    /**
     *
     * @param mainModel
     */
    public MainBroadcastReceiver(@NonNull MainModel mainModel) {
        mMainModel = mainModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mMainModel.onReceive(intent);
    }
}
