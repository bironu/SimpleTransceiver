package com.example.bironu.simpletransceiver.common;

import java.util.concurrent.atomic.AtomicBoolean;

public class JobWorker
implements Worker
{
	public static final String TAG = JobWorker.class.getSimpleName();

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
			CommonUtils.logd(TAG, "work start");
			while (!Thread.interrupted() && mIsWorking.get()) {
				if(!mJob.action()) {
					break;
				}
			}
			CommonUtils.logd(TAG, "work stop");
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
		try {
			mJob.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
