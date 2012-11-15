package org.our.android.ouracademy.ui.pages;


import org.our.android.ouracademy.R;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        BitmapDrawable image = (BitmapDrawable)getResources().getDrawable(R.drawable.splash);
		getWindow().setBackgroundDrawable(image);
        
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000);
    }
    
    @Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);  
	}
    
    //백키를 눌러도 종료 되지 않게.
    @Override
	public void onBackPressed() {
    	return;
    }
}
