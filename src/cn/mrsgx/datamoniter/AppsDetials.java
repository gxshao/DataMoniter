package cn.mrsgx.datamoniter;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import oper.HandleProcess;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class AppsDetials extends ActionBarActivity {
	ActionBar actionbar;
	String pid;
	String packageName;
	PackageManager pm;
	PackageInfo pi;
	HandleProcess prcs = new HandleProcess();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_detials);
		Bundle ex=this.getIntent().getExtras();
		packageName=ex.getString("packagename");
		pid=ex.getString("pid");
		actionbar=this.getSupportActionBar();
		actionbar.setTitle("·µ»Ø");
		actionbar.setDisplayHomeAsUpEnabled(true);
		pm=this.getPackageManager();
		try {
			pi=pm.getPackageInfo(packageName,0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(pi!=null)
			SetPackageInfo();
	}
	private void SetPackageInfo() {
		ImageView iv=(ImageView) findViewById(R.id.iv_detial_icon);
		TextView tv_appname=(TextView) findViewById(R.id.tv_detial_appname);
		TextView tv_packagename=(TextView) findViewById(R.id.tv_detial_packagename);
		TextView tv_text=(TextView) findViewById(R.id.text);
		iv.setImageDrawable(pi.applicationInfo.loadIcon(pm));
		tv_appname.setText(pi.applicationInfo.loadLabel(pm));
		tv_packagename.setText(packageName);
		tv_text.setText(prcs.getNetstat(pid));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apps_detials, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
