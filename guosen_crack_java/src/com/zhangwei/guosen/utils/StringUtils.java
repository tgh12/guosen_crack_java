package com.zhangwei.guosen.utils;

public class StringUtils {
	
	public static boolean isEmpty(String str){
		if(str!=null && !str.equals("")){
			return false;
		}else{
			return true;
		}
	}

}
