package org.our.android.ouracademy.wifidirect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.provider.Settings;

/**********
 * 
 * @author hyeongseokLim
 * 
 */
public abstract class WifiDirectDefaultListener implements WifiDirectListener,
		OnClickListener {
	private boolean connnect = false;
	private boolean dialogOn = true;
	protected WifiP2pManager manager;
	protected Channel channel;
	protected Context context;

	public WifiDirectDefaultListener(Context context, WifiP2pManager manager,
			Channel channel) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.context = context;
	}

	@Override
	public void onDisableP2p() {
		if (dialogOn == true) {
			new AlertDialog.Builder(context)
					.setTitle("Do you want to set wifidirect?")
					.setPositiveButton("yes", this)
					.setNegativeButton("no", this).show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == -1) {
			context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
		} else {
			dialogOn = false;
		}
	}

	@Override
	public void onConnected() {
		connnect = true;
	}

	@Override
	public void onDisConnected() {
		connnect = false;
	}

	public boolean getConnected() {
		return connnect;
	}

	@Override
	public void onDeviceInfoChanged() {
	}
}
