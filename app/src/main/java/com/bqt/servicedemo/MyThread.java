package com.bqt.servicedemo;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyThread extends Thread {
	private SoftReference<MainActivity> context;
	
	MyThread(MainActivity activity) {
		context = new SoftReference<>(activity);
	}
	
	@Override
	public void run() {
		if (context != null && context.get() != null) {
			context.get().runOnUiThread(() -> showToast("线程" + getId() + "已开启……"));
			while (context.get().isKeepThreadRunning) {//这是一个死循环，关闭线程的唯一条件就是isKeepThreadRunning==false
				Log.i("bqt", getId() + " - " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
				SystemClock.sleep(2000);
			}
			context.get().runOnUiThread(() -> showToast("线程" + getId() + "已关闭……"));
		}
	}
	
	private void showToast(String text) {
		if (context != null && context.get() != null) {
			Toast.makeText(context.get(), text, Toast.LENGTH_SHORT).show();
		}
	}
}