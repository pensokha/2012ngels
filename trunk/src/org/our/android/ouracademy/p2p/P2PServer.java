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

import android.util.*;


public class P2PServer implements Runnable {
	private static final String TAG = "P2PServerService";
	private static final int[] PORT = { 7777, 7778, 7779, 7780, 7781 };
	private static final int SERVER_NUM = PORT.length;

	/**
	 * 
	 */
	public P2PServer() {
	}

	@Override
	public void run() {
		ServerSocket serverSock = startServer();
		if (serverSock == null) {
			return;
		}
		Log.d(TAG, "Server Started : " + serverSock.toString());
		
		while (true) {
			Socket clientSock = null;
			// accepting client
			try {
				clientSock = acceptClient(serverSock);
			} catch (IOException e) {
				P2PManager.close(clientSock);
				continue;
			}
			Log.d(TAG, "Client Accepted : " + clientSock.toString());

			Executor executor = Executors.newFixedThreadPool(SERVER_NUM);
			executor.execute(new P2PSession(clientSock));
		}
	}

	public ServerSocket startServer() {
		ServerSocket sock = null;
		int portIdx = 0;

		for (int i = 0; i < PORT.length; i++) {
			try {
				sock = new ServerSocket(PORT[portIdx++]);
			} catch (IOException e) {
				continue;
			}
			return sock;
		}
		return sock;
	}

	public Socket acceptClient(ServerSocket serverSock) throws IOException {
		Socket clientSock = serverSock.accept();

		// String clientAddress = clientSock.getInetAddress().getHostAddress();
		// int clientPort = clientSock.getPort();
		return clientSock;
	}

}
