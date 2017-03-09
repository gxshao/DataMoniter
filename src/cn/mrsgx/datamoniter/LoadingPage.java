package cn.mrsgx.datamoniter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_page);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent=new Intent(LoadingPage.this,MainActivity.class);
				startActivity(intent);
				LoadingPage.this.finish();
			}
		}, 2500);
	}
	
	
}
