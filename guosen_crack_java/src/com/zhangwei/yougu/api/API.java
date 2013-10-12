package com.zhangwei.yougu.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.eno.ENOCoder.Hex;
import com.eno.ENOCoder.RSA;
import com.eno.ENOCoder.RSA2;
import com.eno.ENOCoder.base64;
import com.eno.kjava.system.ENODataEncoder;
import com.eno.utils.ENOUtils;
import com.eno.utils.TCRS;
import com.google.gson.Gson;
import com.guosen.android.system.SystemHUB;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
import com.zhangwei.yougu.androidconvert.Log;

public class API {

	
	public static final String imei = "A000004502832C";//"862620027046913";
	private static final String m_phonenum = "18071080819";
	private static final String TAG = "API";
	private static final String curver = "3.6.2.0.0.1";
	
	private static String lastver = "3.6.4.1.1.1";
	private static String session = null;
	private static String userKey_str;
	private static String m_pwd = "877451";//"628012"; //ck word?
	
	
	private static ENODataEncoder m_encoder;

	public static byte[] Decode(byte[] inputdata, byte encrpt_level, byte[] userKey_str, byte compress_level) throws Exception {
		if ((m_encoder != null) && (encrpt_level != 0))
			inputdata = m_encoder.encryptData(inputdata, encrpt_level, userKey_str, false);
		
		if (inputdata == null)
			throw new Exception("数据解码错误。");
		
		if ((m_encoder != null) && (compress_level != 0))
			inputdata = m_encoder.compressData(inputdata, compress_level, false);
		
		if (inputdata == null)
			throw new Exception("数据解压缩错误。");
		return inputdata;
	}

	public static String Encode(byte[] inputdata, byte[] userKey_str, byte compress_level, byte encrpt_level) {
		byte[] arrayOfByte = inputdata;
		if ((m_encoder != null) && (arrayOfByte != null) && (compress_level != 0))
			arrayOfByte = m_encoder.compressData(arrayOfByte, compress_level, true);
		
		if ((m_encoder != null) && (arrayOfByte != null) && (encrpt_level != 0))
			arrayOfByte = m_encoder.encryptData(arrayOfByte, encrpt_level, userKey_str, true);
		
		String str = null;
		if (arrayOfByte != null) {
			int i = arrayOfByte.length;
			str = null;
			if (i > 0)
				str = new String(base64.encode(arrayOfByte));
		}
		return str;
	}

	public static void main(String args[]){
		//init
		userKey_str = SystemHUB.getKey();
		m_encoder = new ENODataEncoder();
		
/*	      String str3 = SystemHUB.getKey();
	      RSA2 localRSA = new RSA2();
	      if (localRSA.loadPublicKey2()){
	          byte[] arrayOfByte = localRSA.encode(str3.getBytes());
	          String str4 = "userKey=" + Hex.encode(arrayOfByte);
	          Log.i("API", "getKey():" + str3 + ", userKey:" + str4);
	    	  
	      }else{
	    	  Log.e("API", "loadPublicKey2 failed");
	      }*/
		try {
			login();
			//autologin();
			register();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
/*	    try{
	        String hello = "Hello World!";

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ZOutputStream zOut = new ZOutputStream(out, JZlib.Z_BEST_COMPRESSION);
	        ObjectOutputStream objOut = new ObjectOutputStream(zOut);
	        objOut.writeObject(hello);
	        zOut.close();

	        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	        ZInputStream zIn = new ZInputStream(in);
	        ObjectInputStream objIn = new ObjectInputStream(zIn);
	        System.out.println(objIn.readObject());
	      }
	      catch (Exception e){
	        e.printStackTrace();
	      }*/
	}
	
	/**
	 * POST / HTTP/1.1
	 * User-Agent: ENO KJava Client
	 * Content-Language: CN
	 * Content-Type: application/x-www-form-urlencoded
	 * Content-Length: 359
	 * Host: goldsunhq1.guosen.cn:8002
	 * Connection: Keep-Alive
	 * Accept-Encoding: gzip
	 * conn_style=2.460.00.28731.11821&tc_service=300&tc_isunicode=1&tc_mfuncno=100&
	 * tc_sfuncno=1&TC_ENCRYPT=0&userKey=8b52ff3cf53e8a69cef0bdf8f0b7c57169ccb817afe250842ea105800ff7574f&
	 * loginType=1&loginID=18071080819&
	 * loginPwd=4c9b4f6f21952d6d3c2aa9561dfd7c47&
	 * supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&
	 * packageid=1&netaddr=18071080819*/
	public static void autologin() throws Exception{
		List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		postData.add(new BasicNameValuePair("conn_style", "2.460.00.28731.11821")); 
		postData.add(new BasicNameValuePair("tc_service", "300")); 
		postData.add(new BasicNameValuePair("tc_isunicode", "1")); 
		postData.add(new BasicNameValuePair("tc_mfuncno", "100")); 
		postData.add(new BasicNameValuePair("tc_sfuncno", "1")); 
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "0"));
		if(session!=null){
			postData.add(new BasicNameValuePair("TC_SESSION", session));
		}

