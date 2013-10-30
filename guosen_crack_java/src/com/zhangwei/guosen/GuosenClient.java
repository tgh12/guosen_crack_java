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
import org.apache.http.entity.StringEntity;
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
import com.zhangwei.yougu.api.API;

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
	 * 100-1
	 * [GuosenClient]:TCRS.IsError:true, TCRS.isEOF:false
	 * [GuosenClient]:index:0, fields_index:0, FieldType:101, fieldName:ER_String, toString:没有查到相关软件信息
	 * */
	public boolean getSession(){
		Log.i(TAG, "getSession");
		sai.session = null;
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "0");
		postData.add("tc_mfuncno", "100"); 
		postData.add("tc_sfuncno", "1"); 
		
		postData.add("userKey", userKey);
		
		postData.add("loginType", sai.chk_word==null?"0":"1");
		postData.add("loginID", sai.chk_word==null?"":sai.phone);
		
		String loginPwd = null;
		if(sai.chk_word!=null){
			loginPwd = ENOUtils.str2MD5(new StringBuilder(sai.phone).append(sai.chk_word).toString());
		}else{
			loginPwd = ENOUtils.str2MD5(""); //d41d8cd98f00b204e9800998ecf8427e
		}
		postData.add("loginPwd", loginPwd);
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		postData.add("supportCompress", "18");
		postData.add("sysVer", "3.6.4.0.0.2");
		postData.add("hwID", sai.imei);
		postData.add("softName", "Andriod1.6");
		postData.add("netaddr", sai.chk_word==null?"":sai.phone);
		postData.add("conn_style", "2.460.02.0.0");
		postData.add("device_vers", "16|4.1.1");

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
	
	/**
	 * 100-2
	 * */
	public void login(){
		Log.i(TAG, "login");
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", '{' + sai.session + '}');
		postData.add("tc_mfuncno", "100"); 
		postData.add("tc_sfuncno", "2"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
	
		Build_TC_REQDATA_INIT("curver", sai.curver);
		Build_TC_REQDATA_ADD("lastver", sai.lastver);
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.00.28930.58032");//2.460.02.0.0 conn_style=2.460.00.28930.58032
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			sai.session = tcrs.toString("session");
			//bd8e89a55ce88cee80dbc353619e56aa92257b49a5dba7cce9ab2bad
			Log.e(TAG, "session got:" + sai.session);
		}
	}
	
	/**
	 * 100-3
	 * */
	public void register(){
		Log.i(TAG, "register");
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "100"); 
		postData.add("tc_sfuncno", "3"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
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
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			sai.session = tcrs.toString("session");
			//bd8e89a55ce88cee80dbc353619e56aa92257b49a5dba7cce9ab2bad
			Log.e(TAG, "session got:" + sai.session);
		}
	}
	
	/**
	 * 31000-4
		//postData.add("code", "000001.2|399001.1|100100.3|600648.2|002572.1|600708.2|600663.2|600018.2|002052.1|002024.1|603128.2|600119.2|600822.2|600751.2|000088.1|002071.1|601928.2|002596.1|600175.2|600679.2|600278.2|600597.2|600887.2|600519.2|002292.1|002416.1|600860.2|002229.1|600637.2|002153.1|600639.2|600823.2|600895.2|000049.1|000735.1|900912.2|000538.1|600535.2|300298.1|600079.2|002603.1|002038.1|600276.2|300246.1");
		//postData.add("count", "44");
		//postData.add("field", "3|4|16|20|21|11|1|");
	 * fieldName:lotsize, toString:100
	 * fieldName:code, toString:399001
	 * fieldName:name, toString:深证成指
	 * fieldName:zjcj, toString:8336.68
	 * fieldName:zhd, toString:-42.93
	 * fieldName:zdf, toString:-0.51
	 * fieldName:zrsp, toString:8379.61
	 * fieldName:exchid, toString:1
	 * fieldName:precision, toString:2
	 * fieldName:lotsize, toString:100
	 * 
	 * fieldName:code, toString:600648
	 * fieldName:name, toString:外高桥
	 * fieldName:zjcj, toString:41.42
	 * fieldName:zhd, toString:-0.16
	 * fieldName:zdf, toString:-0.38
	 * fieldName:zrsp, toString:41.58
	 * fieldName:exchid, toString:2
	 * fieldName:precision, toString:2
	 * fieldName:lotsize, toString:100
	 * 
	 * 
	 * 	postData.add("code", "600608.2");
		postData.add("field", "11|50|51|52|53|54|55|56|57|58|59|70|71|72|73|74|75|76|77|78|79");
	 * fieldName:zrsp, toString:6.51
	 * fieldName:bj1, toString:6.51
	 * fieldName:bl1, toString:3900
	 * fieldName:bj2, toString:6.50
	 * fieldName:bl2, toString:3099
	 * fieldName:bj3, toString:6.49
	 * fieldName:bl3, toString:51200
	 * fieldName:bj4, toString:6.48
	 * fieldName:bl4, toString:10200
	 * fieldName:bj5, toString:6.47
	 * fieldName:bl5, toString:28050
	 * fieldName:sj1, toString:6.53
	 * fieldName:sl1, toString:9700
	 * fieldName:sj2, toString:6.54
	 * fieldName:sl2, toString:13500
	 * fieldName:sj3, toString:6.55
	 * fieldName:sl3, toString:19500
	 * fieldName:sj4, toString:6.56
	 * fieldName:sl4, toString:17000
	 * fieldName:sj5, toString:6.57
	 * fieldName:sl5, toString:46700

	 * */
	public boolean getHanqing(){	
		Log.i(TAG, "getHanqing");
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 

		postData.add("TC_ENCRYPT", "0");
		postData.add("TC_SESSION", "{" + sai.session + "}");

		postData.add("tc_mfuncno", "31000"); 
		postData.add("tc_sfuncno", "4"); 
		
		//postData.add("code", "000001.2|399001.1|100100.3|600648.2|002572.1|600708.2|600663.2|600018.2|002052.1|002024.1|603128.2|600119.2|600822.2|600751.2|000088.1|002071.1|601928.2|002596.1|600175.2|600679.2|600278.2|600597.2|600887.2|600519.2|002292.1|002416.1|600860.2|002229.1|600637.2|002153.1|600639.2|600823.2|600895.2|000049.1|000735.1|900912.2|000538.1|600535.2|300298.1|600079.2|002603.1|002038.1|600276.2|300246.1");
		//postData.add("count", "44");
		//postData.add("field", "3|4|16|20|21|11|1|");
		
		postData.add("code", "600608.2");
		postData.add("field", "11|50|51|52|53|54|55|56|57|58|59|70|71|72|73|74|75|76|77|78|79");
		

		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		postData.add("supportCompress", "18");
		postData.add("sysVer", "3.6.4.0.0.2");
		postData.add("hwID", sai.imei);
		postData.add("softName", "Andriod1.6");
		postData.add("netaddr", sai.chk_word==null?"":sai.phone);
		postData.add("conn_style", "2.460.02.0.0");
		postData.add("device_vers", "16|4.1.1");

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
	
	/**
	 * 3750-5
	 * fieldName:org_name, toString:深圳红岭中路营业部
	 * fieldName:org_code, toString:1100
	 * fieldName:org_id, toString:20
	 * ...
	 * fieldName:org_name, toString:武汉沿江大道营业部
	 * fieldName:org_code, toString:3100
	 * fieldName:org_id, toString:36
	 * */
	public boolean getyinyebuCode(){
		Log.i(TAG, "getyinyebuCode");
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 

		postData.add("TC_ENCRYPT", "0");
		postData.add("TC_SESSION", "{" + sai.session + "}");

		postData.add("tc_mfuncno", "3750"); 
		postData.add("tc_sfuncno", "5"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		postData.add("supportCompress", "18");
		postData.add("sysVer", "3.6.4.0.0.2");
		postData.add("hwID", sai.imei);
		postData.add("softName", "Andriod1.6");
		postData.add("netaddr", sai.chk_word==null?"":sai.phone);
		postData.add("conn_style", "2.460.02.0.0");
		postData.add("device_vers", "16|4.1.1");

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
	
	/**
	 * 3500-10
	 * 
	 * fieldName:ER_String, toString:软件令牌同一天最多申请三次!
	 * 
	 * fieldName:seed, toString:83FE85BBECA6355AF43011B6D06C81F2 
	 *                          B464FBE68A9979C2FD06C9F675621CF1FE467954069BF69A49A863AFAF1472543EE892A1B700DC65
	 * fieldName:fundidlist, toString:310000110505
	 * fieldName:secuidlist, toString:0139082908,A261906525
	 * 
	 * fieldName:orgid, toString:3100
	 * fieldName:custid, toString:310000110505
	 * fieldName:seed, toString:83 FE 85 BB EC A6 35 5A F4 30 11 B6 D0 6C 81 F2
	 * fieldName:fundidlist, toString:310000110505
	 * fieldName:secuidlist, toString:0139082908,A261906525
	 * 
	 * */
	public void RequestSoftToken(){
		Log.i(TAG, "RequestSoftToken");
		sai.inputtype = "Z";
		sai.custorgid = "3100";

		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "3500"); 
		postData.add("tc_sfuncno", "10"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * 
		 *       localHashMap.put("n", localTCRS2.toString("org_name"));
		 *       localHashMap.put("c", localTCRS2.toString("org_code"));
		 * 
		 * String[] accountTypes = { "Z", "0", "1", "2", "3" };
		 * inputtype=Z&inputid=310000110505&trdpwd=111248&mobileno=18071080819&custorgid=1100&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		Build_TC_REQDATA_INIT("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("trdpwd", sai.pwd);
		Build_TC_REQDATA_ADD("mobileno", sai.phone);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);




		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			sai.seed = tcrs.toString("seed");
			//bd8e89a55ce88cee80dbc353619e56aa92257b49a5dba7cce9ab2bad
			Log.e(TAG, "session got:" + sai.session);
		}
	}
	
	
	/**
	 * 3500-6
	 * 
	 * fieldName:ER_String, toString:inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__
	 * */
	public void auth(){
		Log.e(TAG, "============ auth ============");
		
		sai.seed = "83FE85BBECA6355AF43011B6D06C81F2";
		sai.factor = 1;
		String DynCode = API.genDynCode(sai.seed, sai.factor);
		Log.i(TAG, "DynCode:" + DynCode);
		

		sai.inputtype = "Z";
		sai.custorgid = "3100";
		sai.authtype = "0";
		sai.authtype = "5";
		sai.authdata = DynCode;
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "3500"); 
		postData.add("tc_sfuncno", "6"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * inputtype=Z&custorgid=1100&inputid=310000110505&trdpwd=111245&operway=i&authtype=0&authdata=&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * 
		 * inputtype=Z&custorgid=1100&inputid=310000110505&trdpwd=111248&operway=i&authtype=5&authdata=209214&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		Build_TC_REQDATA_INIT("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("trdpwd", sai.pwd);
		Build_TC_REQDATA_ADD("operway", "i");
		Build_TC_REQDATA_ADD("authtype", sai.authtype);
		if(sai.authdata==null){
			Build_TC_REQDATA_ADD("authdata", "");
		}else{
			Build_TC_REQDATA_ADD("authdata", sai.authdata);
		}

		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	}
	
	/**
	 * 500-2
	 * 新的session也ok。
	 * 
	 * fieldName:~moneytype, toString:人民币
	 * fieldName:fundbal, toString:52.10
	 * fieldName:fundavl, toString:52.10
	 * fieldName:stkvalue, toString:12859.00
	 * fieldName:marketvalue, toString:12911.10
	 * fieldName:fundid, toString:310000110505
	 * fieldName:moneytype, toString:0
	 * fieldName:fundseq, toString:0
	 * tcrs is ok
	 * */
	public void show_asset(){
		Log.e(TAG, "============ show_asset ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "500"); 
		postData.add("tc_sfuncno", "2"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * 
		 * unlist=|moneytype|fundseq|&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";
		Build_TC_REQDATA_INIT("unlist", "|moneytype|fundseq|");
		Build_TC_REQDATA_ADD("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("orgid", sai.custorgid);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("tradenode", "9504");
		Build_TC_REQDATA_ADD("ext1", "010"); 
		Build_TC_REQDATA_ADD("userinfo", "~~~" + sai.custorgid);
		Build_TC_REQDATA_ADD("authid", "__SESSIONID__"); 
		Build_TC_REQDATA_ADD("operway", "i"); 
		Build_TC_REQDATA_ADD("fundid", sai.assetID); 
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	}
	
	/**
	 * 500-3
	 * fieldName:poststr, toString:013908290831000002572
	 * fieldName:stkcode, toString:002572
	 * fieldName:stkname, toString:索菲亚
	 * fieldName:stkbal, toString:200
	 * fieldName:stkavl, toString:200
	 * fieldName:stkbuy, toString:0
	 * fieldName:stksale, toString:0
	 * fieldName:price, toString:20.2000
	 * fieldName:profitprice, toString:20.1900
	 * fieldName:costprice, toString:20.1900
	 * fieldName:mktval, toString:4040.00
	 * fieldName:income, toString:2.00
	 * fieldName:profitrate, toString:0.05
	 * fieldName:secuid, toString:0139082908
	 * fieldName:~market, toString:深A
	 * fieldName:market, toString:0
	 * 
	 * fieldName:poststr, toString:A26190652531001600648
	 * fieldName:stkcode, toString:600648
	 * fieldName:stkname, toString:外高桥
	 * fieldName:stkbal, toString:200
	 * fieldName:stkavl, toString:200
	 * fieldName:stkbuy, toString:0
	 * fieldName:stksale, toString:0
	 * fieldName:price, toString:40.3600
	 * fieldName:profitprice, toString:52.4541
	 * fieldName:costprice, toString:52.4541
	 * fieldName:mktval, toString:8072.00
	 * fieldName:income, toString:-2418.83
	 * fieldName:profitrate, toString:-23.06
	 * fieldName:secuid, toString:A261906525
	 * fieldName:~market, toString:沪A
	 * fieldName:market, toString:1
	 * 
	 * fieldName:poststr, toString:A26190652531001600708
	 * fieldName:stkcode, toString:600708
	 * fieldName:stkname, toString:海博股份
	 * fieldName:stkbal, toString:100
	 * fieldName:stkavl, toString:100
	 * fieldName:stkbuy, toString:0
	 * fieldName:stksale, toString:0
	 * fieldName:price, toString:7.4700
	 * fieldName:profitprice, toString:8.5806
	 * fieldName:costprice, toString:8.5806
	 * fieldName:mktval, toString:747.00
	 * fieldName:income, toString:-111.06
	 * fieldName:profitrate, toString:-12.94
	 * fieldName:secuid, toString:A261906525
	 * fieldName:~market, toString:沪A
	 * fieldName:market, toString:1
	 * */
	public void show_stocks(){
		Log.e(TAG, "============ show_stocks ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "500"); 
		postData.add("tc_sfuncno", "3"); 
		
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * sign=income&position=poststr&unlist=|market|&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";
		Build_TC_REQDATA_INIT("sign", "income");
		Build_TC_REQDATA_ADD("position", "poststr");
		Build_TC_REQDATA_ADD("unlist", "|market|");
		Build_TC_REQDATA_ADD("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("orgid", sai.custorgid);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("tradenode", "9504");
		Build_TC_REQDATA_ADD("ext1", "010"); 
		Build_TC_REQDATA_ADD("userinfo", "~~~" + sai.custorgid);
		Build_TC_REQDATA_ADD("authid", "__SESSIONID__"); 
		Build_TC_REQDATA_ADD("operway", "i"); 
		Build_TC_REQDATA_ADD("fundid", sai.assetID); 
		Build_TC_REQDATA_ADD("poststr", ""); 
		Build_TC_REQDATA_ADD("qryflag", "1");
		Build_TC_REQDATA_ADD("count", "20");
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	
	}
	
	/**
	 * 500-7
	 * fieldName:ER_String, toString:-420413075没有可撤单的委托fundid = 310000110505
	 * 
	 * 500-8
	 * 500-9
	 * */
	public void weituo(int tc_sfuncno, boolean cancelflag){

		
		Log.e(TAG, "============ weituo()????? ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "500"); 
		//postData.add("tc_sfuncno", "8"); 
		postData.add("tc_sfuncno", tc_sfuncno + "");
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * 500-7
		 * inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&market=1&secuid=A261906525&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		
		/**
		 * 500-8/9
		 * sign=~bsflag&position=poststr&unlist=|market|&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		
		/**
		 * 500-8
		 * sign=~bsflag&position=poststr&unlist=|market|&cancelflag=1&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";
		if(tc_sfuncno==7){
			Build_TC_REQDATA_INIT("inputtype", sai.inputtype);
		}else{
			Build_TC_REQDATA_INIT("sign", "~bsflag");
			Build_TC_REQDATA_ADD("position", "poststr");
			Build_TC_REQDATA_ADD("unlist", "|market|");
			if(cancelflag){
				Build_TC_REQDATA_ADD("cancelflag", "1");
			}
			Build_TC_REQDATA_ADD("inputtype", sai.inputtype);
		}
			

		
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("orgid", sai.custorgid);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("tradenode", "9504");
		Build_TC_REQDATA_ADD("ext1", "010"); 
		Build_TC_REQDATA_ADD("userinfo", "~~~" + sai.custorgid);
		Build_TC_REQDATA_ADD("authid", "__SESSIONID__"); 
		Build_TC_REQDATA_ADD("operway", "i"); 
		Build_TC_REQDATA_ADD("fundid", sai.assetID); 
		
		if(tc_sfuncno==7){
			Build_TC_REQDATA_ADD("market", "1"); 
			Build_TC_REQDATA_ADD("secuid", "A261906525");
		}else{
			Build_TC_REQDATA_ADD("poststr", ""); 
			Build_TC_REQDATA_ADD("qryflag", "1");
			Build_TC_REQDATA_ADD("count", "20");
		}
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	}
	
	

	/**
	 * fieldName:ER_String, toString:错误行情结果集
	 * 
	 * fieldName:stkcode, toString:600031
	 * fieldName:stkname, toString:三一重工
	 * fieldName:market, toString:1
	 * fieldName:price, toString:7.05
	 * fieldName:num, toString:0
	 * fieldName:priceunit, toString:10
	 * fieldName:maxrisevalue, toString:7.76
	 * fieldName:maxdownvalue, toString:6.35
	 * fieldName:fundavl, toString:52.10
	 * */
	public void showStockPanKouPrice(String sockCode, boolean buy){

		
		Log.e(TAG, "============ showStockBuy1Price() ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "3500"); 
		postData.add("tc_sfuncno", "7");
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&bsflag=0B&stkcode=600607&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";

		Build_TC_REQDATA_ADD("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("orgid", sai.custorgid);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("tradenode", "9504");
		Build_TC_REQDATA_ADD("ext1", "010"); 
		Build_TC_REQDATA_ADD("userinfo", "~~~" + sai.custorgid);
		Build_TC_REQDATA_ADD("authid", "__SESSIONID__"); 
		Build_TC_REQDATA_ADD("operway", "i"); 
		Build_TC_REQDATA_ADD("fundid", sai.assetID); 
		if(buy){
			Build_TC_REQDATA_ADD("bsflag", "0B");  
		}else{
			Build_TC_REQDATA_ADD("bsflag", "0S");  
		}

		Build_TC_REQDATA_ADD("stkcode", sockCode);
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	}
	
	/**
	 * 500-6
	 * 
	 * fieldName:ER_String, toString:-420411065[-990297020]当前时间非交易时间
	 * 
	 * @param secuid 
	 * @param price 
	 * */
	public void buy_or_sell(String stock, boolean buy, String price, String num, String secuid){
		Log.e(TAG, "============ buy_or_sell() ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "500"); 
		postData.add("tc_sfuncno", "6");
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&trdpwd=111248&chkriskflag=1&price=7.54&qty=100&stkcode=600608&market=1&secuid=A261906525&bsflag=0B&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";

		Build_TC_REQDATA_ADD("inputtype", sai.inputtype);
		Build_TC_REQDATA_ADD("inputid", sai.assetID);
		Build_TC_REQDATA_ADD("orgid", sai.custorgid);
		Build_TC_REQDATA_ADD("custorgid", sai.custorgid);
		Build_TC_REQDATA_ADD("tradenode", "9504");
		Build_TC_REQDATA_ADD("ext1", "010"); 
		Build_TC_REQDATA_ADD("userinfo", "~~~" + sai.custorgid);
		Build_TC_REQDATA_ADD("authid", "__SESSIONID__"); 
		Build_TC_REQDATA_ADD("operway", "i"); 
		Build_TC_REQDATA_ADD("fundid", sai.assetID); 
		
		Build_TC_REQDATA_ADD("trdpwd", sai.pwd); 
		Build_TC_REQDATA_ADD("chkriskflag", "1"); 
		Build_TC_REQDATA_ADD("price", price); 
		Build_TC_REQDATA_ADD("qty", num); 
		Build_TC_REQDATA_ADD("stkcode", stock); 
		Build_TC_REQDATA_ADD("market", "1"); 
		Build_TC_REQDATA_ADD("secuid", secuid); 
		if(buy){
			Build_TC_REQDATA_ADD("bsflag", "0B");  
		}else{
			Build_TC_REQDATA_ADD("bsflag", "0S");  
		}
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
		}
	}
	
	/**
	 * fieldName:classid, toString:broker_id
	 * fieldName:classname, toString:金太阳顾问
	 * fieldName:name, toString:周武
	 * fieldName:telno, toString:13392555588
	 * fieldName:addr, toString:
	 * fieldName:classid, toString:org_id
	 * fieldName:classname, toString:所属营业部
	 * fieldName:name, toString:武汉沿江大道营业部
	 * fieldName:telno, toString:95536
	 * fieldName:addr, toString:武汉沿江大道159号时代广场1座16、17层
	 * fieldName:classid, toString:custserv_id
	 * fieldName:classname, toString:客服热线
	 * fieldName:name, toString:全国统一
	 * fieldName:telno, toString:95536
	 * fieldName:addr, toString:
	 * fieldName:classid, toString:fundid
	 * fieldName:classname, toString:资金帐号
	 * fieldName:name, toString:3100******05
	 * fieldName:telno, toString:提示：关联资金帐号将获取更多服务，一个资金帐号只能关联一个手机号码。
	 * fieldName:addr, toString:
	 * */
	public void m3750_s1(){
		//
		Log.e(TAG, "============ m3750_s1() ============");
		
		//List<NameValuePair> postData = new ArrayList<NameValuePair>();  
		PostEntity postData = new PostEntity();
		postData.add("tc_service", "300");
		postData.add("tc_isunicode", "1"); 
		postData.add("TC_ENCRYPT", "36");
		postData.add("TC_SESSION", "{" + sai.session + "}");
		postData.add("tc_mfuncno", "3750"); 
		postData.add("tc_sfuncno", "1");
		postData.add("tc_packageid", String.valueOf(tc_packageid));
		
		/**
		 * mobile=18071080819&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.02.0.0&device_vers=16|4.1.2&
		 * */
		sai.inputtype = "C";
		sai.custorgid = "3100";

		Build_TC_REQDATA_ADD("mobile", sai.phone);
		
		Build_TC_REQDATA_ADD("supportCompress", "18");
		Build_TC_REQDATA_ADD("sysVer", sai.curver);
		Build_TC_REQDATA_ADD("hwID", sai.imei);
		Build_TC_REQDATA_ADD("softName", "Andriod1.6");
		Build_TC_REQDATA_ADD("netaddr", sai.chk_word==null?"":sai.phone);
		Build_TC_REQDATA_ADD("conn_style", "2.460.02.0.0");
		Build_TC_REQDATA_ADD("device_vers", "16|4.1.1");
		String TC_REQDATA = Build_TC_REQDATA_GET();
		int TC_REQDATA_LEN = Build_TC_REQDATA_GET_NUM();
		postData.add("TC_REQLENGTH", "" + TC_REQDATA_LEN);
		postData.add("TC_REQDATA", TC_REQDATA);
		
		tc_packageid++;
		
		Log.i(TAG, "postData:" + postData.toString());
		TCRS tcrs = Post("goldsunhq1.guosen.cn:8002", "/", postData);
		if(tcrs!=null && !tcrs.IsError()){
			Log.i(TAG, "tcrs is ok");
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
		sb = sb.append("&");
	}

	
	private void Build_TC_REQDATA_ADD(String name, String value) {
		// TODO Auto-generated method stub
		if(sb==null){
			sb = new StringBuilder();
		}
		
		sb = sb.append(name).append("=").append(value).append("&");
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


	





	
	public void get_cur_price(){
		
	}

	private TCRS Post(String host, String url, PostEntity postData/*List<NameValuePair> postData*/) {
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
			//httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.ASCII));
			httppost.setEntity(new StringEntity(postData.toString()));

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
