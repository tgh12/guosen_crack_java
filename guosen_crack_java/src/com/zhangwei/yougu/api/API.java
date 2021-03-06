package com.zhangwei.yougu.api;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sourceforge.blowfishj.BinConverter;
import net.sourceforge.blowfishj.BlowfishCBC;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.dc.DCAlgorithm;
import com.eno.ENOCoder.CRC;
//import com.eno.ENOCoder.CRC;
import com.eno.ENOCoder.Hex;
import com.eno.ENOCoder.RSA;
import com.eno.ENOCoder.base64;
import com.eno.kjava.system.ENODataEncoder;
import com.eno.utils.ENOUtils;
import com.eno.utils.TCRS;
import com.guosen.android.system.SystemHUB;
import com.guosen.android.utils.Utils;
import com.zhangwei.guosen.GuosenClient;
import com.zhangwei.guosen.SaveAccountInfo;
import com.zhangwei.yougu.androidconvert.Log;
import com.zhangwei.yougu.storage.SDCardStorageManager;

import droidbox.apimonitor.Helper;

/**
 * 
Lcom/guosen/android/system/SysParameter;->setRecord
Lcom/guosen/android/system/SysParameter;->getRecord
Lcom/guosen/android/system/SystemHUB;->getKey
Lcom/eno/kjava/net/ENOCommServ;->setCommand
Lcom/eno/kjava/net/ENOCommServ;->ReceiveServerResponse
Lcom/eno/kjava/net/ENOCommServ;->getResultType
Lcom/eno/kjava/net/ENOCommServ;->Decode
Lcom/eno/kjava/net/ENOCommServ;->Encode
Lcom/eno/kjava/net/ENOCommServ;->processData
Lcom/eno/utils/TCRS;->toString
Lcom/eno/utils/TCRS;->getFieldType
Lcom/eno/utils/TCRS;->moveNext
Lcom/eno/utils/TCRS;->getShortIn
Lcom/eno/enotree/ENOTreeNode
Lcom/eno/enotree/ENOTree;->InsertObject
Lcom/eno/utils/TCRS;->setRecordOffs
Lcom/eno/utils/TCRS;->IsEof
Lcom/eno/ENOCoder/BlowfishCBC;-><init>
Lcom/eno/ENOCoder/BlowfishCBC;->cleanUp
Lcom/eno/ENOCoder/BlowfishCBC;->decrypt
Lcom/eno/ENOCoder/BlowfishCBC;->encrypt
Lcom/eno/ENOCoder/BlowfishCBC;->getCBCIV
Lcom/eno/ENOCoder/BlowfishCBC;->setCBCIV
Lcom/eno/ENOCoder/BlowfishEasy;-><init>
Lcom/eno/ENOCoder/BlowfishEasy;->decodeData
Lcom/eno/ENOCoder/BlowfishEasy;->destroy
Lcom/eno/ENOCoder/BlowfishEasy;->encodeData
Lcom/eno/ENOCoder/BlowfishECB;-><init>
Lcom/eno/ENOCoder/BlowfishECB;->cleanUp
Lcom/eno/ENOCoder/BlowfishECB;->encryptPrv
Lcom/eno/ENOCoder/BlowfishECB;->initialize
Lcom/eno/ENOCoder/BlowfishECB;->weakKeyCheck
Lcom/eno/ENOCoder/CRC;-><init>
Lcom/eno/ENOCoder/CRC;->calcCRC
Lcom/eno/ENOCoder/CRC;->make_crc_table
Lcom/eno/ENOCoder/CRC;->update_crc

 * */

public class API {

	
	public static final String imei = "A000004502832C";//"862620027046913";
	private static final String m_phonenum = "18071080819";
	private static final String TAG = "API";
	private static final String curver = "3.6.2.0.0.1";
	
	private static String lastver = "3.6.4.1.1.1";
	private static String session = null;
	private static String userKey_str;
	private static String userKey;
	private static String m_pwd = null;//"877451";//"628012"; Lcom/guosen/android/system/SysParameter;->setRecord(I=20 | Ljava/lang/String;=877451)V
	
	
	private static ENODataEncoder m_encoder;
	private static RSA localRSA;


