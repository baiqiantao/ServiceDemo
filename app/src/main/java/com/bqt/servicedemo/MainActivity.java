package com.bqt.servicedemo;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends ListActivity implements ServiceConnection {
	public boolean isKeepThreadRunning;//线程结束条件
	private IMyBinder mIBinder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"0、开启一个线程，执行死循环操作",
			"1、通过标记位关闭上面开启的所有线程",
			"2、通过startService方式显示开启服务",
			"3、通过stopService方式显示关闭服务",
			"4、隐式方式开启或关闭服务，必须设置包名",
			"5、bindService方式开启服务 ",
			"6、unbindService方式解除绑定服务",
			"7、通过IBinder间接调用服务中的方法",
			"8、启动另一个Activity"};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
		isKeepThreadRunning = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isKeepThreadRunning = false; //在onDestroy中把线程的关闭条件设为true，防止内存泄漏
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0: //开启一个线程，执行死循环操作
				new MyThread(this).start();
				break;
			case 1: //通过标记位关闭上面开启的所有线程
				isKeepThreadRunning = false;
				break;
			case 2: //startService方式显示开启服务
				startService(new Intent(this, MyService.class));
				break;
			case 3://stopService方式显示关闭服务
				stopService(new Intent(this, MyService.class));
				break;
			case 4://隐式方式开启或关闭服务，必须设置包名
				Intent intent = new Intent(MyService.ACTION_MY_SERVICE);
				intent.setPackage(getPackageName()); //在高版本中，隐式方式开启或关闭或绑定服务必须设置包名，否则直接挂掉
				startService(intent);
				break;
			case 5://bindService方式开启服务
				bindService(new Intent(this, MyService.class), this, Context.BIND_AUTO_CREATE);
				break;
			case 6: //unbindService方式解除绑定服务
				unbindMyService();
				break;
			case 7: //通过IBinder间接调用服务中的方法
				callMethodInService();
				break;
			case 8: //启动另一个Activity
				startActivity(new Intent(this, SecondActivity.class));
				break;
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i("bqt", "MainActivity-onServiceConnected，" + name.toString());
		mIBinder = (IMyBinder) service;
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("bqt", "MainActivity-onServiceDisconnected，" + name.toString());
	}
	
	private void unbindMyService() {
		if (mIBinder != null) {
			unbindService(this);//多次调用会报IllegalArgumentException异常，但是并不崩溃
			mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在
		} else {
			Toast.makeText(this, "还没有绑定服务，不需要解绑", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void callMethodInService() {
		if (mIBinder != null) {
			mIBinder.callMethodInService(new Random().nextInt(3));
		} else {
			Toast.makeText(this, "还没有绑定服务", Toast.LENGTH_SHORT).show();
		}
	}
}