/*
 * @(#)P2PServer.java $version 2012. 10. 10.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.our.android.ouracademy.constants.CommonConstants;

import android.util.Log;

public class P2PServer implements Runnable {
	private static final String TAG = "P2PServerService";
	private static final int MAX_CONNECTION_PER_SERVER_THREAD = 10;
	private ServerSocket serverSock;
	
	private Executor executor = Executors.newFixedThreadPool(MAX_CONNECTION_PER_SERVER_THREAD);
	
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

		for (int i = 0; i < CommonConstants.P2P_SERVER_PORT.length; i++) {
			try {
				sock = new ServerSocket(CommonConstants.P2P_SERVER_PORT[portIdx++]);
			} catch (IOException e) {
				continue;
			}
			return sock;
		}
		return sock;
	}
}
