package org.our.android.ouracademy.youtubedownloader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.our.android.ouracademy.constants.CommonConstants;

import android.os.AsyncTask;

/**
*
* @author JiHoon, Moon
*
*/
public class YoutubeContentsTask extends AsyncTask<String, String, ArrayList<Videos>> {
	YoutubeContentsTaskCallback taskCallBack = null;
	public static String Unknow = "UNKNOWN";
	public YoutubeContentsTask(YoutubeContentsTaskCallback taskCallbackInterface) {
		this.taskCallBack = taskCallbackInterface;
	}
	
	@Override
	protected ArrayList<Videos> doInBackground(String... urls) {
		try {
			return getUrl(urls[0]);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(ArrayList<Videos> contentsList) {
		if (taskCallBack != null) {
			String url = getVideoURL(contentsList);
			taskCallBack.onCompletedContentResult(url);
		}
	}
	
	//get mp4 format, medium resulution file url
	public String getVideoURL(ArrayList<Videos> videoList) {
		String url = "";
		for (Videos video : videoList) {
			String type = video.type;
			String resolution = video.resolution;
			if ((CommonConstants.VIDEO_FORMAT_MP4).equals(type) && (CommonConstants.VIDEO_RESOLUTION_MIDIUM).equals(resolution)) {
				url = encodeURI(video.url);
				break;
			}
		}
		return url;
	}
	
	//" ", "\"에 대한 변환을 해줘야 다운가능. 
	public String encodeURI(String url) {
	    String[] findList = {" ",   "\""};
	    String[] replList = {"%20", "%22"};
	    for (int i = 0; i < findList.length; i++) {
	    	url = url.replace(findList[i], replList[i]);
	    }
	    return url;
	} 
	
	// parse to get url, type ,resolution
	public ArrayList<Videos> getUrl(String url) throws Exception {
		ArrayList<Videos> video = new ArrayList<Videos>();
		String content = "";
		content = this.getContent(url);
		String temp = "";

		Pattern p = Pattern.compile("stream_map=(.[^&]*?)amp");
		Matcher m = p.matcher(content);
		if (m.find()) {
			temp = m.group();
		} else {
			return null;
		}

		temp = temp.replaceFirst("\\\\u0026amp", "");

		String[] splitUrl = temp.split("%2Citag%3D[0-9]+%26");
		for (int i = 1; i < splitUrl.length; i++) {
			splitUrl[i] = splitUrl[i].replaceFirst("url%3D", "");
			splitUrl[i] = URLDecoder.decode(URLDecoder.decode(splitUrl[i], "UTF-8"), "UTF-8");
			splitUrl[i] = splitUrl[i].replaceFirst("sig", "signature");
			video.add(new Videos(splitUrl[i], getType(splitUrl[i]),
					getResolution(splitUrl[i])));
		}
		return video;
	}
	
	private String getContent(String url) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);

		Reader reader = null;

		reader = new InputStreamReader(response.getEntity().getContent());

		StringBuffer sb = new StringBuffer();
		{
			int read;
			char[] cbuf = new char[1024];
			while ((read = reader.read(cbuf)) != -1)
				sb.append(cbuf, 0, read);
		}
		reader.close();
		return sb.toString();
	}
	
	// get video resolution
	private String getResolution(String url) {
		String tmpResolution = "";
		Pattern p = Pattern.compile("quality=.*");
		Matcher m = p.matcher(url);
		if (m.find()) {
			tmpResolution = m.group();
		} else {
			return Unknow;
		}
		return tmpResolution.replaceFirst("quality=", "");
	}

	// get video type
	private String getType(String url) {
		String tmpType = "";
		Pattern p = Pattern.compile("type[^;&]*");
		Matcher m = p.matcher(url);
		if (m.find()) {
			tmpType = m.group();
		} else {
			return Unknow;
		}
		return tmpType.replaceFirst("type=video/x{0,1}\\-{0,1}", "");
	}
}
