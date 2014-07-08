package org.dian.torun.utils;

import org.dian.torun.AppData;
import org.dian.torun.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by BaiYa on 2014/5/2.
 */
public class TimeUtils {

    public static boolean isExpired(long time) {
        return time < System.currentTimeMillis();
    }

    /**
     * 单位毫秒
     * @param create_at
     * @return
     */
    public static long getFormatTime(String create_at) {
        Date date;
        SimpleDateFormat srcDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        try {
            date = srcDateFormat.parse(create_at);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param create_at 单位:毫秒
     * @return
     */
    public static String getFormatTime(long create_at) {
        Date date;
//        SimpleDateFormat srcDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss Z", Locale.SIMPLIFIED_CHINESE);
        SimpleDateFormat dstDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE);
        date = new Date(create_at);
//        try {
//            date = srcDateFormat.parse(String.valueOf(create_at));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return dstDateFormat.format(date);
    }

    /**
     * @param create_time 毫秒单位
     * @return
     */
    public static String getIntervalTime(long create_time) {
        return getIntervalTime(create_time, System.currentTimeMillis());
    }

    /**
     * 计算时间差，并返回结果：
     * 当天时间、昨天、3天前、3月前、3年前
     * @param startTime
     * @param endTime
     * @return
     */
    private static String getIntervalTime(long startTime, long endTime) {
        if (endTime < startTime) {
            throw new IllegalArgumentException("time cannot be revered");
        }
        long ei = endTime - startTime;
        int day = (int) (ei / (1000 * 60 * 60 * 24));
        if (day == 0) {
            return getFormatTime(startTime);
        } else if (day == 1) {
            return getString(R.string.time_yesterday);
        } else if (day < 31){
            return day + getString(R.string.time_day) + getString(R.string.time_before);
        }
        int month = day / 30;
        if (month < 12) {
            return month + getString(R.string.time_month) + getString(R.string.time_before);
        }
        int year = month / 12;
        return year + getString(R.string.time_year) + getString(R.string.time_before);
    }

    private static String getString(int resId) {
        return AppData.getContext().getString(resId);
    }

}
