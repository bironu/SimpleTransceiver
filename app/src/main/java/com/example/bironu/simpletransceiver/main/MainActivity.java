package com.example.bironu.simpletransceiver.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.databinding.MainActivityBinding;
import com.example.bironu.simpletransceiver.service.RtpService;

/**
 * 起動時最初に表示されるメイン画面。
 */
public class MainActivity extends Activity {
	public static final String TAG = MainActivity.class.getSimpleName();

    private MainActivityController mMainActivityController;
    private MainActivityBroadcastReceiver mBroadcastReceiver;

	private int mVolumeControlStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        MainViewModel mainViewModel = new MainViewModel();
        MainActivityModel mainActivityModel = new MainActivityModel(this, mainViewModel);
        mMainActivityController = new MainActivityController(mainActivityModel);
        mBroadcastReceiver = new MainActivityBroadcastReceiver(mainActivityModel);

        binding.setMainViewModel(mainViewModel);

		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mainViewModel.setSpeakerMode(am.isSpeakerphoneOn());

        binding.checkSpeaker.setOnCheckedChangeListener(mMainActivityController);
        binding.toggleSend.setOnCheckedChangeListener(mMainActivityController);
        binding.buttonAddForwardIpAddress.setOnClickListener(mMainActivityController);
        binding.listForwardIpAddress.setOnItemClickListener(mMainActivityController);

        Intent rtpService = new Intent(this.getApplicationContext(), RtpService.class);
        if(savedInstanceState == null) {
            this.startService(rtpService);
        }
        this.bindService(rtpService, mMainActivityController, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume() {
		super.onResume();

        // デフォルトの音量調整ボタン押下時の操作対象ストリームを取得しておく
        mVolumeControlStream = this.getVolumeControlStream();
        // 代わりに音量調整ボタン押下時の操作対象ストリームを通話にしておく
        this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
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

		this.unbindService(mMainActivityController);
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
        boolean result = mMainActivityController.onOptionItemSelected(item);
        if(!result) {
            result = super.onOptionsItemSelected(item);
        }
		return result;
	}
}
