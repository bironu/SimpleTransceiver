package com.example.bironu.simpletransceiver.event;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;

/**
 * Activity共通っぽいイベントのハンドラ。
 * EventBusにて動作。
 */
public class ActivityEventHandler
{
    private final Activity mActivity;

    public ActivityEventHandler(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Subscribe
    public void onFinishActivityEvent(FinishActivityEvent event) {
        mActivity.finish();
    }

    @Subscribe
    public void onNoBindRtpServiceAlertEvent(NoBindRtpServiceAlertEvent event) {
        event.createAlertDialogFragment(mActivity).show(mActivity.getFragmentManager(), event.getFragmentTag());
    }

    @Subscribe
    public void onStartActivityEvent(StartActivityEvent event) {
        mActivity.startActivity(event.getIntent(mActivity));
    }

}
