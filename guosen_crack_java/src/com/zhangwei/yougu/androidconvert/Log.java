package com.zhangwei.yougu.androidconvert;


public class Log {

	public static void  e(String tag, String exp){
		if(tag!=null){
			System.err.println("[" + tag + "]:" + exp);
		}else{
			System.err.println(exp);
		}

	}
	
	public static void  w(String tag, String exp){
		if(tag!=null){
			System.err.println("[" + tag + "]:" + exp);
		}else{
			System.err.println(exp);
		}
	}
	
	public static void  i(String tag, String exp){
		if(tag!=null){
			System.out.println("[" + tag + "]:" + exp);
		}else{
			System.out.println(exp);
		}
	}
	
	public static void  d(String tag, String exp){
		if(tag!=null){
			System.out.println("[" + tag + "]:" + exp);
		}else{
			System.out.println(exp);
		}
	}
	
	public static void  v(String tag, String exp){
		if(tag!=null){
			System.out.println("[" + tag + "]:" + exp);
		}else{
			System.out.println(exp);
		}
	}

}