		Log.e(TAG, "getKey : " + userKey_str);
		RSA2 localRSA = new RSA2();
		if (localRSA.loadPublicKey2()) {
			//userKey_str = "0dnG7+epL6KYuyFU";
			byte[] userKey_array = localRSA.encode(userKey_str.getBytes());
			String userKey = Hex.encode(userKey_array);
			Log.i("API", "userKey" + userKey);
			
			postData.add(new BasicNameValuePair("userKey", userKey)); // userKey e060d5d13460d2718f2d94428f3303adff20ee6feae4e2f35a74587fb0e3ba1c
		} else {
			Log.e("API", "loadPublicKey2 failed");
		}
	      
		
		postData.add(new BasicNameValuePair("loginType", "1"));  //0 未注册， 1已注册
		postData.add(new BasicNameValuePair("loginID", m_phonenum));  //手机号
		String loginPwd = ENOUtils.str2MD5(new StringBuilder(m_phonenum).append(m_pwd).toString());
		Log.e(TAG, "loginPwd:" + loginPwd);
		postData.add(new BasicNameValuePair("loginPwd", loginPwd));  //loginPwd 67c9786776f5e06cc06a1dd4af4c5b6a 
		postData.add(new BasicNameValuePair("supportCompress", "18")); 
		postData.add(new BasicNameValuePair("sysVer", "3.6.2.0.0.1")); 
		postData.add(new BasicNameValuePair("hwID", imei)); //imei
		postData.add(new BasicNameValuePair("softName", "Andriod1.6")); 
		postData.add(new BasicNameValuePair("packageid", "1")); 
		postData.add(new BasicNameValuePair("netaddr", m_phonenum)); 
		
		
		byte[] out = RequestHelper.getInstance().Post("goldsunhq1.guosen.cn:8002", "/", postData);

		if(out==null){
			return;
		}
		
		//byte[] out2 = m_encoder.compressData(out, (byte) 18, false);
		byte[] out2  = Decode(out, (byte)0, null, (byte)18);
		
		TCRS tcrs = new TCRS(out2);
		Log.e(TAG, "out2 len:" + out2.length + ", TCRS.IsError:" + tcrs.IsError() + ", TCRS.isEOF:" + tcrs.IsEof());
		int isok = tcrs.getByte("isok");
		
		if(isok!=0){ //ok
			session = tcrs.toString("session");
		}else{
			Log.e(TAG, "login failed!");
		}
		

		String msg = tcrs.toString("msg"); //     
		lastver = tcrs.toString("prd_soft_versn"); //版本号
		String prd_updateinfo = tcrs.toString("prd_updateinfo"); //更新内容
		String help = tcrs.toString("help");
		String popmsg = tcrs.toString("popmsg"); //跑马灯开关
		String refresh = tcrs.toString("refresh"); //刷新开关
		String multiacc = tcrs.toString("multiacc"); //多账户开关
		String reginfo = tcrs.toString("reginfo"); //注册信息
		String servurl = tcrs.toString("servurl"); //服务器地址 

