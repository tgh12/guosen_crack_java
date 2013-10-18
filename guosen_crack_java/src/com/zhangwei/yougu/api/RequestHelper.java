package com.zhangwei.yougu.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.eno.kjava.system.ENODataEncoder;
import com.eno.utils.ENOUtils;
import com.eno.utils.TCRS;
import com.guosen.android.system.SystemHUB;

import com.jcraft.jzlib.ZInputStream;
import com.zhangwei.yougu.androidconvert.Log;

public class RequestHelper {

	private static final String TAG = "RequestHelper";
	private static RequestHelper ins = null;

	private RequestHelper() {

	}

	public static RequestHelper getInstance() {
		if (ins == null) {
			ins = new RequestHelper();
		}

		return ins;
	}

	//sessionid: 20130902160341538458
	//userid: 538458
	//ak: 403001006
	//ts: 1378113440801538458
	    //1378991963020
	public String Get(String host, String url, String sessionid, String userid, String ak, String did, String am) {
		//Log.e(TAG, "Get url:" + url);
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet("http://" + host + url);
		HttpResponse response;

		String ret = null;
		try {
			if(sessionid!=null){
				httpget.setHeader("sessionid", sessionid);
			}

			if(userid!=null){
				httpget.setHeader("userid", userid);
			}

			if(ak!=null){
				httpget.setHeader("ak", ak);
			}
			
			if(did!=null){
				httpget.setHeader("did", did);
			}
			
			if(am!=null){
				httpget.setHeader("am", am);
			}

			if(userid!=null){
				httpget.setHeader("ts", String.valueOf(System.currentTimeMillis()) + userid);
			}else{
				httpget.setHeader("ts", String.valueOf(System.currentTimeMillis()) + "538458");
			}

			
			
			httpget.setHeader("Host",  host);
			httpget.setHeader("Connection",  "Keep-Alive");
			httpget.setHeader("User-Agent",  "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

			
			
			//httpclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpRequestRetryHandler());
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				int len = 0;
				byte[] data = new byte[1024];

				ByteArrayBuffer ba = new ByteArrayBuffer(1024);
				while ((len = instream.read(data)) > 0) {
					ba.append(data, 0, len);
				}
				
				ret = new String(ba.toByteArray());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Log.e(TAG, ret);

		return ret;

	}
	
	public byte[] Post(String host, String url, List<NameValuePair> postData) {
		//Log.e(TAG, "Get url:" + url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://" + host + url);
		HttpResponse response;

		byte[] ret = null;
		try {
			
			httppost.setHeader("User-Agent",  "ENO KJava Client");
			httppost.setHeader("Content-Language",  "CN");
			httppost.setHeader("Content-Type",  "application/x-www-form-urlencoded");
			httppost.setHeader("Host",  host);
			httppost.setHeader("Connection",  "Keep-Alive");
			httppost.setHeader("Accept-Encoding",  "gzip");
				
			//  设置HTTP POST请求参数  
			httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));

			
			//httpclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpRequestRetryHandler());
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				int len = 0;
				byte[] data = new byte[1024];

				ByteArrayBuffer ba = new ByteArrayBuffer(1024);
				while ((len = instream.read(data)) > 0) {
					ba.append(data, 0, len);
				}

/*				ENODataEncoder m_encoder = new ENODataEncoder();
				
				byte[] out = m_encoder.compressData(ba.toByteArray(), (byte) 18, false);*/

				
				return ba.toByteArray();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		return ret;

	}

	public byte[] Post2(String string, String string2,
			List<NameValuePair> postData) {
		// TODO Auto-generated method stub
		return null;
	}

}
