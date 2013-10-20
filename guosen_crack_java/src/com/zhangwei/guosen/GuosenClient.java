package com.zhangwei.guosen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import com.eno.ENOCoder.Hex;
import com.eno.ENOCoder.RSA;
import com.eno.ENOCoder.base64;
import com.eno.kjava.system.ENODataEncoder;
import com.eno.utils.ENOUtils;
import com.eno.utils.TCRS;
import com.guosen.android.system.SystemHUB;
import com.zhangwei.guosen.utils.StringUtils;
import com.zhangwei.yougu.androidconvert.Log;

public class GuosenClient {
	private static final String TAG = "GuosenClient";
	
	private ENODataEncoder m_encoder;
	private int tc_packageid;
	private RSA localRSA;
	private SaveAccountInfo sai;
	private String userKey = null;

	private StringBuilder sb;

	private DefaultHttpClient httpclient;
	
	public GuosenClient(SaveAccountInfo sai){

		reset(sai);
	}
	
	public void reset(SaveAccountInfo sai){
		this.sai = sai;
		
		tc_packageid = 1;
		
		SystemHUB.m_bfKey = this.sai.m_bfKey; //"XMNqxw+RhembfA5K";
		
		if(m_encoder==null){
			m_encoder = new ENODataEncoder();
			SystemHUB.m_encoder = m_encoder;
		}

		localRSA = new RSA();
		if (localRSA!=null 
				&& localRSA.loadPublicKey2()) {
			byte[] userKey_array = localRSA.encode(SystemHUB.m_bfKey.getBytes());
			userKey  = Hex.encode(userKey_array);
		} else{
			userKey = "26916c8fba83671051f79856733cf0de1ad412a1b5c207d75916a05f4bd4ce29";
		}
	}
	
