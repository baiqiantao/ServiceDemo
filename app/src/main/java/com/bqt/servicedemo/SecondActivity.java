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

import java.util.Arrays;

public class SecondActivity extends ListActivity implements ServiceConnection {
	private IMyBinder mIBinder;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"通过startService方式显示开启服务",
			"通过stopService方式显示关闭服务",
			"bindService方式开启服务",
			"unbindService方式解除绑定服务"};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(array)));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				startService(new Intent(this, MyService.class));
				break;
			case 1:
				stopService(new Intent(this, MyService.class));
				break;
			case 2:
				bindService(new Intent(this, MyService.class), this, Context.BIND_AUTO_CREATE);
				break;
			case 3:
				unbindMyService();
				break;
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i("bqt", "SecondActivity-onServiceConnected，" + name.toString());
		mIBinder = (IMyBinder) service;
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("bqt", "SecondActivity-onServiceDisconnected，" + name.toString());
	}
	
	private void unbindMyService() {
		if (mIBinder != null) {
			unbindService(this);//多次调用会报IllegalArgumentException异常，但是并不崩溃
			mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在
		} else {
			Toast.makeText(this, "还没有绑定服务，不需要解绑", Toast.LENGTH_SHORT).show();
		}
	}
}