package com.zhangwei.guosen;


import com.zhangwei.yougu.androidconvert.Log;
import com.zhangwei.yougu.storage.SDCardStorageManager;



public class SaveAccountInfo {
	/**************************************************************/
	private static transient final String SaveAccountInfoKey = "AccountInfoKey.txt";// 需要确保全局唯一性
	private static transient final String DefaultSaveAccountInfoKey = "DefaultAccountInfoKey.txt";// 需要确保全局唯一性
	private static transient SaveAccountInfo ins = null;
	private static transient final String TAG = "SaveAccountInfoKey";
	/**************************************************************/
	
	public String phone; //18071080819
	public String assetID; //310000110505
	public String pwd; //1*8
	public String imei;// = "A000004502832C";
	public String curver;// = "3.6.2.0.0.1";
	public String lastver;// = "3.6.4.1.1.1";
	public String m_bfKey; //"XMNqxw+RhembfA5K"
	public String inputtype; //Z or C
	
	public String session;
	public String chk_word;
	public String secuid;
	public String custorgid; //3100 wuhan
	public String authtype;
	public String authdata;
	
	public static SaveAccountInfo getInstance(){
		if(ins==null){
			SaveAccountInfo a = (SaveAccountInfo) SDCardStorageManager.getInstance().getItem(null, 	SaveAccountInfoKey , SaveAccountInfo.class);
			if(a!=null){
				ins = a;
			}else{
				SaveAccountInfo default_a = (SaveAccountInfo) SDCardStorageManager.getInstance().getItem(null, 	DefaultSaveAccountInfoKey , SaveAccountInfo.class);
				if(default_a==null){
					ins = new SaveAccountInfo();
				}else{
					ins = default_a;
				}
				
			}

		}
		
		return ins;
	}
	
	private SaveAccountInfo(){
/*		phone = "18071080819";
		assetID = "310000110505";
		pwd = "*"; 
		imei = "A000004502832C";
		curver = "3.6.2.0.0.1";
		lastver = "3.6.4.1.1.1";
		m_bfKey = "XMNqxw+RhembfA5K";

		session = null;
		chk_word = null;
		secuid = null;*/
		
	}
	
	synchronized public void persist() {
		Log.e(TAG, "SaveAccountInfo:  persist!");
		SDCardStorageManager.getInstance().putItem(null, SaveAccountInfoKey, ins, SaveAccountInfo.class);
	}
	

}