	public static byte[] Decode(byte[] inputdata, byte encrpt_level, byte[] userKey_str, byte compress_level) throws Exception {
		if(m_encoder==null){
			m_encoder = new ENODataEncoder();
		}
		
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
		
		if(m_encoder==null){
			m_encoder = new ENODataEncoder();
		}
		
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
	public static byte[] Encode_r(byte[] inputdata, byte[] userKey_str, byte compress_level, byte encrpt_level ){
		if(m_encoder==null){
			m_encoder = new ENODataEncoder();
		}
		
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

	public static String genDynCode(String seed, int factor){
		//byte[] keyBytes = new byte[] { 17, 34, 79, 88, -120, 16, 64, 56, 40, 37, 121, 81, -53, -35, 85, 102, 119, 41, 116, -104, 48, 64, 54, -30 };
	    //String ret = new DCAlgorithm().generateOTP(new String(Utils.decryptMode(keyBytes, seed)), factor, 6);
	    String ret = new DCAlgorithm().generateOTP(seed, factor, 6);
	    SaveAccountInfo.getInstance().factor = factor+1;
	    
	    return ret;
	}
	
	public static void Dump_TCRS(byte[] input){
		TCRS tcrs = new TCRS(input);
		Log.e(TAG, "TCRS.IsError:" + tcrs.IsError() + ", TCRS.isEOF:" + tcrs.IsEof());
		int isok = tcrs.getByte("isok");
		if(tcrs.IsEof()){
			tcrs.moveFirst();
		}
		
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
	}
	
	public static void main(String args[]){

		//load info from disk
		SaveAccountInfo sai = SaveAccountInfo.getInstance();
		sai.pwd = "111248";
		sai.chk_word = "112081";
		sai.session = null;
		sai.secuidlist= "0139082908,A261906525";
		sai.persist();
		
		String enc_r_in = "QQEAANib93voq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q90noRW4jzWvzLl7B9iGROY2EGia1L5qSo7HqwQASQVhna0ky/DVHGlong/SMbvm7H9xro6rZEhfYosXUzfgOT2uSEWL6a2L4Jsm4n4UbU6OR+RzNEvQz9NeqqeVNjSwcBewn/1OL+NAyAL1aRCzQhsaSnvK7iRrJvJ6DAd91mt64gFca7PYeDJsDbAcx6pF93XnOsxZr48qiSG1c966qwbzneyLQG6bV3Iqps8JjFFnoB9+6iN8pyLxD4CQd7s5g51JMLhVfHEaieTCOF7uvRWJRuOCHBCFdMTqPR+SWXmrlKfD1Rq7J0C2G1oqLxQnxRV2Ruh9jwyxKC072v52NIvDt3z8AUWYvGxEzq5GpAGnmCRN53HnHv+xosppWxhlsVSliPUbk3OXp";
		//open or close
		//enc_r_in = null;
		
		//init
		if(enc_r_in==null){
			GuosenClient gc = new GuosenClient(sai);
			if(sai.session==null){
				gc.getSession();
			}

			if(sai.chk_word==null){
				gc.register();
			}
			
			//gc.login();
			//gc.getyinyebuCode();
			//gc.RequestSoftToken();
			//gc.auth();
			//gc.getHanqing();
			//gc.show_asset();
			//gc.show_stocks();

			//gc.showStockPanKouPrice("600031", true);
			//Log.i(TAG, "DDDDDDDDDDDDDDDDDDDD");
			//gc.showStockPanKouPrice("600031", false);
			//---- 
			//gc.buy_or_sell("600708", false, "8.04", "100", "1", "A261906525");
			
			gc.buy_or_sell("002572", false, "23", "100", "0", "0139082908");
			
			//gc.m3750_s1();
			
			//gc.weituo(7, false); //撤单
			//gc.weituo(8, false); //显示所有的委托合同
			//gc.weituo(8, true); //显示可以撤单的委托合同
			//gc.weituo(9, false);
		}else{
			Log.i(TAG, "m_bfKey:" + sai.m_bfKey);
			SystemHUB.m_bfKey = sai.m_bfKey;
			byte[] enc_out_1 = Encode_r(enc_r_in.getBytes(), sai.m_bfKey.getBytes(), (byte)0, (byte)36);
			
			Log.e(TAG, "enc_r_in(dec):" + new String(enc_out_1));
			String seed = "83FE85BBECA6355AF43011B6D06C81F2";
			int factor = 1;
			String DynCode = genDynCode(seed, factor);
			Log.e(TAG, "DynCode:" + DynCode);
			factor++;
			String DynCode1 = genDynCode(seed, factor);
			Log.e(TAG, "DynCode1:" + DynCode1);
			factor++;
			String DynCode2 = genDynCode(seed, factor);
			Log.e(TAG, "DynCode2:" + DynCode2);
			factor=1;
			String DynCode3 = genDynCode(seed, factor);
			Log.e(TAG, "DynCode3:" + DynCode3);
		}



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
	 * conn_style=2.460.00.28731.11821
	 * &tc_service=300
	 * &tc_isunicode=1
	 * &tc_mfuncno=100
	 * &tc_sfuncno=1
	 * &TC_ENCRYPT=0
	 * &userKey=8b52ff3cf53e8a69cef0bdf8f0b7c57169ccb817afe250842ea105800ff7574f
	 * &loginType=1
	 * &loginID=18071080819
	 * &loginPwd=4c9b4f6f21952d6d3c2aa9561dfd7c47
	 * &supportCompress=18
	 * &sysVer=3.6.2.0.0.1
	 * &hwID=A000004502832C
	 * &softName=Andriod1.6
	 * &packageid=1
	 * &netaddr=18071080819*/
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
		if (localRSA==null && localRSA.loadPublicKey2()) {
			//userKey_str = "0dnG7+epL6KYuyFU";
			byte[] userKey_array = localRSA.encode(userKey_str.getBytes());
			userKey = Hex.encode(userKey_array);
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
	 * conn_style=2.460.00.28731.11821
	 * &tc_service=300
	 * &tc_isunicode=1
	 * &tc_mfuncno=100
	 * &tc_sfuncno=1
	 * &TC_ENCRYPT=0 //定义返回数据的加密级别，0表示没有加密
	 * &userKey=f56de9df2039f06135e2d20eddc503c1756bfc7e0f21108be4f8ebb56d718354 //客户端随机生成的字符串，经过rsa加密处理后
	 * &loginType=0 //0未注册， 1已注册
	 * &loginID=    //绑定的手机号
	 * &loginPwd=d41d8cd98f00b204e9800998ecf8427e //手机号+验证码进行md5的值
	 * &supportCompress=18 //定义返回数据的压缩级别，0表示没有加密
	 * &sysVer=3.6.2.0.0.1 //当前版本
	 * &hwID=862620027046913 //imei
	 * &softName=Andriod1.6
	 * &packageid=1
	 * &netaddr=  //绑定的手机号
	 * 
	 * 
	 * [API]:index:0, fields_index:0, FieldType:9, fieldName:session, toString:0a460a59e50ede10bd0483dacaa2b0b089f721278cb8fba635006384
	 * [API]:index:0, fields_index:1, FieldType:1, fieldName:isok, toString:0
	 * [API]:index:0, fields_index:2, FieldType:101, fieldName:msg, toString:验证码错误!
	 * [API]:index:0, fields_index:3, FieldType:9, fieldName:prd_soft_versn, toString:3.6.4.1.1.1
	 * [API]:index:0, fields_index:4, FieldType:101, fieldName:prd_updateinfo, toString:
	 * [API]:index:0, fields_index:5, FieldType:9, fieldName:servurl, toString:218.18.103.48
	 * [API]:index:0, fields_index:6, FieldType:101, fieldName:reginfo, toString:提示：请先发送短信DB A至95536注册；无手机卡或无短信功能的设备，请先用手机发送短信，再输入该手机号码登录(免费使用)。
	 * [API]:index:0, fields_index:7, FieldType:9, fieldName:popmsg(跑马灯开关), toString:1
	 * [API]:index:0, fields_index:8, FieldType:9, fieldName:refresh(刷新开关), toString:1
	 * [API]:index:0, fields_index:9, FieldType:101, fieldName:help, toString:使用金太阳软件，需先发送短信DB A至95536，2分钟后在用户注册界面输入手机号码登录。1、发送注册短信，再输入手机号码登录；无手机卡或无短信功能的设备使用金太阳，请先用手机发送短信，再输入该手机号码登录；2、若报“请先发送短信”，请稍候几分钟后再登录，并确认注册界面输入的手机号码正确无误。或可尝试发送短信DB A至以下备用通道，稍候再输入手机号码登录：...
	 * [API]:index:0, fields_index:10, FieldType:101, fieldName:multiacc(多账户开关), toString:8
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
		localRSA = new RSA();
		if (localRSA==null && localRSA.loadPublicKey2()) {
			byte[] userKey_array = localRSA.encode(userKey_str.getBytes());
			userKey = Hex.encode(userKey_array);
			Log.i("API", userKey);
			postData.add(new BasicNameValuePair("userKey", userKey));
		} else {
			Log.e("API", "loadPublicKey2 failed");
		}
	      
		
		postData.add(new BasicNameValuePair("loginType", "1"));  //0 未注册， 1已注册
		postData.add(new BasicNameValuePair("TC_ENCRYPT", "0")); 
		postData.add(new BasicNameValuePair("loginID", m_phonenum));  //手机号
		String loginPwd = null;
		if(m_pwd!=null){
			loginPwd = ENOUtils.str2MD5(new StringBuilder(m_phonenum).append(m_pwd).toString());
		}else{
			loginPwd = ENOUtils.str2MD5(""); //d41d8cd98f00b204e9800998ecf8427e
		}
		
		postData.add(new BasicNameValuePair("loginPwd", loginPwd));  //d41d8cd98f00b204e9800998ecf8427e
		postData.add(new BasicNameValuePair("supportCompress", "18")); 
		postData.add(new BasicNameValuePair("sysVer", "3.6.2.0.0.1")); 
		postData.add(new BasicNameValuePair("hwID", imei)); //imei
		postData.add(new BasicNameValuePair("softName", "Andriod1.6")); 
		postData.add(new BasicNameValuePair("packageid", "1")); 
		postData.add(new BasicNameValuePair("netaddr", "")); 
		
		
		byte[] out = RequestHelper.getInstance().Post("goldsunhq1.guosen.cn:8002", "/", postData);
		byte[] out2  = Decode(out, (byte)0, null, (byte)18);
		
		//Dump_TCRS(out2);
		
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
	 * conn_style=2.460.00.28731.11821
	 * &tc_service=300
	 * &tc_isunicode=1
	 * &tc_mfuncno=100
	 * &tc_sfuncno=3
	 * &TC_ENCRYPT=36
	 * &TC_SESSION={0dbe16b024004977a8a937412e01f1e4591fba94a4906f4ff8dd3a99}
	 * &TC_REQLENGTH=118
	 * &TC_REQDATA=dgAAAHEzt3QsArfwdsiC9eUUvZUmjqkBDLuzk/vWqEQyJwvF0Gat9l/cPl5JW09DiOVU4p5f/N0iFIk2Nm1Pfeur54f1BPgqVtAPqgZvgquSIP18wLIWAKCcyBEtT0HWMoveCxV83PIAmUrYI+qt9KTne+xDFNvLzBaeZAy4UtU= 
	 *
	 * 分两次进行：
	 *1. "mobile=18071080819&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&packageid=3&netaddr=";
	 *2. &curver=3.6.2.0.0.1&lastver=3.6.4.1.1.1&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&packageid=4&netaddr=18071080819
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
		//1. "mobile=18071080819&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&packageid=3&netaddr=";
		//2. &curver=3.6.2.0.0.1&lastver=3.6.4.1.1.1&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&packageid=4&netaddr=18071080819
		
		//String TC_REQ_PLAN_DATA = "mobile=" + m_phonenum + "&curver=" + curver + "&lastver=" + lastver; //=&curver=3.6.2.0.0.1&lastver=3.6.4.1.1.1
		//TC_REQ_PLAN_DATA = TC_REQ_PLAN_DATA + "&supportCompress=18"  + "&sysVer=" + curver + "&hwID=" + imei + "&softName=Andriod1.6&packageid=1&netaddr=";
		String TC_REQ_PLAN_DATA = "mobile=18071080819&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=Andriod1.6&packageid=3&netaddr=";
		postData.add(new BasicNameValuePair("TC_REQLENGTH", TC_REQ_PLAN_DATA.getBytes().length + ""));
		
		//byte[] encdata = m_encoder.encryptData(TC_REQ_PLAN_DATA.getBytes(), (byte)36, userKey_str.getBytes(), true);
		String TC_REQDATA = Encode(TC_REQ_PLAN_DATA.getBytes(), userKey_str.getBytes(), (byte)0, (byte)36);
		
		postData.add(new BasicNameValuePair("TC_REQDATA", TC_REQDATA));
		Log.e(TAG, "TC_REQDATA:" + TC_REQDATA);
		

		
		
		//byte[] out = RequestHelper.getInstance().Post2("218.18.103.48:8002", "/", postData);
		byte[] out = {71,0,0,0,91,-52,105,-51,103,0,0,0,120,1,-53,-49,102,64,1,-116,64,-98,107,80,124,112,73,81,102,94,58,84,-122,19,72,115,-64,85,125,6,41,97,-104,1,-27,47,40,-47,-29,-3,-13,59,-106,-29,3,80,84,-112,79,-84,2,44,9,-108,99,-121,98,51,115,11,75,51,67,6,0,111,-82,12,-125,50};
		//byte[] out = {14,23,0,0,114,-12,60,-77,-128,66,0,0,120,1,-59,-100,11,124,84,-43,-99,-57,15,10,4,121,-55,67,-47,-126,-81,18,-119,60,-61,-68,-14,2,34,-14,16,-83,-64,16,18,32,2,-102,97,94,-103,-103,100,102,-110,-52,-99,73,38,99,54,98,23,91,118,-7,-92,33,22,-16,85,-83,81,-60,-57,126,-20,-38,-18,-42,79,125,84,-59,119,-85,22,34,104,-15,-127,-113,10,-21,110,-83,21,93,5,-41,87,-9,-9,-65,-25,127,-17,-100,25,-110,33,97,-37,109,100,-14,-69,-13,-65,-33,-13,63,-1,123,-18,121,-33,27,-75,86,-83,48,-30,-113,38,4,-1,-28,65,-67,17,23,-103,92,33,-97,52,14,-122,-48,-121,126,-14,-126,-49,109,-111,71,-67,-1,54,-46,54,114,98,35,45,105,113,44,119,106,35,109,-44,29,-15,-109,119,-6,53,-116,63,-93,86,29,105,37,91,95,63,70,-38,112,40,90,79,-52,105,-8,80,-38,49,-8,124,-69,-19,110,-81,-91,-6,-110,106,28,-10,-6,99,-92,13,69,-36,1,-54,-45,72,123,54,-114,-65,93,-77,-89,-94,-41,68,108,52,-45,106,-63,-112,15,-119,79,-123,-3,20,124,46,-32,-13,-71,-92,57,-28,111,105,-120,-7,-4,49,-122,-116,-76,-109,115,37,-30,115,59,-95,-113,14,21,-30,47,-8,33,-45,112,-2,80,-38,-103,-8,-48,53,8,-31,19,-113,49,83,-124,111,-60,-52,-57,103,-118,126,-58,96,-42,-119,-57,-103,-119,48,-45,2,-51,-57,-25,22,124,-92,-97,10,-15,4,51,119,49,-13,0,51,123,77,102,-119,120,-118,-103,-41,-103,121,-97,-11,35,-109,-103,39,118,51,115,-108,-49,-119,65,66,12,-63,-15,16,-88,-52,-53,38,-98,102,102,52,108,20,-13,-39,80,-70,-105,23,-104,-52,84,81,-125,-118,74,-41,62,-115,-49,89,-95,-45,-63,-84,-25,-17,-125,-60,121,-30,73,-10,-29,-123,-115,-4,68,-8,92,2,42,-13,58,67,-4,-102,-103,118,102,54,67,-87,13,116,66,-15,79,-1,60,-61,-52,86,102,110,-127,82,60,119,42,-52,-77,-52,-36,-59,-52,3,80,-86,-13,63,87,-104,82,-114,-7,23,-52,60,6,-91,107,-33,-83,48,-49,-63,15,-59,-12,20,51,47,65,41,-98,125,-52,-32,80,60,-49,-52,126,102,-34,-122,-110,-97,-9,21,-26,5,102,14,-63,54,18,-25,-114,64,97,18,71,21,-26,55,-52,28,-125,-115,-54,103,16,42,46,-7,25,10,-123,73,-1,-7,45,51,121,-80,17,51,22,90,6,-67,66,97,94,100,102,41,108,-108,87,53,51,91,21,-26,98,92,4,93,87,23,51,-73,65,-49,5,-5,-120,-62,-68,-124,-13,116,-49,30,-123,-115,-14,122,22,-6,19,-88,27,13,-61,-120,-25,101,102,60,-80,17,19,-122,-42,66,95,81,-104,-33,49,-77,-113,-103,-125,-48,-55,96,-2,-94,48,123,-63,80,93,-92,-101,68,49,-113,-124,-46,-41,113,80,-103,87,-123,-24,97,102,60,108,116,-65,39,65,-15,79,-100,111,50,75,-60,30,102,46,96,102,42,51,51,77,102,-98,-88,-62,-75,83,94,-77,96,-93,-104,75,-96,84,-50,-13,76,-58,38,-50,100,-90,-100,-103,-53,-104,89,102,50,83,-59,116,102,-106,-61,70,-15,-84,-122,-30,-97,88,107,50,104,-33,-52,-84,99,-58,-61,76,-83,-62,92,-63,76,0,54,-118,-89,9,74,-15,52,43,-52,43,-120,-105,-38,115,11,51,-41,65,79,7,-77,85,97,-10,49,-45,-59,-52,-83,-52,-36,-81,48,-5,-103,-7,23,102,126,9,-67,10,126,-58,34,67,-29,-98,-66,-54,-52,56,-40,40,-98,115,-96,116,125,23,42,-52,107,-52,76,97,102,54,116,33,-104,38,-123,-7,61,51,49,102,-38,-96,-85,-64,-20,85,-104,3,-52,-12,48,-13,38,116,53,24,11,-20,70,60,-81,51,99,-123,82,60,115,-95,-60,108,85,-104,55,-104,-23,98,-26,86,40,-59,124,-105,-62,-68,-55,-52,78,102,126,-58,-6,43,-123,121,11,-57,-44,-65,60,-52,-25,-88,-17,-101,6,63,127,-126,26,-15,28,-60,49,49,31,-31,67,-15,28,-125,18,115,1,-18,-93,-63,-84,-60,49,-11,65,-33,-123,-46,-104,103,-123,-38,-96,94,-109,25,41,42,-103,-15,65,-55,79,20,74,101,-8,-96,-55,-116,16,111,-61,7,-7,-7,57,51,-113,66,41,-81,67,38,51,92,-68,-61,-52,97,102,62,-122,-38,-63,76,70,1,-56,120,-14,-60,-69,-52,-28,-61,70,121,-51,-126,18,-77,-63,100,-122,-118,-9,-104,113,51,83,-49,-52,-19,38,51,68,-4,-127,-103,-97,50,115,31,51,111,-103,-52,96,-15,62,51,7,-103,-7,-128,-103,-119,-24,-28,101,60,-89,-118,67,-52,76,-126,-115,-30,-103,2,-91,120,-82,52,-103,83,-60,97,102,-42,50,-29,101,102,-121,-55,12,18,35,113,-83,-44,-26,111,-124,-115,-6,-115,-69,-96,11,-96,-121,76,70,-120,81,-52,28,-122,13,-88,-8,-112,-11,19,-123,25,-51,-52,-89,-80,81,60,95,67,-99,-48,43,-16,-59,-72,-89,-89,51,-77,20,-74,-47,56,-73,22,74,125,-17,13,10,51,-122,-103,31,51,-45,-51,-52,27,10,51,-106,-103,55,97,-93,-104,63,-128,46,-126,78,31,-111,-50,107,28,51,51,96,35,-90,4,-70,24,122,-115,-62,-116,103,-90,13,54,-118,-25,-97,-95,37,-48,-35,10,115,6,51,79,49,-77,-105,-103,60,56,53,-82,-53,-59,-52,48,-40,-56,-49,4,40,77,-78,106,21,-26,63,80,112,-44,71,7,96,67,-72,-94,9,58,5,-70,-61,100,124,-30,3,102,110,100,-90,-101,-103,-25,76,102,-99,-8,79,102,-98,103,-90,7,-118,-22,33,94,55,-103,10,-15,95,-52,-68,-63,-52,33,102,-2,100,50,75,-60,31,-103,-7,-120,-103,99,-52,-4,-59,100,112,-97,-103,17,-93,100,-52,-61,-95,20,-13,108,-88,113,-19,-44,-82,-23,-70,44,-52,-52,-127,82,60,11,21,-122,-38,57,49,-117,-104,89,-50,-52,106,-123,-7,51,51,107,-104,-39,-64,76,80,97,62,6,67,99,88,-120,25,13,74,-29,-32,118,-123,57,-62,-52,14,102,-18,-128,82,95,-9,-71,-62,124,-62,-52,81,102,-24,-90,-47,125,47,-127,26,-41,-11,41,51,-91,-80,-47,-3,90,-56,76,-85,-62,-4,55,51,41,102,54,65,29,96,127,-91,48,-97,49,-13,48,51,79,51,115,76,97,62,7,67,99,-58,23,-80,-95,-8,-59,96,12,76,8,77,-116,-123,-54,120,124,-30,40,51,-29,96,35,-26,60,102,102,-104,-52,58,-67,31,37,63,51,97,-93,-104,-117,-95,-45,-96,46,-109,-87,16,95,-80,-97,13,-52,-44,65,103,-128,-39,110,50,66,-4,15,24,26,15,118,48,115,7,116,42,-104,23,20,-26,75,102,126,-61,-52,62,40,-35,-117,-49,21,-26,43,102,-114,50,67,29,121,1,-104,2,-88,81,-50,95,51,115,17,108,20,-77,21,74,-9,34,-88,48,-33,48,19,98,70,-125,-26,-125,-39,-95,48,-27,104,-125,20,-13,-115,-52,116,67,-55,-49,1,-123,89,-63,-52,-21,-52,-68,15,-91,-104,-121,-116,77,-57,83,-63,-52,80,-40,112,90,76,-128,82,-99,-97,-85,48,-89,-126,-95,118,56,15,54,-118,121,9,51,-11,10,51,-104,-103,48,51,-51,-52,-36,-86,48,67,-104,-7,9,51,-69,-104,121,81,97,-122,50,-13,18,51,-81,49,-13,-107,-62,-28,49,-13,53,51,67,-57,-31,-34,35,-82,121,80,-93,-100,-121,-127,-95,126,-95,28,54,-118,-7,50,102,-2,-63,100,124,-30,52,102,-38,-103,-39,12,45,5,-5,-92,-55,-84,19,-61,-103,-39,13,27,-86,-85,-40,3,-91,114,30,58,-34,-56,-85,66,-116,96,38,15,54,98,-50,-124,18,-77,-52,100,-106,-120,111,17,11,-11,29,-53,97,-93,114,-66,26,90,0,-3,71,-109,-63,-4,-106,-103,77,-52,108,-125,-38,-63,60,-81,48,-76,-104,32,63,47,48,115,-128,-103,17,103,24,-15,64,-103,25,9,27,-27,117,14,-108,-4,-84,80,-104,83,-104,-87,-128,-115,98,-82,-127,82,60,-101,20,102,2,24,-102,-41,94,15,27,-107,97,39,-108,-6,-106,67,38,-29,19,103,49,115,-104,-103,-113,-103,89,124,-90,17,-49,58,113,54,51,-105,-62,-122,-48,-59,82,40,-51,33,-17,49,-103,10,-15,29,102,-18,-123,-115,-14,-6,5,-108,-6,-43,71,76,102,-119,-72,-100,-103,71,97,-93,62,-31,121,40,-115,113,35,38,24,121,-51,19,19,-103,25,9,27,-7,-103,0,93,13,-83,55,25,-84,3,-104,9,51,-45,-52,-52,-117,10,115,14,51,47,49,-13,26,-108,-30,121,91,97,-50,101,-26,29,-40,-88,12,63,-126,22,65,39,-97,101,-60,-125,126,-117,-103,124,-40,-120,-79,66,-119,-87,85,-104,-13,-103,9,-64,70,49,55,65,41,-81,86,-123,41,97,38,5,27,-7,-39,12,-75,65,31,81,-104,2,48,84,-114,-113,-62,70,126,-98,-123,46,-121,-114,-61,70,-120,-47,46,46,98,102,60,108,-60,-100,11,37,-90,78,97,-90,50,83,-49,76,2,122,49,-104,127,83,-104,105,-52,-4,59,51,-113,67,-99,96,70,126,39,-99,-41,18,102,70,-63,70,49,79,-126,82,-1,-68,88,97,46,4,67,101,125,41,108,84,87,87,67,43,-96,119,43,-52,20,102,118,49,-13,16,-108,-14,26,60,49,-99,87,49,24,42,-57,33,-80,-47,117,-99,14,-67,8,90,108,50,-125,-12,-7,54,49,37,-52,44,-128,-82,0,-77,-43,100,-28,-4,-101,-104,46,-40,40,-98,110,40,-75,-99,-3,10,51,25,-25,-119,121,21,54,-70,-82,-9,-95,20,-113,117,82,58,-98,124,102,108,-80,17,115,9,-108,-104,31,41,-52,12,48,-76,38,-20,-124,-115,-22,-13,-83,-48,11,-95,79,40,-52,76,102,-98,-124,-115,-82,-21,-73,80,-22,-97,63,86,-104,89,-52,28,-127,-115,-4,124,13,-91,-6,51,-8,-100,116,60,118,102,-122,-64,70,109,112,4,-108,-104,-15,10,-29,96,-26,12,-40,40,-81,-13,-96,37,-48,-43,10,83,-60,-52,26,102,54,64,41,-98,-21,21,-26,123,-52,-4,0,54,-118,-25,6,40,-75,-45,-41,20,-90,16,12,-59,-3,123,-40,40,-98,-125,80,26,-29,104,83,-63,-88,-85,-77,-103,25,4,27,49,121,80,98,-90,43,-116,-123,-103,25,-52,88,-95,20,-49,26,-123,-95,-75,20,-27,85,-51,-52,-43,-48,98,48,63,86,24,27,51,-37,96,-93,107,-65,-99,-103,-3,10,83,6,-122,-10,-128,94,-123,77,-65,-17,-48,81,8,-10,-56,121,-23,-104,-25,48,-13,9,108,-28,-25,43,-88,5,58,-29,-4,52,51,-105,-103,-103,-80,17,83,12,37,-90,86,97,-26,49,19,96,-90,-119,-103,110,-123,-71,4,-52,124,124,-18,-124,-115,-30,121,16,-118,101,-120,120,88,97,22,48,-13,8,108,-108,-41,51,-52,-68,-84,48,11,-103,-7,29,108,-28,-25,45,-24,82,-24,69,23,-92,99,94,-60,-52,84,-40,-56,-113,13,74,121,-51,-123,26,-9,107,49,51,-13,96,35,102,9,-76,0,26,-122,26,-52,-91,-52,68,96,35,-90,5,74,-13,-79,59,-95,6,-77,20,12,-19,113,-36,5,27,-35,-9,-5,-96,27,-96,-89,125,55,-51,44,99,102,56,108,-60,-116,-127,86,65,-67,10,-77,-100,25,31,51,117,-52,-4,90,97,-100,-52,60,14,27,-122,118,-79,-105,-103,124,76,-108,6,-31,59,-3,-84,2,67,123,64,23,-62,70,49,23,-78,-106,42,-52,106,102,-54,96,-93,120,-26,67,-87,30,-6,20,102,13,51,126,-40,-56,79,3,51,-37,20,-90,-102,-103,-19,-80,-111,-97,91,-95,84,87,123,20,-26,74,102,94,-127,-115,-4,-68,-59,-52,56,52,14,35,-26,-75,-52,-116,-121,-115,-104,115,-95,-44,-106,87,41,-52,58,102,86,51,-29,-126,82,31,-11,-128,-62,-84,103,-26,103,-80,81,-35,120,12,-70,14,58,1,29,-107,-111,-41,85,-52,-100,5,27,49,83,-96,84,127,26,21,-26,106,102,-102,-104,-71,22,74,-3,-4,30,-123,-39,-117,-29,59,-41,-105,120,70,5,-70,43,49,35,-10,-121,27,-25,-57,-67,-82,72,109,34,-22,-115,54,-108,23,89,28,5,-8,-86,-15,87,-69,85,-44,7,107,91,10,27,-93,1,-79,-72,-29,95,-73,-52,114,62,-19,-62,-19,111,8,-121,67,90,92,77,103,-49,74,88,44,82,-55,84,82,79,-9,117,-5,-80,85,55,120,71,-83,66,-88,-55,96,83,70,34,-85,-59,98,-55,-56,78,71,-12,84,99,61,-101,-102,-69,55,6,91,5,28,-99,32,-107,67,71,-12,84,83,-85,-97,104,-33,-23,28,95,43,68,60,-26,-10,-7,-11,103,54,-87,100,93,-85,126,114,104,124,114,-57,120,-25,28,84,-32,-80,55,-43,-92,-1,-46,-19,-17,-83,104,-40,-16,-95,-21,-119,118,4,-104,-48,-126,17,45,32,-110,-55,122,25,-5,-98,-21,123,-70,6,-19,-64,21,-41,5,-26,7,-29,-15,-58,57,-77,103,123,-62,13,-127,-62,64,-94,65,-13,71,11,-67,13,-111,66,111,116,118,4,-89,103,-121,-94,62,127,114,54,-111,-70,-45,-32,-54,83,-29,121,-34,70,-65,89,90,-94,-87,41,-91,-23,-89,30,-33,18,-67,-74,-80,-26,62,-108,-66,-73,33,90,27,10,8,-15,101,117,79,23,21,47,106,107,-83,-85,-82,46,-107,68,-9,-21,-20,-34,40,77,-75,86,116,91,-41,6,-86,-85,-105,109,126,118,-125,16,-98,-6,96,19,-72,-50,-93,-50,-49,106,31,-124,-5,68,-93,-49,29,-121,46,-120,-20,-23,122,-89,-67,27,62,-62,-31,120,-99,16,-38,-54,31,57,123,-70,-90,-81,-75,35,124,-73,-89,33,17,-57,86,101,85,-93,-97,50,71,-58,-102,-106,-62,-108,100,101,126,-57,-111,43,71,-93,-76,-94,-2,22,13,-63,-60,-3,-47,-20,-69,106,85,-17,79,73,-127,59,28,114,107,-27,84,107,92,45,-55,56,-30,-4,-48,-43,-30,45,-36,-2,-39,-106,-109,-11,82,95,-89,69,-48,-16,-12,64,-25,58,19,55,-31,1,-124,59,20,77,-60,-62,70,121,-73,-72,27,-51,-30,-114,10,113,127,-8,-109,-120,44,-41,84,32,-91,-27,-84,77,-114,2,111,67,34,26,47,47,-125,-6,-4,-27,-10,-78,50,-69,-59,82,104,109,67,-91,-77,88,-84,-123,54,121,80,-58,7,86,-117,97,-79,25,7,118,-29,84,49,14,-112,-100,82,89,-27,-127,-61,56,-80,27,7,69,-58,65,113,-95,85,-120,-101,111,-98,29,29,96,-104,-59,50,76,-76,9,-4,43,-76,-73,-23,7,86,-29,-64,102,28,-40,-115,3,-121,126,96,117,16,-116,-82,-62,-71,44,33,51,-44,-22,50,-53,37,-85,101,-38,1,11,-41,-59,-2,-45,93,84,-21,48,103,-81,-43,-22,80,93,-20,-119,89,29,-73,-83,71,-75,9,120,-24,118,76,91,59,13,-11,-77,46,30,-58,-128,-89,-11,116,-35,126,37,125,77,-42,-91,-16,-67,-29,-121,-117,59,58,126,88,-118,-17,-79,84,12,85,-79,-89,-117,26,22,-32,-80,-105,-66,-100,-26,124,24,-75,-79,78,67,-83,74,-43,126,-77,-15,-106,-11,-37,-31,48,21,-11,-95,-2,53,58,-3,43,-9,-81,-67,18,-33,61,-119,86,-51,29,-10,-49,95,-66,124,-39,-62,114,84,-17,53,85,95,-10,118,6,5,73,-4,78,-33,106,52,-38,-106,-72,-41,-89,-34,-17,-94,-52,-66,-93,-76,64,11,5,-94,-27,-19,30,-83,54,-20,14,20,52,54,104,-95,120,-88,33,90,-114,-125,-72,22,-113,21,36,-94,-44,103,-107,-73,69,-36,-79,122,127,-68,-83,-64,-21,-114,122,-3,97,98,-53,-111,-51,-47,109,-69,-73,-124,59,-57,-44,-92,-101,43,-6,-69,-98,-82,-61,-127,63,-96,65,-23,125,-119,43,85,-25,77,-26,-56,-33,-106, -50,-95,33,-22,111,-115,-73,54,-6,-37,-48,-117,-6,52,127,83,27,102,-72,27,-65,113,-86,-50,2,-75,57,-99,-39,-27,-59,-124,-94,-24,97,-4,39,-68,22,12,-100,-82,-61,126,89,-78,122,-84,77,9,127,-84,53,71,-84,3,44,43,-23,127,-116,103,39,-58,-83,126,-7,47,27,-40,-67,-64,100,26,99,-60,68,39,13,72,60,-74,-96,-114,110,-3,114,-91,-46,-107,-5,-109,33,-44,32,-86,-78,-33,15,-3,114,101,-97,61,86,86,101,-73,97,52,-90,52,123,-38,-121,34,117,31,-67,92,86,26,59,-22,99,79,-41,-115,-41,28,104,46,13,62,-19,106,-12,71,81,-83,-5,-105,-78,-72,32,28,-15,69,-54,3,-127,-108,79,-120,51,-110,-51,-87,-69,61,87,-84,-110,-119,-77,-57,-53,-84,60,75,-48,-10,-93,113,57,-20,-106,-105,22,80,118,84,-127,66,-66,-14,-42,58,15,6,8,26,-94,-114,-19,120,-68,-77,-65,-111,88,81,-91,-61,43,-65,-88,26,72,89,89,109,28,-66,22,-88,-57,-14,-70,-93,-69,50,-49,-101,23,-20,119,-114,-100,-38,-37,-110,66,-5,-98,-21,-100,-78,105,83,96,12,-114,-6,87,114,70,-34,-87,-42,0,74,-69,123,-29,-52,-64,-66,-10,77,-72,-18,-127,-91,14,120,-22,-112,102,-82,-13,-70,127,-22,-34,120,14,-22,-22,-64,82,35,111,-36,54,74,-7,-120,-109,-58,-19,-127,-91,14,-8,98,26,38,-109,-107,119,-74,-121,60,-35,27,7,-102,-70,54,88,-113,-56,101,-73,123,-113,115,55,42,13,-70,94,111,-42,-52,44,99,-86,84,-28,72,-9,55,-78,71,115,39,-30,13,-2,36,-122,112,13,-35,30,-70,28,-22,109,117,63,-111,24,102,-36,-34,-27,-107,47,-76,123,112,99,-31,56,-107,-54,-47,55,20,-107,100,59,-114,-72,-29,-34,32,13,-95,109,-57,-11,68,-23,-98,71,-23,-36,78,-44,-5,20,-107,-11,-31,-121,122,-104,1,-8,41,-74,-12,-30,103,122,-64,82,121,126,96,32,94,-84,-26,-11,6,27,-62,62,26,16,-38,-12,-9,77,-76,104,3,74,81,-34,-109,-16,-54,126,54,100,-121,85,109,-55,14,-101,-38,-108,29,-42,82,52,-53,31,84,125,90,69,93,-75,12,49,24,-46,78,-86,-72,-56,75,-70,-72,-6,-31,-91,-41,-62,-6,-84,-74,-52,-1,81,-5,-70,-118,-121,98,-125,48,-77,12,4,-110,-87,84,-58,-4,47,107,-120,45,-50,85,84,-67,-36,11,-102,-61,-53,73,6,-90,-75,45,-66,-70,-70,-52,106,-105,-79,-46,-80,57,100,79,-3,-115,-13,54,-52,1,-63,-41,-6,115,-114,-112,14,20,37,-7,-106,67,-124,57,106,-48,64,34,75,-42,52,-23,17,-72,74,-48,23,-23,-109,-21,122,116,105,-44,76,-27,68,-104,50,10,106,-71,-38,-125,-67,76,102,-44,-117,-41,27,-68,-53,54,-77,-41,70,76,-123,41,-93,103,-74,-36,-125,117,-124,-98,81,35,-49,-86,30,-12,83,127,-112,12,104,1,-11,-14,-77,23,76,-104,-107,-91,107,69,-65,6,-39,1,15,-30,-53,-69,-114,85,-11,-69,-42,-39,-20,-67,-36,-48,89,-50,-10,107,-6,-17,-95,-88,23,15,84,24,114,89,67,-35,107,111,-93,-94,50,16,90,75,-44,-59,-121,21,43,-12,-21,93,61,85,-78,60,-5,-109,-68,44,35,121,41,-51,-14,-106,109,14,119,-18,-34,66,-109,87,-113,59,90,-113,114,-114,106,-75,-12,106,25,-39,-62,-99,116,62,-5,12,-39,-42,-82,-96,74,73,41,60,-18,48,-51,27,-43,59,-103,-35,72,48,-27,-96,-103,-92,44,39,35,-105,19,53,114,-101,77,14,61,-117,59,110,-58,-48,-45,-81,-5,79,45,-26,-49,43,-54,-4,-49,-83,31,-124,-54,-105,12,68,34,57,-94,-78,-62,63,21,28,-43,1,-84,-6,79,-36,-21,-40,80,33,-115,78,-90,95,9,-84,-67,-11,-58,-117,2,-69,-73,44,-37,-68,8,99,91,83,42,-103,-71,-10,-49,42,54,43,110,15,-51,-72,23,68,28,88,99,-12,35,-64,82,107,47,-43,43,-36,-39,-67,-15,-19,53,-123,120,22,-111,10,4,51,87,-121,-39,-7,9,65,93,-57,37,30,106,-75,-10,-124,-109,22,48,-83,88,9,105,57,83,-15,116,114,-25,-106,89,-32,-11,86,30,-53,104,-43,89,-103,-40,-71,83,-93,-59,21,-13,-103,-67,64,54,-113,-121,22,20,-113,-77,-29,-19,53,6,31,-52,113,87,-19,104,18,-60,-121,59,61,-104,54,-23,-15,-92,114,-14,40,-28,-12,-126,10,-4,9,-42,84,-114,-34,-102,112,122,-83,65,25,-74,-26,-18,-91,123,115,64,17,23,116,80,61,-20,-121,3,-69,57,58,-69,-67,94,45,-18,-114,39,52,-116,-54,3,30,68,29,-67,-43,78,-93,126,-9,-69,75,-61,120,-98,-67,-82,-60,-122,-87,-105,46,-120,94,-67,-107,126,-12,-37,16,-15,-86,-73,45,-69,-77,-89,-90,43,-109,-47,36,93,73,-106,93,-104,-103,67,100,-58,-36,34,-19,-29,-102,-74,45,-43,-3,-10,97,83,-25,39,105,31,52,3,-24,-73,15,123,-74,15,74,121,-119,-25,-76,0,21,4,-41,-61,100,-58,90,-71,-73,2,-96,125,4,57,71,-91,125,4,87,44,21,-119,101,-107,89,-58,-124,23,75,-92,-104,-33,-121,53,-67,-81,-36,82,16,49,-106,-40,56,-106,102,125,33,111,41,-112,-85,-1,114,-53,66,-38,-89,40,-83,-92,125,5,-71,79,-15,87,-15,111,53,-3,-93,-14,-2,63,-20,74,28,-24,44,-83,60,-48,121,49,102,-19,-26,108,-26,89,-49,-63,21,-69,87,-115,-61,96,-59,-41,-27,-13,52,6,115,-50,95,114,110,37,-92,39,-16,-3,28,117,6,60,-21,-96,-18,-30,111,-71,117,48,108,-43,-82,118,89,115,-51,50,122,111,-59,-44,-60,-30,-114,123,120,45,-95,-41,-82,100,107,-54,-101,-35,-72,50,-85,-105,67,-83,84,-76,-30,-98,-102,24,-18,-84,-40,33,93,-77,15,45,-103,-37,71,70,-59,-108,-69,26,89,-95,-27,5,55,53,-45,92,67,113,-21,-15,-91,50,70,-58,-84,49,-95,68,-83,-8,-76,-86,-93,42,97,-20,-66,-71,34,77,-103,-101,-29,-39,-119,-47,-36,115,-75,26,95,40,-26,-9,-58,105,3,-20,65,45,-61,111,-14,-81,-25,-73,-89,75,86,97,89,-122,117,-63,122,-75,-107,103,-57,91,-44,71,-68,-76,69,104,-76,5,-35,17,46,60,-105,-93,19,108,-96,121,18,90,72,-10,24,66,-4,-15,-70,123,-11,70,38,23,4,-70,-13,-108,47,88,-97,115,81,80,-118,-127,119,122,64,-85,-39,-25,76,87,-65,126,77,-38,74,-6,-68,33,-57,15,44,-97,94,-99,-103,65,63,38,69,37,-24,-102,123,-65,-33,-57,-69,31,-24,-2,-26,113,-101,-104,39,-102,-45,14,-32,30,-56,94,45,-85,97,-96,103,-53,-35,50,74,-107,-117,61,-119,57,65,-55,-15,-5,16,-118,67,62,-44,107,73,122,42,44,-37,109,63,-18,68,47,-45,-31,62,-99,-53,29,116,-34,-9,9,123,51,-9,125,48,116,102,116,84,54,108,79,82,20,3,89,40,-53,12,-78,-106,-54,34,115,77,108,76,-122,-6,53,-7,-18,117,34,52,-48,85,-75,-116,-118,-74,54,-27,118,89,111,-85,65,-27,89,100,-82,-83,-107,-94,34,7,-42,43,114,90,-114,-25,16,39,-100,-108,-53,9,57,-56,19,78,-57,7,50,21,-105,51,-126,-65,-41,-76,-102,30,17,-46,19,32,-71,43,-31,-87,111,-52,88,9,-40,-77,-97,-75,-46,-45,49,-7,84,-52,106,47,-93,-121,90,120,-110,-92,75,-111,-108,98,41,37,82,74,-91,-108,-23,98,-77,72,-79,74,-79,89,-28,-109,52,122,74,-90,63,90,43,-27,3,91,-111,113,96,-100,-78,-107,-80,-59,110,60,100,-77,27,15,-39,-20,14,-29,-108,1,-37,77,-40,112,104,55,30,-42,57,-116,-121,117,14,-29,57,-98,3,14,11,106,67,-2,-80,-81,-36,-47,102,-77,-74,89,-117,-37,16,-105,21,-113,-21,-38,48,-70,125,63,-12,-71,-117,-54,7,-117,120,60,62,61,81,69,43,46,-48,48,42,54,-60,-54,47,-61,38,-14,-78,-51,19,-99,39,-111,-12,-14,-75,120,-35,-92,122,78,-43,73,36,93,-116,41,-21,-104,-102,18,15,117,-48,-14,-103,20,-98,-90,-31,-15,83,10,-113,108,-123,-77,112,123,-38,-42,-86,-37,-88,55,48,120,-84,98,-63,122,-109,-87,96,42,107,-73,45,-93,19,41,77,47,-85,-52,-87,116,-101,39,-107,10,122,82,109,-76,101,-95,-17,-10,-86,79,-59,-56,107,107,-118,34,72,-17,-121,-24,-74,96,-21,-33,102,51,68,-72,38,110,-107,-49,13,93,-11,-71,-10,36,-117,-114,95,-40,-103,43,-58,-72,91,-65,14,-51,27,-12,-5,18,-2,-80,95,-18,-19,-42,-58,66,90,125,-77,59,-100,-16,99,33,73,27,40,-100,77,50,-41,74,-34,90,-118,23,35,-78,-57,-48,-20,-116,-36,94,122,-30,24,-95,-99,114,-68,59,-86,-17,-74,-45,-125,79,87,-50,-3,-10,-127,123,-2,-12,106,42,-100,77,-51,-103,-113,87,113,-45,115,76,-84,6,-98,75,81,-94,98,125,-5,53,-69,-80,-101,73,-113,125,93,-104,-92,-30,93,12,-39,27,-22,6,45,-93,123,-55,26,-92,104,99,98,-96,-125,-44,-33,-73,-9,-4,-65,15,127,-101,110,-38,-43,-2,90,21,45,-42,-12,-14,105,109,109,-54,-104,-96,98,28,-49,120,-105,-62,86,32,-85,39,30,-9,53,-44,-22,93,113,-103,-35,106,67,45,-109,-81,88,-104,62,-16,-74,48,59,-106,5,100,-98,-56,-3,52,-36,-122,-83,3,-67,-6,31,-25,63,-26,-41,-4,-79,102,63,-67,53,82,62,-53,90,-48,20,107,53,102,-62,52,3,-90,-39,-16,83,85,-93,-47,-15,-55,-69,-100,74,-7,-68,57,31,37,-37,112,85,-67,103,-108,-27,-7,-32,-118,-75,-119,-37,110,-38,-123,71,-20,-20,-39,19,-10,103,60,-48,63,-103,18,34,31,120,105,1,127,-97,77,-17,-46,-96,127,-44,127,78,-63,111,-40,92,100,115,-43,-5,81,113,-27,123,116,-61,-12,-77,120,-73,108,59,31,-12,33,102,90,-67,-101,-56,-4,27,-21,121,21,125,36,98,51,-67,-81,71,63,67,-8,-13,2,-12,101,124,-14,-16,-39,-113,-49,76,124,-24,-91,46,-3,60,20,-101,-49,-94,-123,-65,-73,65,113,57,-30,93,40,-15,31,64,-79,85,36,38,-31,-126,-24,-5,100,-24,85,-80,-75,-97,42,-65,95,15,-91,-9,-50,58,-95,-108,-17,77,-48,49,-48,123,-95,-93,-95,15,65,-121,66,31,-125,-58,-3,-34,-96,-96,-123,-8,-98,-82,124,-68,-116,-68,0,127,-36,-76,88,-44,-104,71,-12,-84,55,31,47,-108,45,22,87,-64,42,85,-38,42,-15,-54,-33,-9,96,-109,42,109,-43,-8,86,35,-24,-73,-4,-66,16,-81,-8,46,-61,127,53,-8,83,57,121,-12,110,-43,29,-107,-7,-16,70,57,-43,-80,74,118,1,82,-83,-64,127,78,-40,-45,-57,-14,-36,34,68,69,121,73,-107,-74,42,-99,-86,65,20,68,-109,95,122,71,-120,94,-30,16,86,-47,-122,-9,-77,107,-60,39,-111,-5,-61,-7,-8,70,71,-89,-44,-28,-21,-74,-5,-61,116,100,-121,109,118,-76,123,35,-82,31,-49,-45,-11,-81,-76,81,69,-80,21,-81,-125,-42,-120,73,-107,52,-68,-45,119,-117,-2,125,1,-98,0,-53,111,-28,110,-95,-2,-51,-127,35,-38,-38,-54,-57,75,-27,-120,13,123,-38,-7,120,93,-80,70,-52,-87,40,-59,53,82,74,-54,38,-3,82,-115,12,-128,-33,-58,105,-120,-59,17,15,57,-69,55,-14,82,-57,-91,122,76,-12,109,-101,115,104,-27,79,-73,-35,27,57,127,43,-31,116,21,-77,-67,116,-42,-118,-65,-114,-88,-47,111,84,79,-41,109,55,-47,-71,50,124,-33,-65,118,127,61,93,13,-123,-68,-89,-117,-114,-83,122,8,34,-48,-45,41,119,87,46,95,26,15,-31,-1,8,-80,0,37,94,3,-84,8,47,34,90,-60,44,124,74,-32,-62,-126,87,122,43,-8,76,9,50,-112,103,-54,-32,2,-85,9,17,106,-92,25,-111,30,35,69,98,21,-123,-8,-108,32,95,82,-6,77,-82,-26,-24,-55,44,122,-92,-14,117,45,122,-125,48,-72,-58,10,-41,116,61,-67,-89,-76,35,85,95,41,109,72,73,-41,93,4,-90,16,71,69,122,-50,118,61,71,27,-82,-75,-81,116,118,78,71,-111,90,-12,-108,54,-4,-90,56,109,104,45,125,71,-22,64,58,-70,81,-108,-114,114,52,126,-45,-19,43,68,-54,-66,35,45,-30,-108,86,61,45,-91,44,-46,-45,-45,-67,-111,126,28,125,70,91,-116,-76,84,97,-109,-12,98,33,21,20,-35,68,-108,57,94,-74,-117,-95,123,-127,39,-118,121,22,60,-107,-23,39,-16,-34,-94,59,102,-98,-91,43,20,-30,127,1,-100,-65,102,-21,12,-71} ;
		Log.e(TAG, "out: len:" +out.length);
		//ENODataEncoder m_encoder = new ENODataEncoder();
		//byte[] out2 = m_encoder.compressData(out, (byte) 18, false);
		//byte[] out2  = Decode(out, (byte)36, null, (byte)18);
		byte[] out2 = {111,107,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,69,82,95,83,116,114,105,110,103,0,0,0,0,0,0,0,9,0,0,0,8,0,0,0,0,0,0,0,0,0,-13,1,0,0,0,0,-104,0,0,0,0,0,0,0,-96,116,46,13,-4,-5,93,8,-16,1,0,0,17,14,22,120,1,0,0,0,0,0,0,0,7,0,0,0,7,0,0,0,54,55,56,57,54,49,0};
		Dump_TCRS(out2);
	}
	
	  public static byte[] encodeData(BlowfishCBC bfc, byte[] paramArrayOfByte)
	  {
	    int i = paramArrayOfByte.length;
	    int j = CRC.calcCRC(paramArrayOfByte);
	    byte[] arrayOfByte = new byte[16 + (i & 0xFFFFFFF8)];
	    ENOUtils.integer2Bytes(arrayOfByte, 0, i);
	    ENOUtils.integer2Bytes(arrayOfByte, 4, j);
	    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 8, paramArrayOfByte.length);
	    int k = (byte)(arrayOfByte.length - i);

	    for (int l = i + 8; ; l++)
	    {
	      if (l >= arrayOfByte.length)
	      {
	        bfc.setCBCIV(0L);
	        bfc.encrypt(arrayOfByte, 8, arrayOfByte, 8, arrayOfByte.length - 8);
	        return arrayOfByte;
	      }
	      arrayOfByte[l] = (byte) k;
	    }
	  }
	  
	  
	  public static byte[] decodeData(BlowfishCBC bfc, byte[]testKey, byte[] paramArrayOfByte)
	  {
	    int i = ENOUtils.bytes2Integer(paramArrayOfByte, 0);
	    int j = ENOUtils.bytes2Integer(paramArrayOfByte, 4);
/*	    int i = BinConverter.byteArrayToInt(paramArrayOfByte, 0);
	    int j = BinConverter.byteArrayToInt(paramArrayOfByte, 4);*/
	    Log.e(TAG, "decodeData - i:" + i + ", j:" + j + ", paramArrayOfByte.len:" + paramArrayOfByte.length);
	    if(testKey!=null){
		    bfc.setCBCIV(testKey, 0);
	    }else{
	    	bfc.setCBCIV(0);
	    }

	    bfc.decrypt(paramArrayOfByte, 8, paramArrayOfByte, 8, paramArrayOfByte.length - 8);
		Helper.log("paramArrayOfByte:" + Helper.toString(paramArrayOfByte));

	    if ((i > 0) && (i <= paramArrayOfByte.length - 8) && (CRC.calcCRC(paramArrayOfByte, 8, i) == j))
	    {
	      byte[] arrayOfByte = new byte[i];
	      System.arraycopy(paramArrayOfByte, 8, arrayOfByte, 0, i);
	      return arrayOfByte;
	    }
	    return null;
	  }
	  
	  /**
	   * Net:
	   * 1. 出错的登录：
	   * POST / HTTP/1.1
	   * User-Agent: ENO KJava Client
	   * Content-Language: CN
	   * Content-Type: application/x-www-form-urlencoded
	   * Content-Length: 490
	   * Host: 121.15.129.184:8002
	   * Connection: Keep-Alive
	   * Accept-Encoding: gzip
	   * conn_style=2.460.00.28731.11821
	   * &tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={2f8540cae3b3d6bf86c0e79c0a2939c5391a3d0caea6cc2ac8f58a2f}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=1
	   * &TC_REQLENGTH=203
	   * &TC_REQDATA=ywAAAPzpJrjoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxGhavVHTVAtQfNO2Yt/xgtHYgbpZ2ZANecct1Mv622CoVS0gVeOsDSJOQN6ImR9oG2dDCbwAIuBg+2bHFkOhDxtnjcjGEcBdiE2hk0xhj5qI3HFq8Fb23oF38MNNueSgpnQtHcueCTb1DboTiumsJQiquI9BgTcYVkfUOlIicSEqInyWbkAjr42icjlNLqWUs6qPkOwH2iHI
	   * <TC_REQDATA dec:inputtype=Z&custorgid=3100&inputid=310000110505&trdpwd=111245&operway=i&authtype=5&authdata=565159&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=test&packageid=2&netaddr=18071080819&>
	   * 565159 是动态码 ： 
	   * Lcom/guosen/android/system/SystemHUB;->genDynCode(Ljava/lang/String;=310000110505 | [B={-76,100,-5,-26,-118,-103,121,-62,-3,6,-55,-10,117,98,28,-15,-2,70,121,84,6,-101,-10,-102,73,-88,99,-81,-81,20,114,84,62,-24,-110,-95,-73,0,-36,101} | I=5 | Z=false)Ljava/lang/String;=565159
	   * 
	   * TCRS:
	   * [API]:index:0, fields_index:0, FieldType:101, fieldName:ER_String, toString:-420301230客户密码错
	   * 
	   * 2. 正确的登录：
	   * User-Agent: ENO KJava Client
	   * Content-Language: CN
	   * Content-Type: application/x-www-form-urlencoded
	   * Content-Length: 490
	   * Host: 121.15.129.184:8002
	   * Connection: Keep-Alive
	   * Accept-Encoding: gzip
	   * conn_style=2.460.00.28731.11821
	   * &tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={2f8540cae3b3d6bf86c0e79c0a2939c5391a3d0caea6cc2ac8f58a2f}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=1
	   * &TC_REQLENGTH=203
	   * &TC_REQDATA=ywAAAFTFaP3oq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oaJdU9le7h/IU787G5qH3YVYg+823+AwUJAlbKtvEilO3sCisVCOX/kXSd507qF8h+40B4ronms1l7GXEt6tDxS3Pd3G2tumaBtcc1OArh4e/ODRc0cSlKgWbQkkXaM7sERlsWT/+73InGoX+KCjrsd3cQduPSV2ZnuZJto18mrn
	   * <TC_REQDATA dec:inputtype=Z&custorgid=3100&inputid=310000110505&trdpwd=1*2*8&operway=i&authtype=5&authdata=819123&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=test&packageid=3&netaddr=18071080819&>
	   * 
	   * TCRS:
	   * [API]:index:0, fields_index:0, FieldType:9, fieldName:ER_String, toString:inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__
	   * 
	   * 3. 查询资金
	   * User-Agent: ENO KJava Client
	   * Content-Language: CN
	   * Content-Type: application/x-www-form-urlencoded
	   * Content-Length: 566
	   * Host: 121.15.129.184:8002
	   * Connection: Keep-Alive
	   * Accept-Encoding: gzip
	   * conn_style=2.460.00.28731.11821
	   * &tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={2f8540cae3b3d6bf86c0e79c0a2939c5391a3d0caea6cc2ac8f58a2f}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=2
	   * &TC_REQLENGTH=262
	   * &TC_REQDATA=BgEAALMjZvrP2Idwk6QRI5jQGZg/IP0y9Oy10IOBhEI8JVtdSIcDbTS+/8+T2B3fyMHVygpt+SufXyX/hUvouFkT2+lqP50JrmUMm8lBXR2eLNaVgMXh5k6eri2kxe8223VvOJ1ctVgLK4q3Qonik3dtV/QyWahCR5vLCzPy4dD4siN2giF6so+j3ODdpKqfSJdk7DfSz0mqH/pp1n6jfx9sEId3U0snMu90EEzmrlub7Gb1giUDmlwkWoGlaph+sbj+7k99QmFq2rKmqPARnGq1VF7boTgcEDTS1Dx6hFUweguruN+HeXnsE+LE3VK8KPxWbH6iS0BCjzCRwm++LGMUg2EsyU9JG1lHRYzYHr8=
	   * <TC_REQDATA dec:unlist=|moneytype|fundseq|&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=test&packageid=4&netaddr=18071080819&>
	   * 
	   * TCRS:
	   * [API]:index:0, fields_index:0, FieldType:101, fieldName:~moneytype, toString:人民币
	   * [API]:index:0, fields_index:1, FieldType:9, fieldName:fundbal, toString:52.10
	   * [API]:index:0, fields_index:2, FieldType:9, fieldName:fundavl, toString:52.10
	   * [API]:index:0, fields_index:3, FieldType:9, fieldName:stkvalue, toString:14675.00
	   * [API]:index:0, fields_index:4, FieldType:9, fieldName:marketvalue, toString:14727.10
	   * [API]:index:0, fields_index:5, FieldType:9, fieldName:fundid, toString:310000110505
	   * [API]:index:0, fields_index:6, FieldType:9, fieldName:moneytype, toString:0
	   * [API]:index:0, fields_index:7, FieldType:9, fieldName:fundseq, toString:0
	   * 
	   * 4. 在没有关闭app的情况下，不再登录，直接查询资金
	   * POST / HTTP/1.1
	   * User-Agent: ENO KJava Client
	   * Content-Language: CN
	   * Content-Type: application/x-www-form-urlencoded
	   * Host: 121.15.129.184:8002
	   * Connection: Keep-Alive
	   * Accept-Encoding: gzip
	   * Content-Length: 566
	   * conn_style=2.460.00.28731.11821
	   * &tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={37a8197115bd5ee574865a82799097b788b2865f64f78eb42a91d415}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=2
	   * &TC_REQLENGTH=262
	   * &TC_REQDATA=BgEAADPqNtTP2Idwk6QRI5jQGZg/IP0y9Oy10IOBhEI8JVtdSIcDbTS+/8+T2B3fyMHVygpt+SufXyX/hUvouFkT2+lqP50JrmUMm8lBXR2eLNaVgMXh5k6eri2kxe8223VvOJ1ctVgLK4q3Qonik3dtV/QyWahCR5vLCzPy4dD4siN2giF6so+j3ODdpKqfSJdk7DfSz0mqH/pp1n6jfx9sEId3U0snMu90EEzmrlub7Gb1giUDmlwkWoGlaph+sbj+7k99QmFq2rKmqPARnGq1VF7boTgcEDTS1Dx6hFUweguruN+HeXnsE+LE3VK8KPxWbH6iS0BfIcrW/Eco1S3YXvsPYuErZ8IhuPUZM4M=
	   * <TC_REQDATA dec:unlist=|moneytype|fundseq|&inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=test&packageid=6&netaddr=18071080819&>
	   * 
	   * TCRS:
	   * [API]:index:0, fields_index:0, FieldType:101, fieldName:~moneytype, toString:人民币
	   * [API]:index:0, fields_index:1, FieldType:9, fieldName:fundbal, toString:52.10
	   * [API]:index:0, fields_index:2, FieldType:9, fieldName:fundavl, toString:52.10
	   * [API]:index:0, fields_index:3, FieldType:9, fieldName:stkvalue, toString:14675.00
	   * [API]:index:0, fields_index:4, FieldType:9, fieldName:marketvalue, toString:14727.10
	   * [API]:index:0, fields_index:5, FieldType:9, fieldName:fundid, toString:310000110505
	   * [API]:index:0, fields_index:6, FieldType:9, fieldName:moneytype, toString:0
	   * [API]:index:0, fields_index:7, FieldType:9, fieldName:fundseq, toString:0
	   * */
	  public static void queryZiJin(){
			  
	  }
	  
	  /**
	   * 1. 登录
	   * POST / HTTP/1.1
	   * User-Agent: ENO KJava Client
	   * Content-Language: CN
	   * Content-Type: application/x-www-form-urlencoded
	   * Content-Length: 490
	   * Host: 218.18.103.48:8002
	   * Connection: Keep-Alive
	   * Accept-Encoding: gzip
	   * conn_style=2.460.00.28731.11821
	   * &tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={882e22a323409a3d4c350ae980e176ec7ba6f465164f96f5305bd1da}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=1
	   * &TC_REQLENGTH=203
	   * &TC_REQDATA=ywAAAHvwaEHoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oaJdU9le7h/IesZyZ2uz5PZO1QwktkBWUZCHMrRcLSaS2/1xgaCwcWdxk+OMAvEBP//s2HpSnbOmQvB8iYNDSyAQjuz48yD0hLM6s7ODFn/83iLIlqnw37sJRJXGDNM74AObhZ0i4aPwlCetLI0mhaMoVUhISHDkCVq5HZIR5/hU
	   * <TC_REQDATA dec:inputtype=Z&custorgid=3100&inputid=310000110505&trdpwd=1*2*8&operway=i&authtype=5&authdata=404135&supportCompress=18&sysVer=3.6.2.0.0.1&hwID=A000004502832C&softName=test&packageid=2&netaddr=18071080819&>
	   * 
	   * TCRS:
	   * [API]:index:0, fields_index:0, FieldType:9, fieldName:ER_String, toString:inputtype=C&inputid=310000110505&orgid=3100&custorgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__
	   * 
	   * 	
	   * */
	  public static void queryStock(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: goldsunhq1.guosen.cn:8002
	   * Accept: image/png
	   * Content-Length: 383
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &tc_mfuncno=100
	   * &tc_sfuncno=1
	   * &userKey=26916c8fba83671051f79856733cf0de1ad412a1b5c207d75916a05f4bd4ce29
	   * &loginType=1
	   * &loginID=13986014801
	   * &loginPwd=c57357641cdf171a74407ccfd2b243d1
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=1
	   * &netaddr=13986014801
	   * &conn_style=2.460.00.28930.58032
	   * &device_vers=16|4.1.1
	   * 
	   * 
	   * OST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 453
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={9f44e38418164d31b34c8dbc7e0851e7b0f745dd8a711b1db00104af}
	   * &tc_mfuncno=100
	   * &tc_sfuncno=2
	   * &tc_packageid=2
	   * &TC_REQLENGTH=190
	   * &TC_REQDATA=vgAAABr5aqqwtE8ZMn18yqVjMc2766iLfO004f6Ym4d3w/vjVcK4HrZSjXxVGYLLgyfTlc8XwbH3JmDWHRCU6v6CfK65qOL5C0OlL/Kjs1r8bwYLhyXl4NWHeGu8HWbn6FemeXLOKrhirudVa/vXisOVe5C8aZOWuLoAidPKrqgrJ2NjUYMk4vqAFWUcc9XH4VrPLRxIjykXMsHlCYFVh97WHbNaDhKRKlw9mwCW6Mxt06MV1gmqtYGjQWcA7JuZsu66xtcdrKs=
	   * <TC_REQDATA dec: curver=3.6.2.1.1.1&lastver=3.6.4.1.1.2&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=13986014801&conn_style=2.460.00.28930.58032&device_vers=16|4.1.1&>
	   * 
	   * */
	  public static void login1_3_6(){
		  //String enc_r_in = "vgAAABr5aqqwtE8ZMn18yqVjMc2766iLfO004f6Ym4d3w/vjVcK4HrZSjXxVGYLLgyfTlc8XwbH3JmDWHRCU6v6CfK65qOL5C0OlL/Kjs1r8bwYLhyXl4NWHeGu8HWbn6FemeXLOKrhirudVa/vXisOVe5C8aZOWuLoAidPKrqgrJ2NjUYMk4vqAFWUcc9XH4VrPLRxIjykXMsHlCYFVh97WHbNaDhKRKlw9mwCW6Mxt06MV1gmqtYGjQWcA7JuZsu66xtcdrKs=";
		  //String enc_r_in = "nwAAAPB+2HXibOQXPa80JhVJ613PTURtj6RIOnHhK1YOARyX6mPoOWcYH4OoE6gaSsmzzm/fExhkCUmLVkQqolSX9M3fesHD5juPP1zCFlAULDLuE3I+1YWSmIcr2AUuvG0U+mz/NglNKh9HvVwz0SQwGZ0i472fWKEY7IBqTBQnF2AXkkmrzB4Fz/XYRBBzGY7wZuW5WjKK3I9mC3YZowBPnNypJHXa";
		  //String enc_r_in = "9AAAAKLLHTnoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oZanBbYc4xMGTkJQyv6e1abkZsKV9v/vFXyBaxV3HPTi3lmSCRTtmPhRyREuPC9nYG1aRevkFc3FE6Lkgxg/odZPY6RqPSgIF9Qr7BO8+LvTrJLjdee7HNr9FWDNK5DXqCeUyjFzs4gdn+Q5/Fts8oiv/ZB4mml7nN6MtMGNwQE+pIGBi1XT5CW2GHel6pQJarNQQoWQ54ANKcDak1SmR2vQJF6m9e/r4Q==";
		  //String enc_r_in = "6gAAAKzv4Yzoq87z3MHisXnQYk6CpnYon++ASBUfUfFpNrwacj1LlWpfKww9WesoDKjOPNK4ammnOykY6cfzgunr3BfZ+ZppERPjhqaGAU/mWOBq0cJ/XTronb8n1eE3IbS0i3L6EjhHPIxM04OIEzz6d0z1W6D4V8Y3W68d67PWoy516JWmAvvLIRSu2qwIslxhcChsVserCy7xjjoGiMrBqDHDoT06B6UoKzk9gx4oI2hMKOaQZA8OGOdzWZxPhqBHTUDHE4BJBX11WMQ+02TG52n/2fQJt6G1kFgjvj8Pq7IXA++WzjHOmk7WQTrk2IYmwmXsIRY=";
		  //String enc_r_in = "+gAAAKsTujjoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oaJdU9le7h/ImCa+ihqLMA+FEyi/J9vqLJI6sR5qqdwPHytF8/l8zeQjrPGzewK+opPONiHmaqKFEmujrW773vzkpIHQd6ax2fMyDEUFjzDq5yGsmjDH01TMPiO6rS7zvGFr2M3XB6Y6CqgTvux6QcckFhB6wz+hAFMbDdIIGJHEfID65a8NmgPPGHThdkHn4N1vjb1/YZgPQS8hUwbEAfSDQ8VnAHvaN4kNg+rSzFOq";
		  //String enc_r_in = "SQEAAJVJLQzP2Idwk6QRI5jQGZg/IP0y9Oy10IOBhEI8JVtdSIcDbTS+/8+T2B3fyMHVygpt+SufXyX/hUvouPIKYIeFIXU7RUekHZBgtAwNWHA+dp0QNDI1gU/UIDsjO8pI3632y5n5tmLeO1DwOUKK84LwTSimXfvighHLnoR3Sv3++5NE2D0Sy8/bj7BFgBVIxGZzc30WhhDZRTDrAY0nGCardaWYWxpEOEhbwt8TdPdCispwykynjP66YjVCA54wGcKZ1YguynI5w4X59mV6rZ5IZO+dIMTAAh3FCvZIrYBOF9aV0gWVyVR7VszJpgfxE1/ldCiZmw+7y3rL1VV1xzTbgWVSePYPq6lJdp06epHDIqJjBvmMqrQ+GdoJMQUQtpl8lBmEfmL+aCj/tGrhDcIkwYBtQs5YrxXLV8XnW0FGLiNFKfMhSSqrxxSvfmPFnXSONXM=";
          //String enc_r_in = "dwEAANiwww6qxAXSBnCbjSycqVlktfMEPPC8HH2amsvFduo+7LnfWyxNUttJ2hmc6YFqYM7SVlUFzfhUkY7bIkrX3whF19ydENa8yUeOWbjBtTDDU3JB3M8P5mZO/J960zrG/WOwDT6OWRBAhykzh2Tdn7o515t0+PrjaoVJ8JtmiSCcexhStUw3R0HHu4BQ/rLDEmIYJkxYrNy0eP6jZJV/OZTXHuZhl2keCX3Qb+KaEOfT+0Jvfplg7p6R5/tlgjqL+hGAcuNwMoLB63ti989RcU7NoyiEYhinwutTvCMIrgm2htA4mt4dz3O0c/40PqLptRHbY+xEvpdq6iVBT6hdbuPElqsDT8oR+YlpCZorR8eMcBFUdoQpFSLJSQoFUealmj5HfKACDkLhZY8d+PyTXl9b21I1l8ESjhge7IcbyCt0VHtNyLhnbfiRhe/cfNpnOSWYP+gddAGO8CMTbQrwiwc+58BcWkkENhOWeoFgIYQvr57urNFRJJ4V2Obx";
		  //String enc_r_in = "hQEAANLpQ3YqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjiUIB+CQ1ZnABnV3niQ3uOaOx7B5CTz3ugULUImCia42OnoSX+XPvWfwCX6oxh8P23mo0+Fiuy95sf5ybiXOtbtBj7E5VXRjKoGHAJOWzmeLmQNeFdEkkSt9ftRt+RQcCvidHuM+hJqs9/VwumDGvxYGPEF2B1vOfSQpXtC0PP1+wdKf1mWB/rgg2Nxyd/et9ibGrjdIF93Ud0QsBuzh4c7IvBa+X9Ig7jX0jRy8JxIAb+gf3anwvYS6CrZgvTr/DjW6umDVIUmSF4uxMGw3/CnfIg86KXsHeQYe/68/iMgF+GJ2nj7HcJ+rsQ6eR3/TaPQ2XL82keTQ/Ht8EFAyPAvx+NGdPVyTZj4jzzXJUfjrr25rQRam0rQRGB+hOZSfQvP+r4uEmPjhKGdMPaYJcRdLuwY5rL3BMbtKgs2+fBuxk5GbGo/0vKQfC1qYpSxxf5sla44/dFsGliRo9LbFutSA==";
		  //String enc_r_in = "eAEAAAdY/6kqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjikUgBHHZo56iIXQ9wOkLxBdU6Vk/QD2pqYt9VmQBZthPQzmPghb+IDh7mM0AbI+39r02pYQBUsZ4LrSGrrbIBvBdTF3z/7gD41uT+ws+q+J6bGtr4VwR2XRiOEEmilCyjTIODOzt7Gl9JyMCHVMd7nEyBh118wCquoohT4jnXGYh4XWbuhzoNLDZYY+5aLSCsiYyTXc5XzGDOdmkDDS8AeVKfd3cRK1qcjzsVRamXmS3OMwZYZRzyoLZZtisKm5WMVP4VXIfQVb0rmk++DoU37rmCWeJ8bd2aGEFSBpcW26KfJbYfKqT/tuXbNdQ6ZCv4FZWtP0dxuYn6BmoX/2NAI30uZgcSI/9YebrBcxPUy4U1Kb5aB+JzQTH26+ARsU1nR9HoPzj8X8tzaZWor2HXplCYRI59rh1Xi4QewSPAtlYTEil+M3hhuIr/B04L/kglplzAuy7oKfc=";
		  //String enc_r_in  = "eAEAAAdY/6kqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjikUgBHHZo56iIXQ9wOkLxBdU6Vk/QD2pqYt9VmQBZthPQzmPghb+IDh7mM0AbI+39r02pYQBUsZ4LrSGrrbIBvBdTF3z/7gD41uT+ws+q+J6bGtr4VwR2XRiOEEmilCyjTIODOzt7Gl9JyMCHVMd7nEyBh118wCquoohT4jnXGYh4XWbuhzoNLDZYY+5aLSCsiYyTXc5XzGDOdmkDDS8AeVKfd3cRK1qcjzsVRamXmS3OMwZYZRzyoLZZtisKm5WMVP4VXIfQVb0rmk++DoU37rmCWeJ8bd2aGEFSBpcW26KfJbYfKqT/tuXbNdQ6ZCv4FZWtP0dxuYn6BmoX/2NAI30uZgcSI/9YebrBcxPUy4U1Kb5aB+JzQTH26+ARsU1nR9HoPzj8X8tzaZWor2HXplCYRI59rh1Xi4QewSPAtlYTEil+M3hhuIr/B04L/kglplzAuy7oKfc=";
		  //String enc_r_in = "RwEAAA6PzhPoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/GhBl9328Wr7iewb1H2SsREh5NzdiSJowF3VtfbtKuB5Xfi02KGliWpRx/W9PNqwXDH+gcpKgZm7Y+GWQD32TCEpvDiT6htSaY8TefWFsXwKeYTWbSjdvyPCudJQFVugdanA90VQsjgrzl14KnZ4lVBaJhCZN8//ifddAWJmMb6iolkr2l8tnk4GkHwUS75Qlkiovve3whd3Qpg+fH6BzAHVJiHGxIbTpnMSqNf/o7KfpZOBpBgbvrr";
		  //String enc_r_in = "kQEAALe6DY/oq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/F+sm8wUznxFJGdYbesf12J9ZbUZXC5yblw/mK2HJJ4yHPiZDWUDXa+Gg8+3/DIBN6bLgY/JA4/xAvN8cphSHJkhXtG1+7yksPLVrCd/LnkrnUPEOj57yWM+4aI8vTWumcZrE/NcRdtX317e+D0oOFzPiz6sCmXzEIR4m8mnLWSEvdD4ndNgCOVmSuIfVN275DfLFNVwx7aN5VdbWOSbdk9rOu++r2p+arjm9GYZxNa3xRUngHPRIF+ObD71H/pwYk5Jog/GB6Rf3vTMc362O/e7tu31N4vfaclNKdX/ZvDBQO/nbNKShpar5/VMRRNVSKLphKDWRQo+LN1tS0b5Vs+2Z+0mUUSCbI=";
		  //String enc_r_in = "RwEAANOgibLoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/GhBl9328Wr7gblLRkiwpyGn5HzsB0xlYG//D7kcuM8mlGPNiRfLR7juUU9qbIRVLb5E3m2HjkwH/qQnuIY2lN0MrfNk4ekL/smkx02XPC3CmH4sxQta188PRxp7gFnxMmMhNztxdlfNV4qfLrSbpTPsHM+J1ED4fQWaddThmH0vITZNurynjGMqIIcPDwLKYOtrijqpYHbqcOOsFHSieRGdb0MSJnfLcPUzbRPA9cpFVf5StWI1QcS";
		  String enc_r_in = "kQEAAI374BHoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/F+sm8wUznxFJGdYbesf12J9ZbUZXC5yblw/mK2HJJ4yHPiZDWUDXa+JY/z8OknpislGHxtCcM83OtWsxZ3zB2VQp6MwwhlLl82KixLbN9J+xuw4O0rPAOjKkPT14eoUBGrtXul8nG3q56lvuUDRzgzXq8R8mUxFexrOlZFwDuvZ7VKckKJlLj2OdCy0kpgCwNLID0FQWVpxT8ckQScC2RjMyUgN3rDNl3CyydUKJXrMRCzqxKsC+7SDKiL0Ti6beD2J4dIcQdaPZIk8KkgPo7es9nMm96GuY5PePr65CMhOEg62Vw1cxqQk2W5oP9sww9M1YNFwqu9yM/thqWkXuBPZI++AHzfho8=";
		  char[] dec_in_1 = {/*0x47, 0x00, 0x00, 0x00, */
				  0x91, 0xc7, 0x85, 0xe4, 0x4a, 0x1c, 0xf7, 0xf2, 
				  0xc5, 0x74, 0x5d, 0xc0, 0x25, 0x00, 0x1b, 0x50, 
				  0x82, 0x2c, 0xeb, 0x1a, 0x79, 0xe6, 0x85, 0x8c, 
				  0x79, 0x43, 0xe9, 0xba, 0xe0, 0xef, 0xff, 0x06, 
				  0x32, 0xd4, 0x85, 0xf9, 0x5c, 0x02, 0xf7, 0xf5, 
				  0xda, 0x86, 0xe9, 0x04, 0xcc, 0x07, 0x9e, 0x85, 
				  0xa1, 0xda, 0x39, 0x2b, 0x90, 0x31, 0xd3, 0x59, 
				  0xff, 0xde, 0x02, 0x16, 0x76, 0x35, 0x93, 0x90, 
				  0xae, 0x07, 0xb9, 0xa7, 0xe7, 0x7d, 0x64, 0x9c, 
				  0xbc, 0x18, 0x02, 0xc0 };
		  
		  String dec_in_str_1 = new String(dec_in_1);
		  try {
			byte[] enc_out_1 = Encode_r(enc_r_in.getBytes(), userKey_str.getBytes(), (byte)0, (byte)36);
			
			Log.e(TAG, "enc_out_222:" + new String(enc_out_1));
			
			byte[] dec_out_1 = Decode(dec_in_str_1.getBytes(), (byte)36, userKey_str.getBytes(), (byte)18);
		
			Log.e(TAG, "dec_out_222:" /*+ new String(dec_out_1)*/);
			Dump_TCRS(dec_out_1);
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 409
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=100
	   * &tc_sfuncno=3
	   * &tc_packageid=2
	   * &TC_REQLENGTH=159
	   * &TC_REQDATA=nwAAAPB+2HXibOQXPa80JhVJ613PTURtj6RIOnHhK1YOARyX6mPoOWcYH4OoE6gaSsmzzm/fExhkCUmLVkQqolSX9M3fesHD5juPP1zCFlAULDLuE3I+1YWSmIcr2AUuvG0U+mz/NglNKh9HvVwz0SQwGZ0i472fWKEY7IBqTBQnF2AXkkmrzB4Fz/XYRBBzGY7wZuW5WjKK3I9mC3YZowBPnNypJHXa
	   * <TC_REQDATA dec:mobile=18071080819&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * （返回ETag: 0,2,18,36)
	   * 
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 322
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3501
	   * &tc_sfuncno=7
	   * &alias=reg_tips
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=3
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * （返回 ETag: 0,3,18,0）
	   * 
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 351
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3504
	   * &tc_sfuncno=2
	   * &tab_code=news_tab|ad_tab|popmsg|default_tab
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=4
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * (返回 ETag: 0,4,18,0)
	   * */
	  public static void reg_3_6(){
		  
	  }
	  
	  /**
	   * 先进入登录页面，结果提示 认证失败，您已经申请了动态口令，请使用正确的方式登录系统!
	   * 
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 307
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3750
	   * &tc_sfuncno=5
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=6
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * 返回 ETag: 0,6,18,0,3750,5
	   * 
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 530
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3500
	   * &tc_sfuncno=6
	   * &tc_packageid=7
	   * &TC_REQLENGTH=244
	   * &TC_REQDATA=9AAAAKLLHTnoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oZanBbYc4xMGTkJQyv6e1abkZsKV9v/vFXyBaxV3HPTi3lmSCRTtmPhRyREuPC9nYG1aRevkFc3FE6Lkgxg/odZPY6RqPSgIF9Qr7BO8+LvTrJLjdee7HNr9FWDNK5DXqCeUyjFzs4gdn+Q5/Fts8oiv/ZB4mml7nN6MtMGNwQE+pIGBi1XT5CW2GHel6pQJarNQQoWQ54ANKcDak1SmR2vQJF6m9e/r4Q==
	   * <TC_REQDATA dec:inputtype=Z&custorgid=3100&inputid=310000110505&trdpwd=1*2*8&operway=i&authtype=0&authdata=&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * (返回 ETag: 0,7,18,36,3500,6)
	   * 
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 307
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3501
	   * &tc_sfuncno=4
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=8
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * （返回ETag: 0,8,18,0,3501,4）
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 307
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3750
	   * &tc_sfuncno=5
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=9
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * （返回 ETag: 0,9,18,0,3750,5）
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 520
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3500
	   * &tc_sfuncno=10
	   * &tc_packageid=10
	   * &TC_REQLENGTH=234
	   * &TC_REQDATA=6gAAAKzv4Yzoq87z3MHisXnQYk6CpnYon++ASBUfUfFpNrwacj1LlWpfKww9WesoDKjOPNK4ammnOykY6cfzgunr3BfZ+ZppERPjhqaGAU/mWOBq0cJ/XTronb8n1eE3IbS0i3L6EjhHPIxM04OIEzz6d0z1W6D4V8Y3W68d67PWoy516JWmAvvLIRSu2qwIslxhcChsVserCy7xjjoGiMrBqDHDoT06B6UoKzk9gx4oI2hMKOaQZA8OGOdzWZxPhqBHTUDHE4BJBX11WMQ+02TG52n/2fQJt6G1kFgjvj8Pq7IXA++WzjHOmk7WQTrk2IYmwmXsIRY=
	   * <TC_REQDATA dec:inputtype=Z&inputid=310000110505&trdpwd=1*2*8&mobileno=18071080819&custorgid=3100&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 539
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3500
	   * &tc_sfuncno=6
	   * &tc_packageid=11
	   * &TC_REQLENGTH=250
	   * &TC_REQDATA=+gAAAKsTujjoq87z3MHisQXyENiF+8En6ib8+xHNyaUe/sz9qFO5EPu6RRfRO6fK/3kqSQLIGd545QyRTUtIxCauvrZmPA8aC66XbpAz0SNAGpgSapu2oaJdU9le7h/ImCa+ihqLMA+FEyi/J9vqLJI6sR5qqdwPHytF8/l8zeQjrPGzewK+opPONiHmaqKFEmujrW773vzkpIHQd6ax2fMyDEUFjzDq5yGsmjDH01TMPiO6rS7zvGFr2M3XB6Y6CqgTvux6QcckFhB6wz+hAFMbDdIIGJHEfID65a8NmgPPGHThdkHn4N1vjb1/YZgPQS8hUwbEAfSDQ8VnAHvaN4kNg+rSzFOq
	   * <TC_REQDATA dec:inputtype=Z&custorgid=3100&inputid=310000110505&trdpwd=1*2*8&operway=i&authtype=5&authdata=124574&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 646
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=2
	   * &tc_packageid=12
	   * &TC_REQLENGTH=329
	   * &TC_REQDATA=SQEAAJVJLQzP2Idwk6QRI5jQGZg/IP0y9Oy10IOBhEI8JVtdSIcDbTS+/8+T2B3fyMHVygpt+SufXyX/hUvouPIKYIeFIXU7RUekHZBgtAwNWHA+dp0QNDI1gU/UIDsjO8pI3632y5n5tmLeO1DwOUKK84LwTSimXfvighHLnoR3Sv3++5NE2D0Sy8/bj7BFgBVIxGZzc30WhhDZRTDrAY0nGCardaWYWxpEOEhbwt8TdPdCispwykynjP66YjVCA54wGcKZ1YguynI5w4X59mV6rZ5IZO+dIMTAAh3FCvZIrYBOF9aV0gWVyVR7VszJpgfxE1/ldCiZmw+7y3rL1VV1xzTbgWVSePYPq6lJdp06epHDIqJjBvmMqrQ+GdoJMQUQtpl8lBmEfmL+aCj/tGrhDcIkwYBtQs5YrxXLV8XnW0FGLiNFKfMhSSqrxxSvfmPFnXSONXM=
	   * <TC_REQDATA dec:unlist=|moneytype|fundseq|&inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void zijin_3_6(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 698
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=3
	   * &tc_packageid=13
	   * &TC_REQLENGTH=375
	   * &TC_REQDATA=dwEAANiwww6qxAXSBnCbjSycqVlktfMEPPC8HH2amsvFduo+7LnfWyxNUttJ2hmc6YFqYM7SVlUFzfhUkY7bIkrX3whF19ydENa8yUeOWbjBtTDDU3JB3M8P5mZO/J960zrG/WOwDT6OWRBAhykzh2Tdn7o515t0+PrjaoVJ8JtmiSCcexhStUw3R0HHu4BQ/rLDEmIYJkxYrNy0eP6jZJV/OZTXHuZhl2keCX3Qb+KaEOfT+0Jvfplg7p6R5/tlgjqL+hGAcuNwMoLB63ti989RcU7NoyiEYhinwutTvCMIrgm2htA4mt4dz3O0c/40PqLptRHbY+xEvpdq6iVBT6hdbuPElqsDT8oR+YlpCZorR8eMcBFUdoQpFSLJSQoFUealmj5HfKACDkLhZY8d+PyTXl9b21I1l8ESjhge7IcbyCt0VHtNyLhnbfiRhe/cfNpnOSWYP+gddAGO8CMTbQrwiwc+58BcWkkENhOWeoFgIYQvr57urNFRJJ4V2Obx
	   * <TC_REQDATA dec:sign=income&position=poststr&unlist=|market|&inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void stock_3_6(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 722
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=8
	   * &tc_packageid=14
	   * &TC_REQLENGTH=389
	   * &TC_REQDATA=hQEAANLpQ3YqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjiUIB+CQ1ZnABnV3niQ3uOaOx7B5CTz3ugULUImCia42OnoSX+XPvWfwCX6oxh8P23mo0+Fiuy95sf5ybiXOtbtBj7E5VXRjKoGHAJOWzmeLmQNeFdEkkSt9ftRt+RQcCvidHuM+hJqs9/VwumDGvxYGPEF2B1vOfSQpXtC0PP1+wdKf1mWB/rgg2Nxyd/et9ibGrjdIF93Ud0QsBuzh4c7IvBa+X9Ig7jX0jRy8JxIAb+gf3anwvYS6CrZgvTr/DjW6umDVIUmSF4uxMGw3/CnfIg86KXsHeQYe/68/iMgF+GJ2nj7HcJ+rsQ6eR3/TaPQ2XL82keTQ/Ht8EFAyPAvx+NGdPVyTZj4jzzXJUfjrr25rQRam0rQRGB+hOZSfQvP+r4uEmPjhKGdMPaYJcRdLuwY5rL3BMbtKgs2+fBuxk5GbGo/0vKQfC1qYpSxxf5sla44/dFsGliRo9LbFutSA==
	   * <TC_REQDATA dec:sign=~bsflag&position=poststr&unlist=|market|&cancelflag=1&inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void show_chedan_3_6(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 710
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=8
	   * &tc_packageid=15
	   * &TC_REQLENGTH=376
	   * &TC_REQDATA=eAEAAAdY/6kqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjikUgBHHZo56iIXQ9wOkLxBdU6Vk/QD2pqYt9VmQBZthPQzmPghb+IDh7mM0AbI+39r02pYQBUsZ4LrSGrrbIBvBdTF3z/7gD41uT+ws+q+J6bGtr4VwR2XRiOEEmilCyjTIODOzt7Gl9JyMCHVMd7nEyBh118wCquoohT4jnXGYh4XWbuhzoNLDZYY+5aLSCsiYyTXc5XzGDOdmkDDS8AeVKfd3cRK1qcjzsVRamXmS3OMwZYZRzyoLZZtisKm5WMVP4VXIfQVb0rmk++DoU37rmCWeJ8bd2aGEFSBpcW26KfJbYfKqT/tuXbNdQ6ZCv4FZWtP0dxuYn6BmoX/2NAI30uZgcSI/9YebrBcxPUy4U1Kb5aB+JzQTH26+ARsU1nR9HoPzj8X8tzaZWor2HXplCYRI59rh1Xi4QewSPAtlYTEil+M3hhuIr/B04L/kglplzAuy7oKfc=
	   * <TC_REQDATA dec:sign=~bsflag&position=poststr&unlist=|market|&inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * */
	  public static void show_danriweituo(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 710
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=9
	   * &tc_packageid=16
	   * &TC_REQLENGTH=376
	   * &TC_REQDATA=eAEAAAdY/6kqI4GFr1bHC7aC/nLzeKhuUwFbJYjYYjkODC/a237u9Xa126GUYHjikUgBHHZo56iIXQ9wOkLxBdU6Vk/QD2pqYt9VmQBZthPQzmPghb+IDh7mM0AbI+39r02pYQBUsZ4LrSGrrbIBvBdTF3z/7gD41uT+ws+q+J6bGtr4VwR2XRiOEEmilCyjTIODOzt7Gl9JyMCHVMd7nEyBh118wCquoohT4jnXGYh4XWbuhzoNLDZYY+5aLSCsiYyTXc5XzGDOdmkDDS8AeVKfd3cRK1qcjzsVRamXmS3OMwZYZRzyoLZZtisKm5WMVP4VXIfQVb0rmk++DoU37rmCWeJ8bd2aGEFSBpcW26KfJbYfKqT/tuXbNdQ6ZCv4FZWtP0dxuYn6BmoX/2NAI30uZgcSI/9YebrBcxPUy4U1Kb5aB+JzQTH26+ARsU1nR9HoPzj8X8tzaZWor2HXplCYRI59rh1Xi4QewSPAtlYTEil+M3hhuIr/B04L/kglplzAuy7oKfc=
	   * <TC_REQDATA dec:sign=~bsflag&position=poststr&unlist=|market|&inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&poststr=&qryflag=1&count=20&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void show_danrichengjiao(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 635
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3500
	   * &tc_sfuncno=7
	   * &tc_packageid=20
	   * &TC_REQLENGTH=327
	   * &TC_REQDATA=RwEAAA6PzhPoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/GhBl9328Wr7iewb1H2SsREh5NzdiSJowF3VtfbtKuB5Xfi02KGliWpRx/W9PNqwXDH+gcpKgZm7Y+GWQD32TCEpvDiT6htSaY8TefWFsXwKeYTWbSjdvyPCudJQFVugdanA90VQsjgrzl14KnZ4lVBaJhCZN8//ifddAWJmMb6iolkr2l8tnk4GkHwUS75Qlkiovve3whd3Qpg+fH6BzAHVJiHGxIbTpnMSqNf/o7KfpZOBpBgbvrr	   
	   * <TC_REQDATA dec:inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&bsflag=0S&stkcode=600708&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 392
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=31000
	   * &tc_sfuncno=4
	   * &field=11|50|51|52|53|54|55|56|57|58|59|70|71|72|73|74|75|76|77|78|79
	   * &code=600708.2
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=21
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 742
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=6
	   * &tc_packageid=22
	   * &TC_REQLENGTH=401
	   * &TC_REQDATA=kQEAALe6DY/oq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/F+sm8wUznxFJGdYbesf12J9ZbUZXC5yblw/mK2HJJ4yHPiZDWUDXa+Gg8+3/DIBN6bLgY/JA4/xAvN8cphSHJkhXtG1+7yksPLVrCd/LnkrnUPEOj57yWM+4aI8vTWumcZrE/NcRdtX317e+D0oOFzPiz6sCmXzEIR4m8mnLWSEvdD4ndNgCOVmSuIfVN275DfLFNVwx7aN5VdbWOSbdk9rOu++r2p+arjm9GYZxNa3xRUngHPRIF+ObD71H/pwYk5Jog/GB6Rf3vTMc362O/e7tu31N4vfaclNKdX/ZvDBQO/nbNKShpar5/VMRRNVSKLphKDWRQo+LN1tS0b5Vs+2Z+0mUUSCbI=
	   * <TC_REQDATA dec:inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&trdpwd=1*2*8&chkriskflag=1&price=7.47&qty=100&stkcode=600708&market=1&secuid=A261906525&bsflag=0S&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void sell(){
		  
	  }
	  
	  /**
	   * POST / HTTP/1.1
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 635
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=3500
	   * &tc_sfuncno=7
	   * &tc_packageid=32
	   * &TC_REQLENGTH=327
	   * &TC_REQDATA=RwEAANOgibLoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/GhBl9328Wr7gblLRkiwpyGn5HzsB0xlYG//D7kcuM8mlGPNiRfLR7juUU9qbIRVLb5E3m2HjkwH/qQnuIY2lN0MrfNk4ekL/smkx02XPC3CmH4sxQta188PRxp7gFnxMmMhNztxdlfNV4qfLrSbpTPsHM+J1ED4fQWaddThmH0vITZNurynjGMqIIcPDwLKYOtrijqpYHbqcOOsFHSieRGdb0MSJnfLcPUzbRPA9cpFVf5StWI1QcS
	   * <TC_REQDATA dec:inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&bsflag=0B&stkcode=600708&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 392
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=0
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=31000
	   * &tc_sfuncno=4
	   * &field=11|50|51|52|53|54|55|56|57|58|59|70|71|72|73|74|75|76|77|78|79
	   * &code=600708.2
	   * &supportCompress=18
	   * &sysVer=3.6.2.1.1.1
	   * &hwID=A000004502832C
	   * &softName=Andriod1.6
	   * &tc_packageid=33
	   * &netaddr=18071080819
	   * &conn_style=2.460.00.28930.48031
	   * &device_vers=16|4.1.1
	   * 
	   * Host: 218.18.103.48:8002
	   * Accept: image/png
	   * Content-Length: 742
	   * tc_service=300
	   * &tc_isunicode=1
	   * &TC_ENCRYPT=36
	   * &TC_SESSION={c4b7449abda6e432bebd404d9c9131a3643c15bf8f835220e3914c2a}
	   * &tc_mfuncno=500
	   * &tc_sfuncno=6
	   * &tc_packageid=34
	   * &TC_REQLENGTH=401
	   * &TC_REQDATA=kQEAAI374BHoq87z3MHisRRNGMy+UlHpP2y68BdF5aNhiuwc4V2q967u+61ajVrG4yiq4XerYSC882a6E5vRMHpO3X4KijQIaM3SnAEzhKwtHHLSxzUg+CJKOmFYrrhK+EDPr10sc6Q0zCU8bvYdDFyEVAvHbUtPNSu+L0f6m0uuiywgcJFt6y3iooHJqQiQ3QbiBLRLx/F+sm8wUznxFJGdYbesf12J9ZbUZXC5yblw/mK2HJJ4yHPiZDWUDXa+JY/z8OknpislGHxtCcM83OtWsxZ3zB2VQp6MwwhlLl82KixLbN9J+xuw4O0rPAOjKkPT14eoUBGrtXul8nG3q56lvuUDRzgzXq8R8mUxFexrOlZFwDuvZ7VKckKJlLj2OdCy0kpgCwNLID0FQWVpxT8ckQScC2RjMyUgN3rDNl3CyydUKJXrMRCzqxKsC+7SDKiL0Ti6beD2J4dIcQdaPZIk8KkgPo7es9nMm96GuY5PePr65CMhOEg62Vw1cxqQk2W5oP9sww9M1YNFwqu9yM/thqWkXuBPZI++AHzfho8=
	   * <TC_REQDATA dec: inputtype=C&inputid=310000110505&custorgid=3100&orgid=3100&tradenode=9504&ext1=010&userinfo=~~~3100&authid=__SESSIONID__&operway=i&fundid=310000110505&trdpwd=1*2*8&chkriskflag=1&price=7.48&qty=100&stkcode=600708&market=1&secuid=A261906525&bsflag=0B&supportCompress=18&sysVer=3.6.2.1.1.1&hwID=A000004502832C&softName=Andriod1.6&netaddr=18071080819&conn_style=2.460.00.28930.48031&device_vers=16|4.1.1&>
	   * */
	  public static void buy(){
		  
	  }
}


/**
 * cm_menu_name, cm_menu_id, cm_menu_pid, cm_menu_link
 *  客户服务, -1, 1723, help?tc_mfuncno=504&tc_sfuncno=31
 *  资讯中心, -1, 1724, collist?tc_mfuncno=3504&tc_sfuncno=6
 *  综合排名, -1, 1726, pxhq?tc_mfuncno=31000&tc_sfuncno=3
 *  我的股票, -1, 1727, zxhq?tc_mfuncno=31000&tc_sfuncno=4
 *  在线交易, -1, 1730, trademenu
 *  理财专区, -1, 1729, lczq
 *  信息快线, -1, 1731, pushmsg
 *  藏金阁, -1, 1886, cjg?http://blog.guosen.com.cn/mcjg/index/
 *  全球指数, -1, 1728, collist
 *  设置帮助, -1, 1725, config
 *  基金资讯, -1, 1732, of_jjzx
 *  个股资讯, -1, 1733, f10
 *  板块行情, -1, 1848, bkhq
 *  软件更新, 1725, 1734, update
 *  流量统计, 1725, 1735, lltj
 *  关于金太阳, 1725, 1736, about
 *  参数设置, 1725, 1737, cssz
 *  免责声明, 1725, 1738, newscontent?tc_mfuncno=3501&tc_sfuncno=7&alias=help_wxtx
 *  快捷键说明, 1725, 1739, newscontent?tc_mfuncno=3501&tc_sfuncno=7&alias=help_kjsm
 *  金太阳主页, 1725, 1854, mainurl?http://wap.guosen.cn
 *  沪深指数, 1728, 1740, zgzs?tc_mfuncno=31000&tc_sfuncno=4&count=9&code=399300.1|000001.2|000009.2|000010.2|000002.2|000003.2|000016.2|399001.1|399004.1|399003.1|399005.1|399006.1
 *  香港指数, 1728, 1741, zgzs?tc_mfuncno=31000&tc_sfuncno=4&count=6&code=100100.3|100101.3|100102.3|100103.3|100104.3|101400.3
 *  世界指数, 1728, 1742, sjzs?tc_mfuncno=504&tc_sfuncno=33
 *  开放式基金, 1729, 1744, kfsjj
 *  申购宝, 1729, 1745, sgb
 *  金天利, 1729, 1743, jtl
 *  现金增利, 1729, 1875, xjzl
 *  融资融券, 1729, 1813, rzrq
 *  金理财, 1729, 1834, jlc
 *  金三方, 1729, 1855, jsf
 *  智能定投, 1729, 1866, zndt
 *  买入委托, 1730, 1746, buysale?MMLB=0
 *  卖出委托, 1730, 1747, buysale?MMLB=1
 *  委托撤单, 1730, 1748, wtcd?tc_mfuncno=500&tc_sfuncno=8&sign=~bsflag&position=poststr&unlist=|market|&cancelflag=1
 *  银证转帐, 1730, 1749, collist
 *  资金查询, 1730, 1750, trade_zjcx?tc_mfuncno=500&tc_sfuncno=2&unlist=|moneytype|fundseq|
 *  股份查询, 1730, 1751, trade_gfcx?tc_mfuncno=500&tc_sfuncno=3&sign=income&position=poststr&unlist=|market|
 *  当日委托, 1730, 1752, tradequery?tc_mfuncno=500&tc_sfuncno=8&sign=~bsflag&position=poststr&unlist=|market|
 *  当日成交, 1730, 1753, tradequery?tc_mfuncno=500&tc_sfuncno=9&sign=~bsflag&position=poststr&unlist=|market|
 *  综合业务, 1730, 1754, collist
 *  退出交易, 1730, 1755, tradeexit
 *  基金概况, 1732, 1756, newscontent?tc_mfuncno=504&tc_sfuncno=27
 *  基金经理, 1732, 1757, newscontent?tc_mfuncno=504&tc_sfuncno=30
 *  金算盘核心数据, 1733, 1873, newscontent?tc_mfuncno=504&tc_sfuncno=6&lmdm=ggzd
 *  研究报告, 1733, 1874, newslist?tc_mfuncno=504&tc_sfuncno=7&cont_sfunc=8&newstypeid=yjbg
 *  信息雷达, 1733, 1758, newscontent?tc_mfuncno=504&tc_sfuncno=11
 *  公司概况, 1733, 1759, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=gsgk
 *  财务指标, 1733, 1760, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=cwzb
 *  主营构成, 1733, 1761, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=zygc
 *  股本结构, 1733, 1762, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=gbjg
 *  主要股东, 1733, 1763, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=zygd
 *  股东人数, 1733, 1764, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=gdrs
 *  分红扩股, 1733, 1765, newscontent?tc_mfuncno=504&tc_sfuncno=12&lmdm=fhkg
 *  金天利产品, 1743, 1804, jtlcp?tc_mfuncno=500&tc_sfuncno=54&unlist=|market|autoextension|
 *  买入, 1743, 1805, jtlmr
 *  提前终止, 1743, 1806, jtlzz?tc_mfuncno=500&tc_sfuncno=57&unlist=|market|matchcode|&position=poststr
 *  当日委托查询, 1743, 1807, tradequery?tc_mfuncno=500&tc_sfuncno=59&position=poststr
 *  当日成交查询, 1743, 1808, tradequery?tc_mfuncno=500&tc_sfuncno=60&position=poststr
 *  未到期查询, 1743, 1809, tradequery?tc_mfuncno=500&tc_sfuncno=61&unlist=|holdflag|ordersno|
 *  金天利公告, 1743, 1810, newslist?tc_mfuncno=504&tc_sfuncno=41&cont_sfunc=42&newstypeid=4181
 *  历史委托查询, 1743, 1811, hisquery?tc_mfuncno=500&tc_sfuncno=59&position=poststr
 *  历史成交查询, 1743, 1812, hisquery?tc_mfuncno=500&tc_sfuncno=60&position=poststr
 *  更改续做状态, 1743, 1887, ggxzzt?tc_mfuncno=500&tc_sfuncno=61&unlist=|holdflag|ordersno|&position=poststr
 *  我的基金, 1744, 1766, of_wdjj?tc_mfuncno=504&tc_sfuncno=24
 *  基金份额, 1744, 1767, of_fecx?tc_mfuncno=500&tc_sfuncno=41
 *  基金交易, 1744, 1768, collist
 *  综合查询, 1744, 1769, collist
 *  基金开户, 1744, 1770, of_jjkh
 *  分红设置, 1744, 1771, of_fhsz?tc_mfuncno=500&tc_sfuncno=39
 *  基金查询, 1744, 1772, collist
 *  基金排行, 1744, 1773, of_jjpx
 *  基金评级, 1744, 1774, of_jjpj
 *  申购新股, 1745, 1775, xgsg?tc_mfuncno=3504&tc_sfuncno=3
 *  委托查询, 1745, 1776, tradequery?tc_mfuncno=500&tc_sfuncno=8&sign=~bsflag&position=poststr&unlist=|market|
 *  配号查询, 1745, 1777, hisquery?tc_mfuncno=500&tc_sfuncno=23&position=poststr
 *  中签查询, 1745, 1778, hisquery?tc_mfuncno=500&tc_sfuncno=25&position=poststr
 *  新股资讯, 1745, 1779, newslist?tc_mfuncno=504&cont_sfunc=17&tc_sfuncno=16
 *  待发新股, 1745, 1780, newslist?tc_mfuncno=504&cont_sfunc=19&tc_sfuncno=18
 *  银行转证券, 1749, 1781, banktransfer
 *  证券转银行, 1749, 1782, banktransfer
 *  银行余额, 1749, 1783, bankbalance?tc_mfuncno=500&tc_sfuncno=67
 *  转帐查询, 1749, 1784, banktranquery?tc_mfuncno=500&tc_sfuncno=22
 *  股东资料, 1754, 1785, tradequery?tc_mfuncno=500&tc_sfuncno=24
 *  修改密码, 1754, 1786, xgmm?tc_mfuncno=500&tc_sfuncno=12
 *  新股配号, 1754, 1787, hisquery?tc_mfuncno=500&tc_sfuncno=23
 *  历史成交, 1754, 1788, hisquery?tc_mfuncno=500&tc_sfuncno=10&position=poststr
 *  权证行权, 1754, 1789, qzxq?tc_mfuncno=3500&tc_sfuncno=18
 *  资金流水, 1754, 1853, hisquery?tc_mfuncno=500&tc_sfuncno=81&position=poststr
 *  转股回售, 1754, 1871, zghs?tc_mfuncno=3500&tc_sfuncno=1
 *  交易所基金申赎, 1754, 1872, jysjjss?tc_mfuncno=3500&tc_sfuncno=7
 *  基金认购, 1768, 1795, of_jjrg?tc_mfuncno=500&tc_sfuncno=34
 *  基金申购, 1768, 1796, of_jjsg?tc_mfuncno=500&tc_sfuncno=35
 *  基金赎回, 1768, 1797, of_jjsh?tc_mfuncno=500&tc_sfuncno=36
 *  基金转换, 1768, 1798, of_jjzh?tc_mfuncno=500&tc_sfuncno=38
 *  委托撤单, 1768, 1799, of_wtcd?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  当日委托, 1769, 1800, of_jycx?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  基金账号, 1769, 1801, of_jycx?tc_mfuncno=500&tc_sfuncno=43&unlist=|accstatus|
 *  历史委托查询, 1769, 1802, hisquery?tc_mfuncno=500&tc_sfuncno=40&position=poststr
 *  历史成交查询, 1769, 1803, hisquery?tc_mfuncno=500&tc_sfuncno=42&position=poststr
 *  按基金名称查询, 1772, 1790, of_jjmc?tc_mfuncno=3504&tc_sfuncno=24
 *  按基金公司查询, 1772, 1791, of_jjcx?tc_mfuncno=504&tc_sfuncno=21&cont_sfunc=24
 *  按基金类型查询, 1772, 1792, of_jjcx?tc_mfuncno=504&tc_sfuncno=22&cont_sfunc=24
 *  按基金状态查询, 1772, 1793, of_jjcx?tc_mfuncno=504&tc_sfuncno=23&cont_sfunc=24
 *  查询所有基金, 1772, 1794, of_jjxx?tc_mfuncno=3504&tc_sfuncno=24
 *  融资买入, 1813, 1814, rzrq_rzmr?tc_mfuncno=3500&tc_sfuncno=7&creditid=0&moneytype=0&creditflag=0&bsflag=0B
 *  融券卖出, 1813, 1815, rzrq_rzmr?tc_mfuncno=3500&tc_sfuncno=7&creditid=0&moneytype=0&creditflag=1&bsflag=0S
 *  撤单, 1813, 1816, wtcd?tc_mfuncno=500&tc_sfuncno=8&sign=~bsflag&position=poststr&unlist=|market|&cancelflag=1
 *  还券还款, 1813, 1817, collist
 *  担保品划转, 1813, 1864, rzrq_dbphz?tc_mfuncno=500&tc_sfuncno=3&sign=income&position=poststr
 *  当日委托, 1813, 1818, tradequery?tc_mfuncno=500&tc_sfuncno=8&sign=~bsflag&position=poststr&unlist=|market|
 *  当日成交, 1813, 1819, tradequery?tc_mfuncno=500&tc_sfuncno=9&sign=~bsflag&position=poststr&unlist=|market|
 *  合约查询, 1813, 1820, collist
 *  信用资产查询, 1813, 1821, rzrq_xyzccx?tc_mfuncno=500&tc_sfuncno=74&moneytype=0
 *  信用上限查询, 1813, 1822, rzrq_xysxcx?tc_mfuncno=500&tc_sfuncno=70&moneytype=0
 *  综合查询, 1813, 1823, collist
 *  标的证券查询, 1813, 1847, rzrq_bdzq?tc_mfuncno=500&tc_sfuncno=77&creditid=0
 *  买券还券, 1817, 1830, rzrq_mqhq?tc_mfuncno=500&tc_sfuncno=72&creditid=0&moneytype=0&creditdirect=1
 *  现券还券, 1817, 1831, rzrq_xqhq?tc_mfuncno=500&tc_sfuncno=72&creditid=0&moneytype=0&creditdirect=1
 *  现金还款, 1817, 1832, rzrq_xjhk?tc_mfuncno=500&tc_sfuncno=75&creditid=0&moneytype=0
 *  卖券还款, 1817, 1833, rzrq_mqhk?tc_mfuncno=500&tc_sfuncno=3&sign=income&position=poststr&busiflag=0
 *  自动还款设置, 1817, 1862, rzrq_zdhksz?tc_mfuncno=500&tc_sfuncno=86
 *  未平仓合约查询, 1820, 1828, tradequery?tc_mfuncno=500&tc_sfuncno=72&creditid=0&moneytype=0&position=poststr
 *  已平仓合约查询, 1820, 1829, hisquery?tc_mfuncno=500&tc_sfuncno=73&creditid=0&moneytype=0&position=poststr
 *  资金查询, 1823, 1846, trade_zjcx?tc_mfuncno=500&tc_sfuncno=2
 *  股份查询, 1823, 1824, tradequery?tc_mfuncno=500&tc_sfuncno=3&sign=income&position=poststr&busiflag=0
 *  担保品证券查询, 1823, 1825, rzrq_dbpzq?tc_mfuncno=500&tc_sfuncno=78&creditid=0
 *  历史委托查询, 1823, 1826, hisquery?tc_mfuncno=500&tc_sfuncno=79&position=poststr&creditid=0&creditflag=
 *  历史成交查询, 1823, 1827, hisquery?tc_mfuncno=500&tc_sfuncno=10&position=poststr&creditid=0&creditflag=
 *  金理财产品, 1834, 1835, jlccp?tc_mfuncno=3500&tc_sfuncno=20
 *  查询份额, 1834, 1836, of_fecx?tc_mfuncno=500&tc_sfuncno=41
 *  金理财交易, 1834, 1837, collist
 *  开户, 1834, 1843, of_jjkh
 *  历史成交, 1834, 1844, hisquery?tc_mfuncno=500&tc_sfuncno=42&position=poststr
 *  分红设置, 1834, 1845, of_fhsz?tc_mfuncno=500&tc_sfuncno=39
 *  金理财信息, 1834, 1865, newslist?tc_mfuncno=504&tc_sfuncno=41&cont_sfunc=42&newstypeid=5543
 *  认购, 1837, 1838, jlcrg?tc_mfuncno=500&tc_sfuncno=34
 *  申购, 1837, 1839, jlcsg?tc_mfuncno=500&tc_sfuncno=35
 *  赎回, 1837, 1840, of_jjsh?tc_mfuncno=500&tc_sfuncno=36
 *  撤单, 1837, 1841, of_wtcd?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  当日委托, 1837, 1842, of_jycx?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  板块指数排行, 1848, 1849, bkph?tc_mfuncno=31000&tc_sfuncno=4&code=399139|399140|399150|399160|399170|399180|399190|399200|399210|399220|000006.2|000018.2|000025.2|000026.2|000027.2|000032.2|000033.2|000034.2|000036.2|000037.2|000038.2|000039.2|000040.2|000041.2|000042.2&field=4|21|16|20|11|3|1
 *  概念板块, 1848, 1850, bkhqlist?tc_mfuncno=504&tc_sfuncno=46&sector=GN
 *  行业板块, 1848, 1851, bkhqlist?tc_mfuncno=504&tc_sfuncno=46&sector=HY
 *  地区板块, 1848, 1852, bkhqlist?tc_mfuncno=504&tc_sfuncno=46&sector=DQ
 *  帐户资金转帐, 1855, 1856, jsf_zjzz
 *  一键转帐, 1855, 1857, jsf_yjzz
 *  查询帐户资金, 1855, 1858, jsf_cxzhzj?tc_mfuncno=500&tc_sfuncno=83&unlist=|moneytype|bzzhbz|bankcode|
 *  银证转帐, 1855, 1859, jsf_yzzz
 *  银行余额, 1855, 1860, jsf_yhye?tc_mfuncno=500&tc_sfuncno=67
 *  转帐查询, 1855, 1861, banktranquery?tc_mfuncno=500&tc_sfuncno=22
 *  开通, 1866, 1867, zndt_kt?tc_mfuncno=500&tc_sfuncno=50&position=poststr&unlist=|tacode|schedueleflag|ofriskvalue|
 *  修改, 1866, 1868, zndt_xg?tc_mfuncno=500&tc_sfuncno=1804&position=poststr&unlist=|tacode|actionmode|
 *  终止, 1866, 1869, zndt_zz?tc_mfuncno=500&tc_sfuncno=1804&position=poststr&unlist=|tacode|actionmode|
 *  已开通的智能定投, 1866, 1870, zndt_cx?tc_mfuncno=500&tc_sfuncno=1804&position=poststr&unlist=|tacode|actionmode|
 *  电子签约, 1875, 1876, xjzl_dzqy
 *  赎回, 1875, 1877, xjzl_sh?tc_mfuncno=500&tc_sfuncno=36
 *  查询份额, 1875, 1878, of_fecx?tc_mfuncno=500&tc_sfuncno=41
 *  撤单, 1875, 1879, of_wtcd?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  当日委托, 1875, 1880, of_jycx?tc_mfuncno=500&tc_sfuncno=45&position=poststr
 *  历史成交, 1875, 1881, hisquery?tc_mfuncno=500&tc_sfuncno=42&position=poststr
 *  预约取款, 1875, 1882, xjzl_yyqk?tc_mfuncno=3501&tc_sfuncno=2&tacode=8&ofcode=931204&alias=xjzl_yyqk
 *  预约取款撤单, 1875, 1883, xjzl_yyqkcd?tc_mfuncno=500&tc_sfuncno=824&tacode=8&ofcode=931204&reservedate=-1&qryflag=0
 *  设置自动参与, 1875, 1884, xjzl_szzdcy?tc_mfuncno=500&tc_sfuncno=821&tacode=8&ofcode=931204&qryflag=0
 *  设置保留额度, 1875, 1885, xjzl_szbled?tc_mfuncno=3501&tc_sfuncno=2&tacode=8&ofcode=931204&alias=xjzl_bled
 * 
 * */
