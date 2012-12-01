package org.our.android.ouracademy.p2p.client;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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
import org.our.android.ouracademy.p2p.P2PManager;
import org.our.android.ouracademy.p2p.action.DownloadFile;
import org.our.android.ouracademy.ui.pages.MainActivity.OurDataChangeReceiver;

import android.content.Context;
import android.content.Intent;

public class DownloadClient extends P2PClient {
	private OurContents content;
	private Context context;
	private Intent intent;
	private long totalSize;
	
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
		DataInputStream dataInput = null;
		RandomAccessFile rand = null;
		DataOutputStream imageOutput = null;
		
		
		long downloadingSize = 0;
		totalSize = content.getDownloadedSize();
		
		if(inputStream != null){
			try{
				dataInput = new DataInputStream(inputStream);
				
				//Image File Download
				downloadingSize = dataInput.readLong();
				if(downloadingSize != 0){
					imageOutput = new DataOutputStream(new FileOutputStream(FileManager.getImageFile(content.getId())));
					
					write(dataInput, downloadingSize, imageOutput, false);
				}
				
				//Video File Download
				downloadingSize = dataInput.readLong();
				totalSize = content.getDownloadedSize();
				rand = FileManager.getRandomAccessFile(content.getId(), "rws");
				rand.seek(totalSize);
				
				write(dataInput, downloadingSize, rand, true);
			}finally{
				P2PManager.close(imageOutput);
				P2PManager.close(rand);
				P2PManager.close(dataInput);
			}
		}

		content.setDownloadedSize(totalSize);

		updateDownloadedSize();
	}
	
	private void write(DataInputStream dataInput, long downloadingSize, DataOutput output, boolean isVideo) throws IOException{
		byte buf[] = new byte[CommonConstants.SOCKET_BUFFER_SIZE];
		int readLength, length;
		long downloadedSize = 0;
		
		readLength = (downloadingSize - downloadedSize > buf.length) ? buf.length : (int)(downloadingSize - downloadedSize);
		while ((readLength > 0) 
				&& (length = dataInput.read(buf, 0, readLength)) != -1 
				&& (Thread.currentThread().isInterrupted() == false)) {
			output.write(buf, 0, length);
			downloadedSize += length;
			readLength = (downloadingSize - downloadedSize > buf.length) ? buf.length : (int)(downloadingSize - downloadedSize);
			
			if(isVideo){
				totalSize += length;
				content.setDownloadedSize(totalSize);
				if(notiSize + CommonConstants.SOCKET_BUFFER_SIZE < totalSize){
					sendDownloadState(totalSize);
				}
			}
		}
	}

	private void sendDownloadState(long downloadSize) {
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
				sendDownloadState(content.getDownloadedSize());
			}
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
