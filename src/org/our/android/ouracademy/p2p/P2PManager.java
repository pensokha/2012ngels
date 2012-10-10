/*
 * @(#)P2PManager.java $version 2012. 10. 10.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.our.android.ouracademy.p2p;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class P2PManager {

	public static void runServer() {
		//추후에 Thread Pooling 방식으로 변경한다. 
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new P2PServer());
	}

	/**
	 * 
	 * @param resource
	 *            Reader, Writer, OutputStream, InputStream
	 */
	public static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception ignore) {
				;
			}
		}
	}

	public static void close(Socket sock) {
		if (sock != null) {
			try {
				sock.close();
			} catch (Exception ignore) {
				;
			}
		}
	}
}
