package com.zhangwei.yougu.androidconvert;


public class Log {

	public static void  e(String tag, String exp){
		System.err.println("[" + tag + "]:" + exp);
	}
	
	public static void  w(String tag, String exp){
		System.err.println("[" + tag + "]:" + exp);
	}
	
	public static void  i(String tag, String exp){
		System.out.println("[" + tag + "]:" + exp);
	}
	
	public static void  d(String tag, String exp){
		System.out.println("[" + tag + "]:" + exp);
	}
	
	public static void  v(String tag, String exp){
		System.out.println("[" + tag + "]:" + exp);
	}

}
