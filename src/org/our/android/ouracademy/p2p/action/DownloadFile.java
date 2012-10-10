package org.our.android.ouracademy.p2p.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.manager.FileManager;

public class DownloadFile implements OurP2PAction {
	public static final String methodName = "DownloadFile";

	@Override
	public void excute(Socket socket, JSONObject data) {
		try {
			String id = data.getString("id");
			final File f = FileManager.getFile(id);
			
			FileManager.copyFile(new FileInputStream(f), socket.getOutputStream());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
