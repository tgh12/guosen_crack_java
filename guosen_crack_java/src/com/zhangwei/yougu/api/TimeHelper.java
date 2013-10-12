package com.zhangwei.yougu.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
	/**
	 * @return Calendar.SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
	 * */
    public static int getWeekOfDate(Date dt) {
        //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK);
        return w;
    }
    
	/**
	 * @return Calendar.SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
	 * */
    public static int getWeekOfDate() {
        //String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int w = cal.get(Calendar.DAY_OF_WEEK);
        return w;
    }
    
    /**
     * -1 还没到
     * 0 到了
     * 1 过了，下一天看看
     * */
    public static int isTradeTime(){
    	SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    	String date_str = sdf.format(new Date());
    	
    	 if(getWeekOfDate()==Calendar.SUNDAY || getWeekOfDate()==Calendar.SATURDAY){
     		return 1;
     	}else{
     		if(date_str.compareTo("0930") <0){
        		return -1;
        	}else if(date_str.compareTo("1500")>0){
        		return 1;
        	}else{
        		return 0;
        	}
     	}
    	
    }


}
