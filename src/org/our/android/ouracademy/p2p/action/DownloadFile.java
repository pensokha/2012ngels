package org.our.android.ouracademy.p2p.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.P2PManager;

public class DownloadFile implements OurP2PAction {

	@Override
	public void excute(Socket socket, JSONObject data) {
		RandomAccessFile rand = null;
		OutputStream out = null;
		try {
			String contentId = data.getString(OurContents.CONTENTS_ID_JSON_KEY);
			long downloadPoint = data
					.getLong(OurContents.DOWNLOAD_POINT_JSON_KEY);

			rand = FileManager.getRandomAccessFile(contentId, "r");
			rand.seek(downloadPoint);

			out = socket.getOutputStream();
			byte buf[] = new byte[CommonConstants.SOCKET_BUFFER_SIZE];
			int len;
			while ((len = rand.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			P2PManager.close(out);
			P2PManager.close(rand);
		}
	}
}
