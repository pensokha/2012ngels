/*
 * @(#)P2PSession.java $version 2012. 10. 9.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.our.android.ouracademy.p2p;



import java.io.*;
import java.net.*;

public class P2PSession implements Runnable {

	private Socket clientSock;
	private final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 
	 */
	public P2PSession(Socket clientSock) {
		this.clientSock = clientSock;
	}

	public void run() {
		DataInputStream in = null;
		try {
			in = new DataInputStream(clientSock.getInputStream());
		} catch (IOException e) {
			P2PManager.close(in);
			P2PManager.close(clientSock);
			System.out.println("Getting inputStream failed");
			return;
		}

		// get request string size
		while (true) {
			int reqSize = 0;
			try {
				reqSize = in.readInt();
			} catch (IOException e) {
				P2PManager.close(in);
				P2PManager.close(clientSock);
				return;
			}
			System.out.println("Size:" + reqSize);

			String request = "";
			try {
				request = getRequest(in, reqSize);
			} catch (IOException e) {
				P2PManager.close(in);
				P2PManager.close(clientSock);
				return;
			}
			System.out.println("Json String : " + request);
		}
	}

	public String getRequest(InputStream in, int size) throws IOException {
		if (size <= 0) {
			throw new IOException();
		}

		int len = 0;
		int totalLen = 0;
		byte[] buffer = new byte[size];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size);

		while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
			totalLen += len;
			if (totalLen > size) {
				break;
			}
		}

		return baos.toString(DEFAULT_CHARSET);
	}	
	
	
	// client sample code
//	import java.io.DataOutputStream;
//	import java.io.IOException;
//	import java.io.OutputStream;
//	import java.net.Socket;
//
//	class ClientTest {
//	        public static void main(String[] args) throws IOException {
//	                Socket socket = new Socket("127.0.0.1", 7777);
//	                OutputStream out = socket.getOutputStream();
//	                DataOutputStream dos = new DataOutputStream(out);
//
//	                String json = "{this is json String}";
//	                byte [] jsonByte = json.getBytes();
//	                dos.writeInt(jsonByte.length);
//	                dos.write(jsonByte, 0, jsonByte.length);
//
//	                while(true) {
//	                        ;
//	                }
//	        }
//	}
	

}
