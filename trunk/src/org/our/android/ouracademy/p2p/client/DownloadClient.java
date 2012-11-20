package org.our.android.ouracademy.p2p.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.our.android.ouracademy.constants.CommonConstants;
import org.our.android.ouracademy.dao.ContentDAO;
import org.our.android.ouracademy.dao.DAOException;
import org.our.android.ouracademy.manager.FileManager;
import org.our.android.ouracademy.model.OurContents;
import org.our.android.ouracademy.p2p.P2PClient;
import org.our.android.ouracademy.p2p.action.DownloadFile;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;

import android.content.Context;
import android.content.Intent;

public class DownloadClient extends P2PClient {
	private OurContents content;
	private Context context;
	private Intent intent;
	
	private long notiSize = 0;

	public DownloadClient(String serverAddress, OurContents content,
			Context context) {
		super(serverAddress);

		this.content = content;
		this.context = context;

		this.intent = new Intent(OurDataChangeReceiver.OUR_DATA_CHANGED);
	}

	@Override
	protected String getMethod() {
		return DownloadFile.class.getName();
	}

	@Override
	protected void setBody(JSONObject request) throws JSONException {
		request.put(OurContents.CONTENTS_ID_JSON_KEY, content.getId());
		request.put(OurContents.DOWNLOAD_POINT_JSON_KEY,
				content.getDownloadedSize());
	}

	@Override
	protected void responseProcess(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		RandomAccessFile rand = FileManager.getRandomAccessFile(content.getId(), "rws");
		rand.seek(content.getDownloadedSize());

		byte buf[] = new byte[CommonConstants.SOCKET_BUFFER_SIZE];
		long totalSize = content.getDownloadedSize();
		int len;
		while ((len = inputStream.read(buf)) != -1 || (Thread.currentThread().isInterrupted() == false)) {
			rand.write(buf, 0, len);
			totalSize += len;
			content.setDownloadedSize(totalSize);
			if(notiSize + CommonConstants.SOCKET_BUFFER_SIZE < totalSize){
				sendDownload(totalSize);
			}
		}
		rand.close();
		inputStream.close();

		content.setDownloadedSize(totalSize);

		updateDownloadedSize();
	}

	private void sendDownload(long downloadSize) {
		notiSize = downloadSize;
		intent.putExtra(OurDataChangeReceiver.ACTION,
				OurDataChangeReceiver.ACTION_DOWNLOADING);
		intent.putExtra(OurDataChangeReceiver.CONTENT_ID, content.getId());
		intent.putExtra(OurDataChangeReceiver.DOWNLAD_SIZE, downloadSize);

		context.sendBroadcast(intent);
	}
	
	private void sendCancelDownload() {
		intent.putExtra(OurDataChangeReceiver.ACTION,
				OurDataChangeReceiver.ACTION_CANCEL_DOWNLOADING);
		intent.putExtra(OurDataChangeReceiver.CONTENT_ID, content.getId());

		context.sendBroadcast(intent);
	}
	
	private void sendErrorDownload() {
		intent.putExtra(OurDataChangeReceiver.ACTION,
				OurDataChangeReceiver.ACTION_ERROR_DOWNLOADING);
		intent.putExtra(OurDataChangeReceiver.CONTENT_ID, content.getId());

		context.sendBroadcast(intent);
	}

	@Override
	public void onInterrupted() {
		updateDownloadedSize();
	}

	private void updateDownloadedSize() {
		ContentDAO contentDao = new ContentDAO();
		try {
			contentDao.updateDownloadedSize(content);
			
			if(content.getSize() != content.getDownloadedSize()){
				if(Thread.currentThread().isInterrupted()){
					sendErrorDownload();
				}else{
					sendCancelDownload();
				}
			}else{
				sendDownload(content.getDownloadedSize());
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
