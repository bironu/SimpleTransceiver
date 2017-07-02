package com.example.bironu.simpletransceiver;

import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class JobWorker
implements Worker
{
	public static final String TAG = DataRelayer.class.getSimpleName();

	private final AtomicBoolean mIsWorking;
	private final Job mJob;
	
	public JobWorker(Job job) {
		mIsWorking = new AtomicBoolean(true);
		mJob = job;
	}
	

	@Override
	public void run() {
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		try{
			if(CommonSettings.DEBUG_LEVEL >= Log.VERBOSE) Log.d(TAG, "work start");
			while (!Thread.interrupted() && mIsWorking.get()) {
				if(!mJob.action()) {
					break;
				}
			}
			if(CommonSettings.DEBUG_LEVEL >= Log.VERBOSE) Log.d(TAG, "work stop");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			halt();
		}
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
	}

	@Override
	public void halt() {
		mIsWorking.set(false);
		mJob.close();
	}

}