	/**
	 * [GuosenClient]:TCRS.IsError:true, TCRS.isEOF:false
	 * [GuosenClient]:index:0, fields_index:0, FieldType:101, fieldName:ER_String, toString:没有查到相关软件信息
	 * */
	public boolean getSession(){
		sai.session = null;
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		postData.add(new BasicNameValuePair("tc_service", "300"));
		postData.add(new BasicNameValuePair("tc_isunicode", "1")); 
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "0"));
		postData.add(new BasicNameValuePair("tc_mfuncno", "100")); 
		postData.add(new BasicNameValuePair("tc_sfuncno", "1")); 
		
		postData.add(new BasicNameValuePair("userKey", userKey));
		
		postData.add(new BasicNameValuePair("loginType", sai.chk_word==null?"0":"1"));
		postData.add(new BasicNameValuePair("loginID", sai.chk_word==null?"":sai.phone));
		
		String loginPwd = null;
		if(sai.chk_word!=null){
			loginPwd = ENOUtils.str2MD5(new StringBuilder(sai.phone).append(sai.chk_word).toString());
		}else{
			loginPwd = ENOUtils.str2MD5(""); //d41d8cd98f00b204e9800998ecf8427e
		}
		postData.add(new BasicNameValuePair("loginPwd", loginPwd));
		postData.add(new BasicNameValuePair("tc_packageid", String.valueOf(tc_packageid)));
		
		postData.add(new BasicNameValuePair("supportCompress", "18"));
		postData.add(new BasicNameValuePair("sysVer", "3.6.4.0.0.2"));
		postData.add(new BasicNameValuePair("hwID", sai.imei));
		postData.add(new BasicNameValuePair("softName", "Andriod1.6"));
		postData.add(new BasicNameValuePair("netaddr", sai.chk_word==null?"":sai.phone));
		postData.add(new BasicNameValuePair("conn_style", "2.460.02.0.0"));
		postData.add(new BasicNameValuePair("device_vers", "16|4.1.1"));

		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			sai.session = tcrs.toString("session");
			//bd8e89a55ce88cee80dbc353619e56aa92257b49a5dba7cce9ab2bad
			Log.e(TAG, "session got:" + sai.session);
		}
		

		return !StringUtils.isEmpty(sai.session);
	}
	
	public void login(){
		
	}
	
	public void register(){
		List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		postData.add(new BasicNameValuePair("tc_service", "300"));
		postData.add(new BasicNameValuePair("tc_isunicode", "1")); 
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "36"));
		postData.add(new BasicNameValuePair("TC_SESSION", "{" + sai.session + "}"));
		postData.add(new BasicNameValuePair("tc_mfuncno", "100")); 
		postData.add(new BasicNameValuePair("tc_sfuncno", "3")); 
		
		postData.add(new BasicNameValuePair("tc_packageid", String.valueOf(tc_packageid)));
		
		Build_TC_REQDATA_INIT("mobile", sai.phone);
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add(new BasicNameValuePair("TC_REQLENGTH", "" + TC_REQDATA_LEN));
		postData.add(new BasicNameValuePair("TC_REQDATA", TC_REQDATA));
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			sai.session = tcrs.toString("session");
			//bd8e89a55ce88cee80dbc353619e56aa92257b49a5dba7cce9ab2bad
			Log.e(TAG, "session got:" + sai.session);
		}
	}
	
	private int Build_TC_REQDATA_GET_NUM() {
		// TODO Auto-generated method stub
		return sb.toString().length();
	}

	private void Build_TC_REQDATA_INIT(String name, String value) {
		// TODO Auto-generated method stub
		sb = new StringBuilder();
		sb = sb.append(name).append("=").append(value);
	}

	
	private void Build_TC_REQDATA_ADD(String name, String value) {
		// TODO Auto-generated method stub
		if(sb==null){
			sb = new StringBuilder();
		}
		
		sb = sb.append("&").append(name).append("=").append(value);
	}
	
	private String Build_TC_REQDATA_GET() {
		// TODO Auto-generated method stub
		String tc_req_data_plain = null;
		if(sb==null){
			tc_req_data_plain = "";
		}else{
			tc_req_data_plain = sb.toString();
		}
		
		return Encode(tc_req_data_plain.getBytes(), sai.m_bfKey.getBytes(), (byte)0, (byte)36);
		
	}


	public void  auth(){
		
	}
	
	public void show_asset(){
		
	}
	
	public void show_stocks(){
		
	}
	
	public void buy(){
		
	}

	public void sell(){
		
	}
	
	public void get_cur_price(){
		
	}

	private TCRS Post(String host, String url, List<NameValuePair> postData) {
		if(httpclient==null){
			httpclient = new DefaultHttpClient();
		}

		HttpPost httppost = new HttpPost("http://" + host + url);
		HttpResponse response;

		TCRS ret = null;
		int mainID = 0;
		int packageID = 0;
		int compressLevel = 0;
		int encrpytLevel = 0;
		int tc_mfuncno = 0;
		int tc_sfuncno = 0;
		
		try {
			
			httppost.setHeader("Host",  host);
			httppost.setHeader("Accept", "image/png");
			
			httppost.setHeader("User-Agent",  "ENO KJava Client");
			httppost.setHeader("Content-Language",  "CN");
			httppost.setHeader("Content-Type",  "application/x-www-form-urlencoded");
				
			//  设置HTTP POST请求参数  
			httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));

			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			Header[] heads = response.getAllHeaders();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
				int len = 0;
				byte[] data = new byte[1024];

				ByteArrayBuffer ba = new ByteArrayBuffer(1024);
				while ((len = instream.read(data)) > 0) {
					ba.append(data, 0, len);
				}
				
				if(heads!=null){
					for(Header head : heads){
						if("ETag".equals(head.getName()) && head.getValue()!=null){
							String[] etags = head.getValue().split(",");
							if(etags.length>=2){
								mainID = Integer.valueOf(etags[0]);
								packageID = Integer.valueOf(etags[1]);
							}
							
							if(etags.length>=4){
								compressLevel = Integer.valueOf(etags[2]);
								encrpytLevel = Integer.valueOf(etags[3]);
							}
							
							if(etags.length>=6){
								tc_mfuncno = Integer.valueOf(etags[4]);
								tc_sfuncno = Integer.valueOf(etags[5]);
							}

						}
					}
				}
				
				byte[] dec_out = Decode(ba.toByteArray(), (byte)encrpytLevel, SystemHUB.m_bfKey.getBytes(), (byte)compressLevel);
				
				if(dec_out!=null){
					TCRS tcrs = Dump_TCRS(dec_out);
					return tcrs;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		return ret;
	}
	



	public byte[] Decode(byte[] inputdata, byte encrpt_level, byte[] userKey_str, byte compress_level) throws Exception {
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

	public String Encode(byte[] inputdata, byte[] userKey_str, byte compress_level, byte encrpt_level) {
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
	
	/**
	 *  输入为base64后的压缩及加密数据
	 * */
	public byte[] Encode_r(byte[] inputdata, byte[] userKey_str, byte compress_level, byte encrpt_level ){
		//1. de-Base64
		byte[] arrayOfByte = base64.decode(inputdata);
		
		
		//2. de-encrpt
		if ((m_encoder != null) && (arrayOfByte != null) && (encrpt_level != 0))
			arrayOfByte = m_encoder.encryptData(arrayOfByte, encrpt_level, userKey_str, false);
		
		//3. de-compress
		if ((m_encoder != null) && (arrayOfByte != null) && (compress_level != 0))
			arrayOfByte = m_encoder.compressData(arrayOfByte, compress_level, false);
		
		return arrayOfByte;
	}

	public TCRS Dump_TCRS(byte[] input){
		Log.e(TAG, "input.len:" + input.length);
		TCRS tcrs = new TCRS(input);
		if(tcrs!=null){
/*			Log.e(TAG, "TCRS.IsError:" + tcrs.IsError() + ", TCRS.isEOF:" + tcrs.IsEof());

			int isok = tcrs.getByte("isok");
			if(tcrs.IsEof()){
				tcrs.moveFirst();
			}*/
			
			int index = 0;
			while(!tcrs.IsEof()){
				
				int fields_num = tcrs.getFields();
				int fields_index = 0;
				while(fields_index<fields_num){
					Log.i(TAG, "index:" + index + ", fields_index:" + fields_index +", FieldType:" + tcrs.getFieldType(fields_index) + ", fieldName:" + tcrs.getFieldName(fields_index) + ", toString:" + tcrs.toString(fields_index));
					fields_index++;
				}
				
				tcrs.moveNext();
			}
			
			if(tcrs.IsEof()){
				tcrs.moveFirst();
			}
			
		}

		return tcrs;
	}

}
