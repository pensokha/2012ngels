package org.our.android.ouracademy.p2p.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
		DataOutputStream dataOutput = null;
		FileInputStream imageInput = null;
		try {
			
			String contentId = data.getString(OurContents.CONTENTS_ID_JSON_KEY);
			long downloadPoint = data
					.getLong(OurContents.DOWNLOAD_POINT_JSON_KEY);

			byte buf[] = new byte[CommonConstants.SOCKET_BUFFER_SIZE];
			
			OutputStream out = socket.getOutputStream();
			
			if(out == null)
				return ;
			
			dataOutput = new DataOutputStream(out);
			
			//Image File 
			File imageFile = FileManager.getImageFile(contentId);
			dataOutput.writeLong(imageFile.length());
			
			int len;
			
			if(imageFile.exists()){
				imageInput = new FileInputStream(imageFile);
				while((len = imageInput.read(buf)) != -1){
					dataOutput.write(buf, 0, len);
				}
			}
			
			rand = FileManager.getRandomAccessFile(contentId, "r");
			rand.seek(downloadPoint);
			
			dataOutput.writeLong(rand.length() - downloadPoint);
			
			while ((len = rand.read(buf)) != -1) {
				dataOutput.write(buf, 0, len);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			P2PManager.close(dataOutput);
			P2PManager.close(rand);
			P2PManager.close(imageInput);
		}
	}
}
