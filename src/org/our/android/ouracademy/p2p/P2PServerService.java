package org.our.android.ouracademy.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.our.android.ouracademy.OurDefine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class P2PServerService extends Service {
	private static final String TAG = "P2PServerService";

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
                Log.d(TAG, "Client Accepted : " + clientSock.toString());
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
		super.onDestroy();
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

	public Socket acceptClient(ServerSocket serverSock) throws IOException {
		Socket clientSock = serverSock.accept();

		String clientAddress = clientSock.getInetAddress().getHostAddress();
		int clientPort = clientSock.getPort();
		Log.d(TAG, "Client connected,  IP: " + clientAddress
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
