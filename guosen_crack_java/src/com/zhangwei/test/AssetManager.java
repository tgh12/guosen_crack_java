package com.zhangwei.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
	public static int read(String path, byte[] buffer){
		File dataFile = new File(path);
		if(!dataFile.exists()){
			return -1;
		}
		
		try {
			FileInputStream mInput =  new FileInputStream(dataFile);
			int len = mInput.read(buffer);
			mInput.close();
			return len;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -2;
	}
	
	public static InputStream open(String filename){
		File dataFile = new File("assets/" +  filename);
		if(!dataFile.exists()){
			return null;
		}
		
		try {
			FileInputStream mInput =  new FileInputStream(dataFile);
			return mInput;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
