package org.our.android.ouracademy.p2p.action;

import java.net.Socket;

import org.json.JSONObject;

public interface OurP2PAction {
	public void excute(Socket socket, JSONObject data);
}
