package com.example.bironu.simpletransceiver.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.common.CommonSettings;
import com.example.bironu.simpletransceiver.databinding.MainActivityBinding;
import com.example.bironu.simpletransceiver.preference.PreferencesActivity;
import com.example.bironu.simpletransceiver.service.RtpService;

/**
 *
 */
public class MainActivity extends Activity {
	public static final String TAG = MainActivity.class.getSimpleName();

    private MainController mMainController;
    private MainBroadcastReceiver mBroadcastReceiver;

	private int mVolumeControlStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        MainViewModel mainViewModel = new MainViewModel();
        MainModel mainModel = new MainModel(this, mainViewModel);
        mMainController = new MainController(mainModel);
        mBroadcastReceiver = new MainBroadcastReceiver(mainModel);

        binding.setMainViewModel(mainViewModel);

		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mainViewModel.setSpeakerMode(am.isSpeakerphoneOn());

        binding.checkSpeaker.setOnCheckedChangeListener(mMainController);
        binding.toggleSend.setOnCheckedChangeListener(mMainController);
        binding.buttonAddForwardIpAddress.setOnClickListener(mMainController);
        binding.listForwardIpAddress.setOnItemClickListener(mMainController);

        Intent rtpService = new Intent(this.getApplicationContext(), RtpService.class);
        if(savedInstanceState == null) {
            this.startService(rtpService);
        }
        this.bindService(rtpService, mMainController, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume() {
		super.onResume();

        // デフォルトの音量調整ボタン押下時の操作対象ストリームを取得しておく
        mVolumeControlStream = this.getVolumeControlStream();
        // 代わりに音量調整ボタン押下時の操作対象ストリームを通話にしておく
        this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE);
		filter.addAction(RtpService.ACTION_BEGIN_RTP_RECEIVE);
		filter.addAction(RtpService.ACTION_END_RTP_RECEIVE);
		this.registerReceiver(mBroadcastReceiver, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.unregisterReceiver(mBroadcastReceiver);

        this.setVolumeControlStream(mVolumeControlStream);
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();

		this.unbindService(mMainController);
        if(this.isFinishing()) {
            this.stopService(new Intent(this.getApplicationContext(), RtpService.class));
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch(item.getItemId()) {
		case R.id.action_settings:
			this.startActivity(new Intent(this, PreferencesActivity.class));
			result = true;
			break;
			
		case R.id.action_logout:
			this.finish();
			result = true;
			break;
			
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}
}
