package org.our.android.ouracadmy.p2p;

import java.io.*;
import java.net.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;

public class P2PServerService extends Service {
	private static final int[] PORT = { 7777, 7778, 7779, 7780, 7781 };

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runServer();
		return START_STICKY;
	}

	
    public void runServer() {
        ServerSocket serverSock = startServer();
        if (serverSock == null) {
                return;
        }

        while (true) {
                Socket clientSock = null;
                // accepting client
                try {
                        clientSock = acceptClient(serverSock);
                } catch (IOException e) {
                        closeSocket(clientSock);
                        continue;
                }
                Log.d(WIFI_P2P_SERVICE, "Client Accepted : " + clientSock.toString());
                new P2PSession(clientSock).start();
        }
}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void onDestroy() {

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

		String clientAddress = clientSock.getInetAddress().getHostAddress();
		int clientPort = clientSock.getPort();
		Log.d(WIFI_P2P_SERVICE, "Client connected,  IP: " + clientAddress
				+ ", Port:" + clientPort);

		return clientSock;
	}

	public void closeSocket(Socket sock) {
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				;
			}
		}
	}
}
