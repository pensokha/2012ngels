/*
 * @(#)P2PSession.java $version 2012. 10. 9.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.p2p.action.OurP2PAction;

public class P2PSession implements Runnable {

	private Socket clientSock;
	

	/**
	 * 
	 */
	public P2PSession(Socket clientSock) {
		this.clientSock = clientSock;
	}

	public void run() {
		String request;
		try {
			request = JSONProtocol.read(clientSock);
		} catch (IOException e) {
			P2PManager.close(clientSock);
			return;
		}

		try {
			JSONObject json = new JSONObject(request);
			String method = json.getJSONObject("header").getString("method");
			OurP2PAction action = (OurP2PAction)(Class.forName(method).newInstance());
			
			action.excute(clientSock, json);
			
		//Exception에 따른 적절한 ErrorCode를 Client에 주어야 한다. 추후 작업. 
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		
		P2PManager.close(clientSock);
	}	
}
