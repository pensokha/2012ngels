package org.our.android.ouracademy.downloader;

interface IDownloadService {
	int add(String url, String path, boolean visibility, int reserved);
	boolean cancel(int id);
}