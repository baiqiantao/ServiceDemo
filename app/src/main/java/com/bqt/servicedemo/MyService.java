package com.bqt.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	public static final String ACTION_MY_SERVICE = "com.bqt.service.my_action";
	
	@Override
	public void onCreate() {
		Log.i("bqt", "MyService-onCreate");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("bqt", "MyService-onStartCommand--flags=" + flags + "--startId=" + startId);//flags一直为0，startId每次会自动加1
		return super.onStartCommand(intent, flags, startId); //每次调用startService时都会回调；调用bindService时不会回调
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("bqt", "MyService-onBind");
		return new MyMyBinder(); //再次bindService时，系统不会再调用onBind()方法，而是直接把IBinder对象传递给其他后来增加的客户端
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.i("bqt", "MyService-onRebind");
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("bqt", "MyService-onUnbind");
		return super.onUnbind(intent); //绑定多客户端情况下，需要解除所有的绑定后才会(就会自动)调用onDestoryed方法
	}
	
	@Override
	public void onDestroy() {
		Log.i("bqt", "MyService-onDestroy");
		super.onDestroy(); //不知道什么时候调用，没有发现他被回调过
	}
	
	/**
	 * 这是服务里面的一个方法，对外是隐藏的，只能通过IBinder间接访问
	 */
	private void methodInService(int money) {
		Log.i("bqt", "MyService-call method in service");
		Toast.makeText(this, "调用了服务里的方法，开启了大招：" + money, Toast.LENGTH_SHORT).show();
	}
	
	private class MyMyBinder extends Binder implements IMyBinder {//实现IBinder接口或继承Binder类
		
		@Override
		public void callMethodInService(int money) {
			if (money < 1) {
				Toast.makeText(MyService.this, "对不起，余额不足，不能发大招：" + money, Toast.LENGTH_SHORT).show();
			} else {
				methodInService(money);//间接调用了服务中的方法
			}
		}
	}
}