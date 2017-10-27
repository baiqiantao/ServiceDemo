package demo.com.bqt.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	@Override
	public void onCreate() {
		Log.i("bqt", "MyService-onCreate");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*客户端每次调用startService方法时都会回调此方法；调用bindService时不会回调此方法*/
		Log.i("bqt", "MyService-onStartCommand--" + flags + "--" + startId);//flags一直为0，startId每次会自动加1
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("bqt", "MyService-onBind");
		/*如果再次使用bindService绑定Service，系统不会再调用onBind()方法，而是直接把IBinder对象传递给其他后来增加的客户端*/
		/*当访问者通过bindService方法与Service连接成功后，系统会将此返回的IBinder接口类型对象，通过bindService中的参数ServiceConnection对象的onServiceConnected方法传递给访问者，访问者通过该对象便可以与Service组件进行通信*/
		return new MyBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.i("bqt", "MyService-onRebind");
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		/*绑定多客户端情况下，需要解除所有的绑定后才会(就会自动)调用onDestoryed方法，除非service也被startService()方法开启*/
		Log.i("bqt", "MyService-onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.i("bqt", "MyService-onDestroy");
		super.onDestroy();
	}
	//**********************************************************************************************************
	
	/**
	 * 这是服务里面的一个方法，对外是隐藏的，只能通过IBinder间接访问
	 */
	private void methodInService(int money) {
		Toast.makeText(this, "服务里的方法被调用了……" + money, Toast.LENGTH_SHORT).show();
	}
	
	private class MyBinder extends Binder implements IBinderInterface {/*须实现IBinder接口或继承Binder类。此类对外是隐藏的*/
		
		public void callMethodInService(int money) {
			if (money >= 1) methodInService(money);//间接调用了服务中的方法
			else Toast.makeText(MyService.this, "对不起，余额不足  " + money, Toast.LENGTH_SHORT).show();
		}
	}
}