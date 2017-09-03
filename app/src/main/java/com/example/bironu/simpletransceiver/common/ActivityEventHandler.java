package com.example.bironu.simpletransceiver.common;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.bironu.simpletransceiver.activitys.FinishActivityEvent;
import com.example.bironu.simpletransceiver.activitys.NoBindRtpServiceAlertEvent;
import com.example.bironu.simpletransceiver.activitys.StartPreferenceActivityEvent;
import com.example.bironu.simpletransceiver.activitys.preference.PreferencesActivity;

import org.greenrobot.eventbus.Subscribe;

/**
 *
 */
public class ActivityEventHandler {
    private final Activity mActivity;

    public ActivityEventHandler(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Subscribe
    public void onFinishActivityEvent(FinishActivityEvent event){
        mActivity.finish();
    }

    @Subscribe
    public void onStartPreferenceActivityEvent(StartPreferenceActivityEvent event){
        mActivity.startActivity(new Intent(mActivity, PreferencesActivity.class));
    }

    @Subscribe
    public void onNoBindRtpServiceAlertEvent(NoBindRtpServiceAlertEvent event) {
        event.createAlertDialogFragment(mActivity).show(mActivity.getFragmentManager(), event.getFragmentTag());
    }
}
