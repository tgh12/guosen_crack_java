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
	
	String phone; //18071080819
	String assetID; //310000110505
	String pwd; //1*8
	String imei;// = "A000004502832C";
	String curver;// = "3.6.2.0.0.1";
	String lastver;// = "3.6.4.1.1.1";
	String m_bfKey; //"XMNqxw+RhembfA5K"
	
	String session;
	String chk_word;
	String secuid;
	
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
