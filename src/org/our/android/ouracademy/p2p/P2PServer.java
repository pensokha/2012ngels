/*
 * @(#)P2PServer.java $version 2012. 10. 10.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.our.android.ouracademy.p2p;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import org.our.android.ouracademy.OurDefine;

import android.util.*;

public class P2PServer implements Runnable {
	private static final String TAG = "P2PServerService";
	private static final int SERVER_NUM = OurDefine.P2P_SERVER_PORT.length;

	private ServerSocket serverSock;

	/**
	 * 
	 */
	public P2PServer() {
	}

	@Override
	public void run() {
		serverSock = startServer();
		if (serverSock == null) {
			return;
		}
		Log.d(TAG, "Server Started : " + serverSock.toString());

		while (true) {
			if (serverSock == null) {
				Log.d(TAG, "Server Stopped");
				break;
			}

			Socket clientSock = null;
			// accepting client
			try {
				clientSock = serverSock.accept();
			} catch (IOException e) {
				P2PManager.close(clientSock);
				continue;
			}
			Log.d(TAG, "Client Accepted : " + clientSock.toString());

			Executor executor = Executors.newFixedThreadPool(SERVER_NUM);
			executor.execute(new P2PSession(clientSock));
		}
	}

	public void stopServer() {
		if (serverSock != null) {
			try {
				serverSock.close();
			} catch (IOException err) {
				err.printStackTrace();
			} finally {
				serverSock = null;
			}
		}
	}

	public ServerSocket startServer() {
		ServerSocket sock = null;
		int portIdx = 0;

		for (int i = 0; i < OurDefine.P2P_SERVER_PORT.length; i++) {
			try {
				sock = new ServerSocket(OurDefine.P2P_SERVER_PORT[portIdx++]);
			} catch (IOException e) {
				continue;
			}
			return sock;
		}
		return sock;
	}
}
