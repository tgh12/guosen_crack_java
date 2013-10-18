package droidbox.apimonitor;

import java.io.File;
import java.lang.reflect.Array;

import com.zhangwei.yougu.androidconvert.Log;

public class Helper {
	private static Helper ins = null;
	private static boolean openDebug = false;
	private static Helper getInstance(){
		if(ins == null){
			ins = new Helper();
		}
		
		return ins;
	}
	
	private Helper(){
		File dataFile = new File("/sdcard/open_debug.flag");
		if(dataFile.exists()){
			openDebug = true;
		}else{
			openDebug = false;
			//openDebug = true;
		}
	}

/*	public static void log(String paramString) {
		getInstance();
		if(openDebug){
			Log.v("DroidBox", paramString.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		}
		
	}*/
	
	public static void main(String[] args){
		byte[] a = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
		log(Helper.toString2(a));
	}
	
	public static void log(String paramString) {
		final int FRAG_LEN = 2000;
		getInstance();
		if(openDebug){
			String tmp = paramString.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");

			int left = tmp.length();
			int index = 0;
			while(left>0){
				if(left<=FRAG_LEN){
					Log.v("DroidBox", tmp.substring(index));
					//System.out.println(tmp.substring(index));
					left = 0;
				}else{
					Log.v("DroidBox", tmp.substring(index, index+FRAG_LEN));
					//System.out.println(tmp.substring(index, index+FRAG_LEN));
					left-=FRAG_LEN;
					index+=FRAG_LEN;
				}

			}
			
		}
		
	}

	public static String toString2(Object paramObject) {
		if (paramObject == null)
			return "null";
		if (paramObject.getClass().isArray()) {
			StringBuilder localStringBuilder = new StringBuilder("{");
			int i = Array.getLength(paramObject);
			for (int j = 0;; ++j) {
				if (j >= i) {
					localStringBuilder.append("}");
					return localStringBuilder.toString();
				}
				
				if(j<20){
					localStringBuilder.append(toString(Array.get(paramObject, j)));
					if (j >= i - 1)
						continue;
					localStringBuilder.append(", ");
				}else if(j==20){
					localStringBuilder.append("... len:" + i);
				}else{
					
				}

			}
		}
		return paramObject.toString();
	}
	
	public static String toString(Object paramObject) {
		if (paramObject == null)
			return "null";
		if (paramObject.getClass().isArray()) {
			StringBuilder localStringBuilder = new StringBuilder("{");
			int i = Array.getLength(paramObject);
			int j=0;
			if(i<=0){
				localStringBuilder.append("}");
			}else{
				for (;; ++j) {
					if (j >= i-1) {
						localStringBuilder.append(toString(Array.get(paramObject, j)));
						localStringBuilder.append("}");
						break;
					}else{
						localStringBuilder.append(toString(Array.get(paramObject, j)));
						localStringBuilder.append(",");
					}

				}
			}

			return localStringBuilder.toString();
		}
		return paramObject.toString();
	}

	public static String valueOf(byte paramByte) {
		return String.valueOf(paramByte);
	}

	public static String valueOf(char paramChar) {
		return String.valueOf(paramChar);
	}

	public static String valueOf(double paramDouble) {
		return String.valueOf(paramDouble);
	}

	public static String valueOf(float paramFloat) {
		return String.valueOf(paramFloat);
	}

	public static String valueOf(int paramInt) {
		return String.valueOf(paramInt);
	}

	public static String valueOf(long paramLong) {
		return String.valueOf(paramLong);
	}

	public static String valueOf(Object paramObject) {
		return String.valueOf(paramObject);
	}

	public static String valueOf(short paramShort) {
		return String.valueOf(paramShort);
	}

	public static String valueOf(boolean paramBoolean) {
		return String.valueOf(paramBoolean);
	}

	public static String valueOf(byte[] paramArrayOfByte) {
		return new String(paramArrayOfByte);
	}

	public static String valueOf(char[] paramArrayOfChar) {
		return String.valueOf(paramArrayOfChar);
	}

	public static String valueOf(char[] paramArrayOfChar, int paramInt1,
			int paramInt2) {
		return String.valueOf(paramArrayOfChar, paramInt1, paramInt2);
	}
}
