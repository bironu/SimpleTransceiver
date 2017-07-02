package com.example.bironu.simpletransceiver.main;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bironu.simpletransceiver.CommonSettings;
import com.example.bironu.simpletransceiver.CommonUtils;
import com.example.bironu.simpletransceiver.DataRelayer;
import com.example.bironu.simpletransceiver.Preferences;
import com.example.bironu.simpletransceiver.PreferencesActivity;
import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.Worker;
import com.example.bironu.simpletransceiver.service.RtpService;

public class MainActivity extends Activity
implements OnClickListener
, OnCheckedChangeListener
, OnItemClickListener
{
	public static final String TAG = MainActivity.class.getSimpleName();

	private CheckBox mCheckMic;
	private TextView mTextRtpReceiveStatus;
	
	private final ExecutorService mExec = Executors.newCachedThreadPool();
	private final List<Worker> mWorkerList = new ArrayList<Worker>();

	private EditText mEditFowardIpAddress;
	private ToggleButton mToggleSend;
	private ArrayAdapter<String> mFowardListAdapter;
	private InetAddress mLocalInetAddress;
	
	private DataRelayer mPacket2Surface;
	
	private int mVolumeControlStream;
	private RtpService.RtpServiceBinder mRtpServiceBinder;

	private class RtpServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected : "+name);
			mRtpServiceBinder = (RtpService.RtpServiceBinder)service;
			updateAdapter();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected : "+name);
			MainActivity.this.unbindService(this);
			mRtpServiceBinder = null;
		}
	}
	private RtpServiceConnection mRtpServiceConnection = new RtpServiceConnection();

	private static class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE.equals(action)) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive android.net.conn.CONNECTIVITY_CHANGE !!!");
				((MainActivity) context).setLocalIpAddress();
			}
			else if(RtpService.ACTION_BEGIN_RTP_RECEIVE.equals(action)) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive RtpService.ACTION_BEGIN_RTP_RECEIVE !!!");
				((MainActivity) context).setRtpReceiveStatus(true);
				((MainActivity) context).mToggleSend.setChecked(false);
			}
			else if(RtpService.ACTION_END_RTP_RECEIVE.equals(action)) {
				if(CommonSettings.DEBUG_LEVEL >= Log.DEBUG) Log.d(TAG, "receive RtpService.ACTION_END_RTP_RECEIVE !!!");
				((MainActivity) context).setRtpReceiveStatus(false);
			}
			else {
			}
		}
	}
	private MyBroadcastReceiver mBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		startRtpService();

		mCheckMic = (CheckBox) this.findViewById(R.id.check_mic);
		
		mTextRtpReceiveStatus = (TextView) this.findViewById(R.id.text_rtp_receive_status);

		mLocalInetAddress = CommonUtils.getIPAddress();
		if(mLocalInetAddress != null) {
			TextView ipAddress = (TextView) this.findViewById(R.id.text_local_ip_address);
			ipAddress.setText(mLocalInetAddress.toString());
		}
		
		this.findViewById(R.id.button_add_foward_ip_address).setOnClickListener(this);
		
		mEditFowardIpAddress = (EditText) this.findViewById(R.id.edit_foward_ip_address);

		CheckBox checkSpeaker = (CheckBox) this.findViewById(R.id.check_speaker);
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		checkSpeaker.setChecked(am.isSpeakerphoneOn());
		checkSpeaker.setOnCheckedChangeListener(this);

		mToggleSend = (ToggleButton) this.findViewById(R.id.toggle_send);
		mToggleSend.setOnCheckedChangeListener(this);

		mFowardListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView listFowardIpAddress = (ListView) this.findViewById(R.id.list_foward_ip_address);
		listFowardIpAddress.setAdapter(mFowardListAdapter);
		listFowardIpAddress.setOnItemClickListener(this);

		mVolumeControlStream = this.getVolumeControlStream();
		this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		
		updateAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		mBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonSettings.ACTION_NET_CONN_CONNECTIVITY_CHANGE);
		filter.addAction(RtpService.ACTION_BEGIN_RTP_RECEIVE);
		filter.addAction(RtpService.ACTION_END_RTP_RECEIVE);
		this.registerReceiver(mBroadcastReceiver, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		this.unregisterReceiver(mBroadcastReceiver);
		mBroadcastReceiver = null;
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();

		this.setVolumeControlStream(mVolumeControlStream);
		
		for(Worker worker : mWorkerList) {
			worker.halt();
		}
		mExec.shutdown();
		try {
			if(!mExec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
				mExec.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			mExec.shutdownNow();
		}
		this.unbindService(mRtpServiceConnection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		switch(item.getItemId()) {
		case R.id.action_settings:
			this.startActivity(new Intent(this, PreferencesActivity.class));
			result = true;
			break;
			
		case R.id.action_logout:
			this.stopService(new Intent(this.getApplicationContext(), RtpService.class));
			this.finish();
			result = true;
			break;
			
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
		case R.id.toggle_send:
			if(isChecked) {
				if(mCheckMic.isChecked()) {
					if(mRtpServiceBinder != null){
						if(!mRtpServiceBinder.beginRtpSender()) {
							mToggleSend.setChecked(false);
						}
					}
				}
			}
			else {
				if(mRtpServiceBinder != null){
					mRtpServiceBinder.endRtpSender();
				}
			}
			break;
			
		case R.id.check_speaker:{
			AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			am.setSpeakerphoneOn(isChecked);
			break;
		}
		
		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_add_foward_ip_address:{
			final String ipAddress = mEditFowardIpAddress.getText().toString();
			if(!TextUtils.isEmpty(ipAddress)) {
				mEditFowardIpAddress.setText("");
				if(mRtpServiceBinder != null) {
					Preferences prefs = new Preferences(this.getApplicationContext());
					final int rtpPort = prefs.getRtpPort();
					final int imagePort = prefs.getImagePort();
					final int ctrlPort = prefs.getCtrlPort();
					mRtpServiceBinder.addSendTarget(ipAddress, ctrlPort, rtpPort, imagePort);
					updateAdapter();
				}
			}
			break;
		}
		default:
			break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()) {
		case R.id.list_foward_ip_address:{
			if(mRtpServiceBinder != null) {
				mRtpServiceBinder.removeSendTarget(position);
			}
			updateAdapter();
			break;
		}
		default:
			break;
		}
	}

	private void startRtpService() {
		Intent rtpService = new Intent(this.getApplicationContext(), RtpService.class);
		this.startService(rtpService);
		this.bindService(rtpService, mRtpServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void updateAdapter() {
		if(mRtpServiceBinder != null) {
			mFowardListAdapter.clear();
			//mFowardListAdapter.addAll(mRtpServiceBinder.getAddressList());
			for(String address : mRtpServiceBinder.getAddressList()) {
				mFowardListAdapter.add(address);
			}
		}
	}
	
	void setLocalIpAddress() {
		mLocalInetAddress = CommonUtils.getIPAddress();
		if(mLocalInetAddress != null) {
			TextView ipAddress = (TextView) this.findViewById(R.id.text_local_ip_address);
			ipAddress.setText(mLocalInetAddress.toString());
		}
	}
	
	void setRtpReceiveStatus(boolean status) {
		if(status) {
			mTextRtpReceiveStatus.setVisibility(View.VISIBLE);
		}
		else {
			mTextRtpReceiveStatus.setVisibility(View.GONE);
		}
	}
}
