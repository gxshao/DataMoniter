package cn.mrsgx.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import cn.mrsgx.datamoniter.MainActivity;
import cn.mrsgx.datamoniter.R;
import obj.Apps;
import oper.CallbackGetData;

public class DataService extends Service {

	PackageManager pm;
	private IBinder binder = new GetService();
	CallbackGetData getDatas;
	ConnectivityManager manager;
	static List<Apps> apps;
	static HashMap<String, Object> OperObj;
	List<HashMap<String, Object>> datalist;
	static Thread mThread, dialogThread;
	static Context content;

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public class GetService extends Binder {
		public GetService getService() {
			return GetService.this;
		}

		public void SetCallback(CallbackGetData callbaks) {
			setCallback(callbaks);
			return;
		}
	}

	@Override
	public void onCreate() {
		content=this;
		apps = new ArrayList<Apps>();
		mThread = new Thread(GetData);
		mThread.start();
		super.onCreate();
	}

	public void setCallback(CallbackGetData callbacks) {
		this.getDatas = callbacks;
	}

	private List<Apps> GetTraffic() {
		List<Apps> temp = new ArrayList<Apps>();
		pm = getPackageManager();
		List<AndroidAppProcess> papis = ProcessManager.getRunningAppProcesses();
		List<String> pkgs = new ArrayList<String>();
		List<String> pids = new ArrayList<String>();
		for (AndroidAppProcess papi : papis) {
			pkgs.add(papi.name);
			pids.add(papi.pid + "");
		}
		/// 获取应用程序
		int i = 0;
		for (String packageName : pkgs) {
			try {
				ApplicationInfo pi = pm.getApplicationInfo(packageName, 0);
				int Uid = pi.uid;
				long rx = TrafficStats.getUidRxBytes(Uid);
				long tx = TrafficStats.getUidTxBytes(Uid);
				if (rx < 0 || tx < 0 || rx + tx == 0) {
					i++;
					continue;
				} else {
					Apps packet = new Apps(Uid, (String) pi.loadLabel(pm), tx, rx, pi.loadIcon(pm), pi.packageName);
					packet.setPid(pids.get(i));
					temp.add(packet);
				}
			} catch (NameNotFoundException e) {
			}
			i++;
		}
		return temp;
	}

	Handler hand = new Handler();
  	Runnable GetData = new Runnable() {
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			List<Apps> data = new ArrayList<Apps>();
			data = GetTraffic();
			DecimalFormat df = new DecimalFormat("#0.00");
			for (Apps app : apps) {
				double x = app.getUpload();
				double y = app.getDownload();
				for (int j = 0; j < data.size(); j++) {
					if (app.getUid() == data.get(j).getUid()) {
						data.get(j).perUp = (data.get(j).getUpload() - x) / 1024;
						data.get(j).perDown = (data.get(j).getDownload() - y) / 1024;
						break;
					}

				}
			}
			List<HashMap<String, Object>> listmap = new ArrayList<HashMap<String, Object>>();
			for (Apps app : data) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("icon", app.getIcon().getCurrent());
				item.put("name", app.getAppname());
				item.put("traffic", "上传:" + df.format(app.perUp) + app.getUpUnit() + "下载:" + df.format(app.perDown)
						+ app.getDownUnit());
				item.put("uid", app.getUid());
				item.put("packagename", app.getPackageName());
				item.put("pid", app.getPid());
				int warninglevel=R.drawable.warning_green;
				double tempVal=0;
				switch (MainActivity.CONNECT_WAY) {
				case 0:
					break;
				case 1: //GPRS
					if(app.perUp<app.perDown)
					{
						tempVal=app.perDown;
					}else if(app.perUp>app.perDown)
					{
						tempVal=app.perUp;
					}
					warninglevel=getGPRSLevelWarning(tempVal);
					break;
				case 2:
					if(app.perUp<app.perDown)
					{
						tempVal=app.perDown;
					}else if(app.perUp>app.perDown)
					{
						tempVal=app.perUp;
					}
					warninglevel=getWIFILevelWarning(tempVal);
					break;
				default:
					break;
				}

				app.setLvl_warning(content.getResources().getDrawable(warninglevel));
				item.put("warning", app.getLvl_warning());
				listmap.add(item);

			}
			apps.clear();
			for(Apps app:data){
				Apps temp=new Apps();
				temp.setUid(app.getUid());
				temp.setUpload(app.getUpload());
				temp.setDownload(app.getDownload());
				apps.add(temp);
			}
			if (getDatas != null) {
				getDatas.RecvData(listmap);
			}
			hand.postDelayed(this, 2000);
		}
	};
	
	private int getGPRSLevelWarning(double val){		
		int warninglevel=R.drawable.abc_ab_share_pack_mtrl_alpha;
		if(val<20)
		{
		}
		else if(val>10&&val<50)
		{
			warninglevel=R.drawable.warning_green;
		}else if(val>50&&val<100)
		{
			warninglevel=R.drawable.warning_yellow;
		}else if(val>100&&val<150){
			warninglevel=R.drawable.warning_orage;
		}else if(val>150){
			warninglevel=R.drawable.warning_red;
		}
		return warninglevel;
	}
	private int getWIFILevelWarning(double val){		
		int warninglevel=R.drawable.abc_ab_share_pack_mtrl_alpha;
		if(val<50)
		{
		}
		else if(val>50&&val<100)
		{
			warninglevel=R.drawable.warning_green;
		}
		else if(val>100&&val<200)
		{
			warninglevel=R.drawable.warning_yellow;
		}else if(val>200&&val<500)
		{
			warninglevel=R.drawable.warning_orage;
		}else if(val>500){
			warninglevel=R.drawable.warning_red;
		}
		return warninglevel;
	}
	@Override
	public void onDestroy() {
		mThread.stop();
		super.onDestroy();
	}
}
