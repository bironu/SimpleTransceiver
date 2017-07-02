package com.example.bironu.simpletransceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class DataRelayer
implements Worker
{
	public static final String TAG = DataRelayer.class.getSimpleName();

	private final AtomicBoolean mIsWorking;
	private final DataInputter mIn;
	private final List<DataOutputter> mOuts = new ArrayList<DataOutputter>();
	private Runnable mBeforeAction;
	private Runnable mAfterAction;
	
	public DataRelayer(DataInputter in) {
		mIsWorking = new AtomicBoolean(true);
		mIn = in;
	}
	
	public void addDataOutputter(DataOutputter out) {
		synchronized(mOuts) {
			mOuts.add(out);
		}
	}
	
	public void setBeforeAction(Runnable action) {
		mBeforeAction = action;
	}
	
	public void setAfterAction(Runnable action) {
		mAfterAction = action;
	}
	
	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		if(mBeforeAction != null) {
			mBeforeAction.run();
		}
		try{
			if(CommonSettings.DEBUG_LEVEL >= Log.VERBOSE) Log.d(TAG, "work start");
			while (!Thread.interrupted() && mIsWorking.get()) {
				final int length = mIn.input();
				if(length > 0) {
					final byte[] buf = mIn.getBuffer();
					synchronized(mOuts) {
						for(DataOutputter out : mOuts) {
							out.output(buf, length);
						}
					}
				}
			}
			if(CommonSettings.DEBUG_LEVEL >= Log.VERBOSE) Log.d(TAG, "work stop");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			halt();
			if(mAfterAction != null) {
				mAfterAction.run();
			}
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
	}

	public void halt() {
		mIsWorking.set(false);
		mIn.close();
		synchronized(mOuts) {
			for(DataOutputter out : mOuts) {
				out.close();
			}
		}
	}
}
