package org.our.android.ouracademy.download;

interface IDownloadService {
	int add(String url, String path, boolean visibility, int reserved);
	boolean cancel(int id);
}