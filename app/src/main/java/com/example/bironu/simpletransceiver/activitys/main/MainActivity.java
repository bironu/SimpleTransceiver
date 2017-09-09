package com.example.bironu.simpletransceiver.activitys.main;

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
import com.example.bironu.simpletransceiver.activitys.main.data.MainRepository;
import com.example.bironu.simpletransceiver.activitys.main.data.MainRepositoryImpl;
import com.example.bironu.simpletransceiver.activitys.main.domain.MainUseCaseImpl;
import com.example.bironu.simpletransceiver.activitys.main.presentation.MainBroadcastReceiver;
import com.example.bironu.simpletransceiver.activitys.main.presentation.MainController;
import com.example.bironu.simpletransceiver.activitys.main.presentation.MainPresenter;
import com.example.bironu.simpletransceiver.activitys.main.presentation.MainPresenterImpl;
import com.example.bironu.simpletransceiver.activitys.main.presentation.MainViewModel;
import com.example.bironu.simpletransceiver.data.DataStore;
import com.example.bironu.simpletransceiver.data.db.SendTargetTable;
import com.example.bironu.simpletransceiver.databinding.MainActivityBinding;
import com.example.bironu.simpletransceiver.event.ActivityEventHandler;
import com.example.bironu.simpletransceiver.service.RtpService;

import org.greenrobot.eventbus.EventBus;

/**
 * 起動時最初に表示されるメイン画面。
 */
public class MainActivity extends Activity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private MainBroadcastReceiver mBroadcastReceiver;
    private MainController mController;
    private ActivityEventHandler mActivityEventHandler;

    private int mVolumeControlStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this.getApplicationContext();
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        MainViewModel viewModel = new MainViewModel();
        binding.setMainViewModel(viewModel);

        //this.setActionBar(binding.myToolbar);

        DataStore dataStore = new DataStore(context);
        MainRepository repository = new MainRepositoryImpl(dataStore);

        MainPresenter presenter = new MainPresenterImpl(viewModel);
        MainUseCaseImpl useCase = new MainUseCaseImpl(repository, presenter);

        mController = new MainController(useCase, viewModel);
        mBroadcastReceiver = new MainBroadcastReceiver(mController);

        binding.checkSpeaker.setOnCheckedChangeListener(mController);
        binding.toggleSend.setOnCheckedChangeListener(mController);
        binding.buttonAddForwardIpAddress.setOnClickListener(mController);
        binding.listForwardIpAddress.setOnItemClickListener(mController);

        Intent rtpService = new Intent(context, RtpService.class);
        if (savedInstanceState == null) {
            this.startService(rtpService);
        }
        this.bindService(rtpService, mController, Context.BIND_AUTO_CREATE);
        mController.onCreate();
        this.getLoaderManager().initLoader(SendTargetTable.LOADER_ID, null, dataStore);

        mActivityEventHandler = new ActivityEventHandler(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // デフォルトの音量調整ボタン押下時の操作対象ストリームを取得しておく
        mVolumeControlStream = this.getVolumeControlStream();
        // 代わりに音量調整ボタン押下時の操作対象ストリームを通話にしておく
        this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(RtpService.ACTION_BEGIN_RTP_RECEIVE);
        filter.addAction(RtpService.ACTION_END_RTP_RECEIVE);
        this.registerReceiver(mBroadcastReceiver, filter);
        EventBus.getDefault().register(mActivityEventHandler);

        mController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResume();
    }

    @Override
    public void onPause() {
        mController.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mController.onStop();
        EventBus.getDefault().unregister(mActivityEventHandler);
        this.unregisterReceiver(mBroadcastReceiver);
        this.setVolumeControlStream(mVolumeControlStream);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mActivityEventHandler = null;
        mController.onDestroy();
        this.unbindService(mController);
        if (this.isFinishing()) {
            this.stopService(new Intent(this.getApplicationContext(), RtpService.class));
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = mController.onOptionItemSelected(item);
        if (!result) {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }
}
