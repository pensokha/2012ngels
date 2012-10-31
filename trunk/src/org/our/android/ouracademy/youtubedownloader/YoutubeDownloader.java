package org.our.android.ouracademy.youtubedownloader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class YoutubeDownloader {
	public static String Unknow = "UNKNOWN";

	// request content from youtube url
	// 필히 쓰레드에서 실행해야한다. 3.0 이상부터는 메인 메소드에서 네트워크 호출하면 
	// android.os.NetworkOnMainThreadException 에러 발생
	private String getContent(URL url) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url.toURI());
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

	// parse to get url, type ,resolution
	public ArrayList<Videos> getUrl(URL url) throws Exception {
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
	
	public interface GetContentTaskCallBackInterface {
		public void onCompletedGetContentResult(String contents);
	}
}