		Log.e(TAG, "isok:" + isok);
		Log.e(TAG, "session:" + session);
		Log.e(TAG, "msg:" + msg);
		Log.e(TAG, "prd_soft_versn(lastver):" + lastver);
		Log.e(TAG, "prd_updateinfo:" + prd_updateinfo);
		Log.e(TAG, "help:" + help);
		Log.e(TAG, "popmsg 跑马灯开关:" + popmsg);
		Log.e(TAG, "refresh 刷新开关:" + refresh);
		Log.e(TAG, "multiacc 多账户开关:" + multiacc);
		Log.e(TAG, "reginfo 注册信息:" + reginfo);
		Log.e(TAG, "servurl 服务器地址:" + servurl);
	}
	
	
	/**
	 * POST / HTTP/1.1
	 * User-Agent: ENO KJava Client
	 * Content-Language: CN
	 * Content-Type: application/x-www-form-urlencoded
	 * Content-Length: 338
	 * Host: goldsunhq1.guosen.cn:8002
	 * Connection: Keep-Alive
	 * Accept-Encoding: gzip
	 * conn_style=2.460.00.28731.11821&tc_service=300&tc_isunicode=1&tc_mfuncno=100&
	 * tc_sfuncno=1&TC_ENCRYPT=0&userKey=f56de9df2039f06135e2d20eddc503c1756bfc7e0f21108be4f8ebb56d718354&
	 * loginType=0&loginID=&loginPwd=d41d8cd98f00b204e9800998ecf8427e&supportCompress=18&
	 * sysVer=3.6.2.0.0.1&hwID=862620027046913&softName=Andriod1.6&packageid=1&netaddr= 
	 * */
	public static void login() throws Exception{
		List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		postData.add(new BasicNameValuePair("conn_style", "2.460.00.28731.11821")); 
		postData.add(new BasicNameValuePair("tc_service", "300")); 
		postData.add(new BasicNameValuePair("tc_isunicode", "1")); 
		postData.add(new BasicNameValuePair("tc_mfuncno", "100")); 
		postData.add(new BasicNameValuePair("tc_sfuncno", "1")); 
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "0"));
		

		Log.e(TAG, "getKey : " + userKey_str);
		RSA2 localRSA = new RSA2();
		if (localRSA.loadPublicKey2()) {
			byte[] userKey_array = localRSA.encode(userKey_str.getBytes());
			String userKey = Hex.encode(userKey_array);
			Log.i("API", userKey);
			postData.add(new BasicNameValuePair("userKey", userKey));
		} else {
			Log.e("API", "loadPublicKey2 failed");
		}
	      
		
		postData.add(new BasicNameValuePair("loginType", "1"));  //0 未注册， 1已注册
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "0")); 
		postData.add(new BasicNameValuePair("loginID", m_phonenum));  //手机号
		String loginPwd = ENOUtils.str2MD5(new StringBuilder(m_phonenum).append(m_pwd).toString());
		postData.add(new BasicNameValuePair("loginPwd", loginPwd));  //d41d8cd98f00b204e9800998ecf8427e
		postData.add(new BasicNameValuePair("supportCompress", "18")); 
		postData.add(new BasicNameValuePair("sysVer", "3.6.2.0.0.1")); 
		postData.add(new BasicNameValuePair("hwID", imei)); //imei
		postData.add(new BasicNameValuePair("softName", "Andriod1.6")); 
		postData.add(new BasicNameValuePair("packageid", "1")); 
		postData.add(new BasicNameValuePair("netaddr", "")); 
		
		
		byte[] out = RequestHelper.getInstance().Post("goldsunhq1.guosen.cn:8002", "/", postData);
		byte[] out2  = Decode(out, (byte)0, null, (byte)18);
		
		//byte[] out2 = m_encoder.compressData(out, (byte) 18, false);
		TCRS tcrs = new TCRS(out2);
		Log.e(TAG, "out2 len:" + out2.length + ", TCRS.IsError:" + tcrs.IsError() + ", TCRS.isEOF:" + tcrs.IsEof());
		int isok = tcrs.getByte("isok");
		session = tcrs.toString("session");
		String msg = tcrs.toString("msg"); //     
		lastver = tcrs.toString("prd_soft_versn"); //版本号
		String prd_updateinfo = tcrs.toString("prd_updateinfo"); //更新内容
		String help = tcrs.toString("help");
		String popmsg = tcrs.toString("popmsg"); //跑马灯开关
		String refresh = tcrs.toString("refresh"); //刷新开关
		String multiacc = tcrs.toString("multiacc"); //多账户开关
		String reginfo = tcrs.toString("reginfo"); //注册信息
		String servurl = tcrs.toString("servurl"); //服务器地址 

		Log.e(TAG, "isok:" + isok);
		Log.e(TAG, "session:" + session);
		Log.e(TAG, "msg:" + msg);
		Log.e(TAG, "prd_soft_versn(lastver):" + lastver);
		Log.e(TAG, "prd_updateinfo:" + prd_updateinfo);
		Log.e(TAG, "help:" + help);
		Log.e(TAG, "popmsg 跑马灯开关:" + popmsg);
		Log.e(TAG, "refresh 刷新开关:" + refresh);
		Log.e(TAG, "multiacc 多账户开关:" + multiacc);
		Log.e(TAG, "reginfo 注册信息:" + reginfo);
		Log.e(TAG, "servurl 服务器地址:" + servurl);
	}
	
	
	/**
	 * POST / HTTP/1.1
	 * User-Agent: ENO KJava Client
	 * Content-Language: CN
	 * Content-Type: application/x-www-form-urlencoded
	 * Content-Length: 374
	 * Host: 218.18.103.48:8002
	 * Connection: Keep-Alive
	 * Accept-Encoding: gzip
	 * conn_style=2.460.00.28731.11821&tc_service=300&tc_isunicode=1&tc_mfuncno=100&
	 * tc_sfuncno=3&TC_ENCRYPT=36&
	 * TC_SESSION={0dbe16b024004977a8a937412e01f1e4591fba94a4906f4ff8dd3a99}&
	 * TC_REQLENGTH=118&
	 * TC_REQDATA=dgAAAHEzt3QsArfwdsiC9eUUvZUmjqkBDLuzk/vWqEQyJwvF0Gat9l/cPl5JW09DiOVU4p5f/N0iFIk2Nm1Pfeur54f1BPgqVtAPqgZvgquSIP18wLIWAKCcyBEtT0HWMoveCxV83PIAmUrYI+qt9KTne+xDFNvLzBaeZAy4UtU= 
	 * */
	public static void register() throws Exception{
		List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		postData.add(new BasicNameValuePair("conn_style", "2.460.00.28731.11821")); 
		postData.add(new BasicNameValuePair("tc_service", "300")); 
		postData.add(new BasicNameValuePair("tc_isunicode", "1")); 
		postData.add(new BasicNameValuePair("tc_mfuncno", "100")); 
		postData.add(new BasicNameValuePair("tc_sfuncno", "3")); 
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "36"));
		postData.add(new BasicNameValuePair("TC_SESSION", "{" + session + "}"));
		
		String TC_REQ_PLAN_DATA = "mobile=" + m_phonenum + "&curver=" + curver + "&lastver=" + lastver; //=&curver=3.6.2.0.0.1&lastver=3.6.4.1.1.1
		TC_REQ_PLAN_DATA = TC_REQ_PLAN_DATA + "&supportCompress=18"  + "&sysVer=" + curver + "&hwID=" + imei + "&softName=Andriod1.6&packageid=1&netaddr=";
		
		postData.add(new BasicNameValuePair("TC_REQLENGTH", TC_REQ_PLAN_DATA.getBytes().length + ""));
		
		//byte[] encdata = m_encoder.encryptData(TC_REQ_PLAN_DATA.getBytes(), (byte)36, userKey_str.getBytes(), true);
		String TC_REQDATA = Encode(TC_REQ_PLAN_DATA.getBytes(), userKey_str.getBytes(), (byte)0, (byte)36);
		
		postData.add(new BasicNameValuePair("TC_REQDATA", TC_REQDATA));
		Log.e(TAG, "TC_REQDATA:" + TC_REQDATA);
		
		byte[] out = RequestHelper.getInstance().Post("218.18.103.48:8002", "/", postData);
		
		if(out==null){
			return;
		}
		
		//ENODataEncoder m_encoder = new ENODataEncoder();
		//byte[] out2 = m_encoder.compressData(out, (byte) 18, false);
		byte[] out2  = Decode(out, (byte)36, null, (byte)18);
		
		TCRS tcrs = new TCRS(out2);
		Log.e(TAG, "out2 len:" + out2.length + ", TCRS.IsError:" + tcrs.IsError() + ", TCRS.isEOF:" + tcrs.IsEof());
		
		int isok = tcrs.getByte("isok");
		String session = tcrs.toString("session");
		String msg = tcrs.toString("msg"); //     
		String prd_soft_versn = tcrs.toString("prd_soft_versn"); //版本号
		String prd_updateinfo = tcrs.toString("prd_updateinfo"); //更新内容
		String help = tcrs.toString("help");
		String popmsg = tcrs.toString("popmsg"); //跑马灯开关
		String refresh = tcrs.toString("refresh"); //刷新开关
		String multiacc = tcrs.toString("multiacc"); //多账户开关
		String reginfo = tcrs.toString("reginfo"); //注册信息
		String servurl = tcrs.toString("servurl"); //服务器地址 

		Log.e(TAG, "isok:" + isok);
		Log.e(TAG, "session:" + session);
		Log.e(TAG, "msg:" + msg);
		Log.e(TAG, "prd_soft_versn:" + prd_soft_versn);
		Log.e(TAG, "prd_updateinfo:" + prd_updateinfo);
		Log.e(TAG, "help:" + help);
		Log.e(TAG, "popmsg 跑马灯开关:" + popmsg);
		Log.e(TAG, "refresh 刷新开关:" + refresh);
		Log.e(TAG, "multiacc 多账户开关:" + multiacc);
		Log.e(TAG, "reginfo 注册信息:" + reginfo);
		Log.e(TAG, "servurl 服务器地址:" + servurl);
	}
}
