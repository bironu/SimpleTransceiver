package com.example.bironu.simpletransceiver.event;

import android.content.Context;
import android.content.Intent;

import com.example.bironu.simpletransceiver.activitys.preference.PreferencesActivity;

/**
 * 設定画面開始イベント。
 */
public class StartPreferenceActivityEvent implements StartActivityEvent
{
    @Override
    public Intent getIntent(Context context) {
        return new Intent(context, PreferencesActivity.class);
    }
}
