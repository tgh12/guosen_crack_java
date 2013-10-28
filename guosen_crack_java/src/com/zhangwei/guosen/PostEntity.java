package com.zhangwei.guosen;



public class PostEntity {
	StringBuilder sb;
	int num = 0;
	
	public PostEntity(){
		sb = new StringBuilder();
		num = 0;
	}


	public void add(String key, String value) {
		// TODO Auto-generated method stub
		if(num!=0){
			sb = sb.append("&");
		}

		sb = sb.append(key).append("=").append(value);
		num++;
	}
	
	public String toString(){
		return sb.toString();
	}

}
