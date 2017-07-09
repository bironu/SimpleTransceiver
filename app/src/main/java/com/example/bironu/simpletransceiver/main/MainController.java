package com.example.bironu.simpletransceiver.main;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.service.RtpService;

/**
 *
 */
class MainController
    implements View.OnClickListener
    , CompoundButton.OnCheckedChangeListener
    , AdapterView.OnItemClickListener
    , ServiceConnection
{
    public static final String TAG = MainController.class.getSimpleName();
    private final MainModel mMainModel;

    MainController(MainModel mainModel) {
        mMainModel = mainModel;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_add_forward_ip_address:
                mMainModel.onClickAddForwardIpAddress();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()) {
            case R.id.list_forward_ip_address:
                mMainModel.onItemClickListForwardIpAddress(i);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()) {
            case R.id.toggle_send:
                mMainModel.onCheckedChangedToggleSend(b);
                break;

            case R.id.check_speaker:
                mMainModel.onCheckedChangedCheckSpeaker(b);
                break;

            default:
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mMainModel.onServiceConnected(componentName, (RtpService.RtpServiceBinder)iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mMainModel.onServiceDisconnected(componentName);
    }
}
