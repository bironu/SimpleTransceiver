package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.activitys.main.domain.MainUseCase;
import com.example.bironu.simpletransceiver.common.CommonUtils;
import com.example.bironu.simpletransceiver.service.IRtpServiceBinder;
import com.example.bironu.simpletransceiver.service.RtpService;

/**
 * MainActivityの各種操作をハンドリングするクラス。
 */
public class MainController
        implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , AdapterView.OnItemClickListener
        , ServiceConnection
{
    public static final String TAG = MainController.class.getSimpleName();
    private final MainUseCase mUseCase;
    private final GetViewModel mGetViewModel;

    public void onCreate() {
        mUseCase.initialize();
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
        mUseCase.dispose();
    }

    public MainController(@NonNull MainUseCase useCase, @NonNull GetViewModel getViewModel) {
        mUseCase = useCase;
        mGetViewModel = getViewModel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.button_add_forward_ip_address:
            CommonUtils.logd(TAG, "R.id.button_add_forward_ip_address click.");
            mUseCase.addForwardIpAddress(mGetViewModel.getForwardIpAddress());
            break;

        default:
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
        case R.id.list_forward_ip_address:
            mUseCase.removeForwardIpAddress(l);
            break;

        default:
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
        case R.id.toggle_send:
            if (b) {
                mUseCase.startSendRtp();
            }
            else {
                mUseCase.stopSendRtp();
            }
            break;

        case R.id.check_speaker:
            mUseCase.changeSpeakerMode(b);
            break;

        default:
            break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        CommonUtils.logd(TAG, "onServiceConnected : " + componentName);
        mUseCase.onServiceConnected((IRtpServiceBinder) iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        CommonUtils.logd(TAG, "onServiceDisconnected : " + componentName);
        mUseCase.onServiceDisconnected();
    }

    public boolean onOptionItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
        case R.id.action_settings:
            mUseCase.startPreferencesActivity();
            result = true;
            break;

        case R.id.action_logout:
            mUseCase.finishMainActivity();
            result = true;
            break;

        default:
            result = false;
            break;
        }
        return result;
    }

    public void onMainBroadcastReceive(String action, Bundle extra) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            mUseCase.changeConnectivity();
        }
        else if (RtpService.ACTION_BEGIN_RTP_RECEIVE.equals(action)) {
            mUseCase.startReceiveRtp();
        }
        else if (RtpService.ACTION_END_RTP_RECEIVE.equals(action)) {
            mUseCase.stopReceiveRtp();
        }
        //else {
        //	// do nothing
        //}
    }
}
