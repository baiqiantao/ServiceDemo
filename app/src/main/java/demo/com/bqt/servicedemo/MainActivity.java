package demo.com.bqt.servicedemo;

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

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends ListActivity {
	private boolean isKeepThreadRunning;//线程结束条件
	private MyServiceConnection conn;
	private IBinderInterface mIBinder;//面向接口编程，达到解耦、隐藏的目的
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"0、开启一个线程，执行死循环操作", //
				"1、通过标记位关闭上面开启的所有线程",//
				"2、通过startService方式显示开启服务", //
				"3、通过stopService方式显示关闭服务",//
				"4、隐式方式开启或关闭服务必须设置包名",//
				"5、bindService方式开启服务 ", //
				"6、unbindService方式解除绑定服务",//
				"7、startService启动服务后再bindService",//
				"8、通过IBinder间接调用服务中的方法",//
				"9、启动AIDLActivity"};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("bqt", "onDestroy");
		isKeepThreadRunning = false; //在onDestroy中把线程的关闭条件设为true，不然会导致内存泄漏
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0:
				isKeepThreadRunning = true;
				new MyThread(this).start();
				break;
			case 1:
				isKeepThreadRunning = false;
				break;
			case 2://startService方式显示开启服务
				startService(new Intent(this, MyService.class));
				break;
			case 3://stopService方式显示关闭服务
				stopService(new Intent(this, MyService.class));
				break;
			case 4://隐式方式开启或关闭服务
				Intent intent = new Intent("com.bqt.service.TEST_SERVICE");
				/*在高版本中（5.0以后），隐式方式开启或关闭或绑定服务必须设置包名，否则直接挂掉*/
				/*java.lang.IllegalArgumentException: Service Intent must be explicit: Intent { act=com.bqt.service.TEST_SERVICE }*/
				if (new Random().nextBoolean()) intent.setPackage(getPackageName());
				startService(intent);
				break;
			case 5://bindService方式开启服务
				conn = new MyServiceConnection();
				bindService(new Intent(this, MyService.class), conn, Context.BIND_AUTO_CREATE);
				break;
			case 6:
				if (conn != null) unbindService(conn);//多次调用会报IllegalArgumentException异常，但是并不崩溃
				mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在
				break;
			case 7:
				startService(new Intent(this, MyService.class));
				bindService(new Intent(this, MyService.class), conn, BIND_AUTO_CREATE);
				/*使用bindService来绑定一个已通过startService方式启动的Service时，系统只是将Service的内部IBinder对象传递给启动者，并不会将Service的生命周期与启动者绑定，所以，此后调用unBindService方法取消绑定后，Service不会调用onDestroy方法*/
				break;
			case 8:
				if (mIBinder != null) mIBinder.callMethodInService(new Random().nextInt(3));
				else Toast.makeText(this, "还没有绑定服务呦……", Toast.LENGTH_SHORT).show();
				break;
			case 9:
				startActivity(new Intent(this, AIDLActivity.class));
				break;
		}
	}
	
	private class MyServiceConnection implements ServiceConnection {//Interface for monitoring监控 the state of an application service
		
		@Override
		/*此方法中的IBinder即为我们调用bindService方法时，Service的onBind方法返回的对象，我们可以在此方法回调后通过这个IBinder与Service进行通信 */
		public void onServiceConnected(ComponentName name, IBinder service) {
			/*Called when a connection to the Service has been established确定, with the IBinder of the communication channel to the Service.*/
			mIBinder = (IBinderInterface) service;
			Toast.makeText(MainActivity.this, "服务已连接……", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {//异常终止或者其他原因终止导致Service与访问者断开连接时回调
			Toast.makeText(MainActivity.this, "服务已断开连接……", Toast.LENGTH_SHORT).show();
		}
	}
	
	private static class MyThread extends Thread {
		SoftReference<MainActivity> context;
		
		MyThread(MainActivity activity) {
			context = new SoftReference<>(activity);
		}
		
		@Override
		public void run() {
			if (context.get() == null) return;
			context.get().runOnUiThread(() -> Toast.makeText(context.get(), "线程" + getId() + "已开启……", Toast.LENGTH_SHORT).show());
			while (context.get().isKeepThreadRunning) {//这是一个死循环，关闭线程的唯一条件就是isKeepThreadRunning==false
				String log = "线程" + getId() + new SimpleDateFormat(" yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date());
				Log.i("bqt", log);
				context.get().runOnUiThread(() -> Toast.makeText(context.get(), log, Toast.LENGTH_SHORT).show());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			context.get().runOnUiThread(() -> Toast.makeText(context.get(), "线程" + getId() + "已关闭……", Toast.LENGTH_SHORT).show());
		}
	}
}