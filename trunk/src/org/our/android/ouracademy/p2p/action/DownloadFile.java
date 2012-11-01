package org.our.android.ouracademy.p2p.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.OurDefine;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;

public class DownloadFile implements OurP2PAction {

	@Override
	public void excute(Socket socket, JSONObject data) {
		try {
			String contentId = data.getString(OurContents.CONTENTS_ID_JSON_KEY);
			long downloadPoint = data
					.getLong(OurContents.DOWNLOAD_POINT_JSON_KEY);

			RandomAccessFile rand = new RandomAccessFile(
					FileManager.getRealPathFromContentId(contentId), "r");
			rand.seek(downloadPoint);

			OutputStream out = socket.getOutputStream();
			byte buf[] = new byte[OurDefine.SOCKET_BUFFER_SIZE];
			int len;
			while ((len = rand.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.close();
			rand.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
