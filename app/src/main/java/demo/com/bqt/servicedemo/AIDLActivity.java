package demo.com.bqt.servicedemo;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bqt.aidlservice.IAidlBinderInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AIDLActivity extends ListActivity {
	private MyServiceConnection conn;
	private IAidlBinderInterface mIBinder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] array = {"0、bindService方式开启服务 ", //
				"1、unbindService方式解除绑定服务",//
				"2、通过IBinder间接调用服务中的方法"};
		setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
		conn = new MyServiceConnection();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
			case 0://onCreate() --->onBind();--->onUnbind()-->onDestory()  绑定服务不会调用onStartCommand方法
				Intent intent = new Intent("com.bqt.service.REMOTE_SERVICE");
				intent.setPackage("com.bqt.aidlservice");
				bindService(intent, conn, BIND_AUTO_CREATE);//flags:绑定时如果Service还未创建是否自动创建；0:不自动创建；1:自动创建
				break;
			case 1://取消绑定服务
				if (conn != null) unbindService(conn);//多次调用会报IllegalArgumentException异常，但是并不崩溃
				mIBinder = null;//若不把mIBinder置为空，则服务销毁后仍然可以调用服务里的方法，因为内部类的引用还在
				break;
			case 2://访问服务中的方法
				if (mIBinder != null) {
					try {
						boolean isOK = mIBinder.callMethodInService(new Random().nextInt(3));
						Toast.makeText(this, "是否成功调用：" + isOK, Toast.LENGTH_SHORT).show();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else Toast.makeText(this, "还没有绑定服务呦……", Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	private class MyServiceConnection implements ServiceConnection {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIBinder = IAidlBinderInterface.Stub.asInterface(service);//和之前的唯一区别就是，需要通过与.aidl文件相关的方法，将Ibinder对象转换为IBinderInterface对象
			Toast.makeText(AIDLActivity.this, "服务已连接……", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Toast.makeText(AIDLActivity.this, "服务已断开连接……", Toast.LENGTH_SHORT).show();
		}
	}
}