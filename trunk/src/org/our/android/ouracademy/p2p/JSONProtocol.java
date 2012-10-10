package org.our.android.ouracademy.p2p;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class JSONProtocol {
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	public static void write(Socket sock, String json) throws IOException{
		OutputStream out = sock.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		
		byte[] jsonByte = json.getBytes();
		dos.writeInt(jsonByte.length);
		dos.write(jsonByte, 0, jsonByte.length);
	}
	
	public static String read(Socket sock) throws IOException{
		DataInputStream in = null;
		try {
			in = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			P2PManager.close(in);
			throw e;
		}

		int reqSize = 0;
		try {
			reqSize = in.readInt();
		} catch (IOException e) {
			P2PManager.close(in);
			throw e;
		}

		String response;
		try {
			response = getRequest(in, reqSize);
		} catch (IOException e) {
			P2PManager.close(in);
			throw e;
		}
		return response;
	}
	
	public static String getRequest(InputStream in, int size) throws IOException {
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
			if (totalLen >= size) {
				break;
			}
		}

		return baos.toString(DEFAULT_CHARSET);
	}
}
