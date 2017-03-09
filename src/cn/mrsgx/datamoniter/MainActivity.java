package cn.mrsgx.datamoniter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;
import cn.mrsgx.service.DataService;
import cn.mrsgx.service.DataService.GetService;
import obj.Apps;
import oper.CallbackGetData;
import oper.HandleProcess;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements CallbackGetData {
	static List<Apps> apps = new ArrayList<Apps>();
	public static int CONNECT_WAY=0;
	static HashMap<String, Object> OperObj;
	List<HashMap<String, Object>> datalist;
	ConnectivityManager manager;
	WifiNotifyReceiver wifiRecv;
	static SimpleAdapter adapter;
	private GetService myBinder;
	static ListView lv;
	HandleProcess prcs = new HandleProcess();
	Thread dialogThread;
	ProgressDialog pb;
	static Context content;
	Intent intService;
	IntentFilter filter;
	boolean ss=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		content = this;
		intService = new Intent(MainActivity.this, DataService.class);
		startService(intService);
		this.bindService(intService, sc, Context.BIND_AUTO_CREATE);
		lv = (ListView) findViewById(R.id.listView1);
		lv.setOnItemLongClickListener(ItemMenu);
		lv.setOnItemClickListener(ItemInfo);
		SetAdapter();
		lv.setAdapter(adapter);
		checkNetworkState();
		wifiRecv = new WifiNotifyReceiver();
		filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(wifiRecv, filter);
		registerForContextMenu(lv);
	}

	@Override
	protected void onResume() {
		this.registerReceiver(wifiRecv, filter);
		this.bindService(intService, sc, Context.BIND_AUTO_CREATE);
		super.onResume();
	}

	public class WifiNotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				checkNetworkState();
			}

		}

	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private boolean checkNetworkState() {
		boolean flag = false;
		// 得到网络连接信息
		manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		if (!flag) {
			this.setTitle("您尚未连接到网络..");
			this.CONNECT_WAY=0;
			Toast.makeText(MainActivity.this, "您尚未连接到网络..", 0).show();
		} else {
			isNetworkAvailable();
		}
		return flag;
	}

	/**
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接 设置一些自己的逻辑调用
	 */
	private void isNetworkAvailable() {
		State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
			this.CONNECT_WAY=1;
			this.setTitle("当前网络连接:GPRS");
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			this.CONNECT_WAY=2;
			this.setTitle("当前网络连接:WIFI");
		}

	}

	protected void onDestroy() {
		unbindService(sc);
		unregisterReceiver(wifiRecv);
		super.onDestroy();
	};

	ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = ((DataService.GetService) service).getService();
			myBinder.SetCallback(MainActivity.this);
		}
	};

	@SuppressWarnings("unused")
	private void EmptyListView(String msg) {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put("1", msg);
		data.add(item);
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.empty, new String[] { "1" },
				new int[] { R.id.em });
		lv.setAdapter(adapter);
	}

	private void SetAdapter() {

		datalist = new ArrayList<HashMap<String, Object>>();
		for (Apps app : apps) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("icon", app.getIcon().getCurrent());
			item.put("name", app.getAppname());
			item.put("traffic", "上传:" + app.perUp + app.getUpUnit() + "下载:" + app.perDown + app.getDownUnit());
			item.put("uid", app.getUid());
			item.put("packagename", app.getPackageName());
			item.put("pid", app.getPid());
			item.put("warning",getResources().getDrawable(R.drawable.warning_green));
			datalist.add(item);
		}
		adapter = new SimpleAdapter(this, datalist, R.layout.appinfo, new String[] { "icon", "name", "traffic","warning" },
				new int[] { R.id.imageView1, R.id.em, R.id.textView2,R.id.iv_warning });
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				} else
					return false;
			}
		});

	}

	/**
	 * 控件的方法
	 */
	private static final int btnKill = 1;
	private static final int btnCancel = 2;

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle((String) OperObj.get("name"));
		menu.setHeaderIcon((Drawable) OperObj.get("icon"));
		menu.add(0, btnKill, 0, "结束");
		menu.add(0, btnCancel, 0, "取消");
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case btnKill:
			if (prcs == null)
				regetTheRootAuth();
			if (prcs == null)
				return false;
			HandleProcess.KillProcess(OperObj.get("packagename").toString());
			int x = (Integer) OperObj.get("pos");
			datalist.remove(x);
			adapter.notifyDataSetChanged();
			pb = ProgressDialog.show(MainActivity.this, "", "Working on it...."); // 在当前activity中新建进度条。参数为标题和内容
			dialogThread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pb.dismiss();
					super.run();
				}
			};
			dialogThread.start();
			break;
		case btnCancel:
			break;
		default:
			break;
		}
		return true;
	}

	private static final OnItemLongClickListener ItemMenu = new OnItemLongClickListener() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			OperObj = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			OperObj.put("pos", arg2);
			return false;
		}
	};
	private OnItemClickListener ItemInfo = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			OperObj = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			Intent intent = new Intent(content, AppsDetials.class);
			intent.putExtra("packagename", (String) OperObj.get("packagename"));
			intent.putExtra("pid", (String) OperObj.get("pid"));
			startActivityForResult(intent, 0, null);
		}

	};
	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> temp=(List<HashMap<String, Object>>)msg.obj;
			datalist.clear();
			for(HashMap<String, Object> item:temp){
				datalist.add(item);
			
			}
			adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	// 菜单事件
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void regetTheRootAuth() {
		new AlertDialog.Builder(this).setTitle("Warning").setPositiveButton("确定", new OnClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				prcs = new HandleProcess();
				if (prcs != null) {
					Toast.makeText(MainActivity.this, "Root权限获取成功！", 0).show();
				} else {
					Toast.makeText(MainActivity.this, "Root权限获取失败，请重试！", 0).show();
				}
			}
		}).setNegativeButton("取消", null).setMessage("您没有root权限是否重新获取？").show();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			new AlertDialog.Builder(this).setTitle("About").setIcon(R.drawable.ic_launcher)
					.setMessage("Made By Mr.Shao 2016/10/27").show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void RecvData(List<HashMap<String, Object>> data) {
		Message msg = hand.obtainMessage();
		msg.obj=data;
		hand.sendMessage(msg);

	}

}
