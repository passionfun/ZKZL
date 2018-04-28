package com.sunparlcompany.zkel.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Created by zhanghuanhuan on 2018/4/8.
 */
public class GetCurrentTimeUtil {
    public static String data(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.CHINA);
        try {
            String str = String.valueOf(localSimpleDateFormat.parse(paramString).getTime()).substring(0, 10);
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public static String dataOne(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
        try {
            String str = String.valueOf(localSimpleDateFormat.parse(paramString).getTime()).substring(0, 10);
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public static String formatTime(int paramInt) {
        if ((paramInt >= 0) && (paramInt <= 9))
            return "0" + paramInt;
        return paramInt + "";
    }

    public static String getCurrentDateTimes() {
        return data(new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date(System.currentTimeMillis())));
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    public static String getCurrentTime_Today() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }

    public static String getDateTimeByMillisecond(String paramString1, String paramString2) {
        Date localDate = new Date(Long.valueOf(paramString1).longValue());
        return new SimpleDateFormat(paramString2).format(localDate);
    }
    public static int[] getHourAndMinute(String paramString) {
        int[] arrayOfInt = new int[2];
        String[] arrayOfString = paramString.split(":");
        String str1 = arrayOfString[0];
        if (str1.startsWith("0"))
            str1 = str1.substring(1, 2);
        int i = Integer.valueOf(str1).intValue();
        String str2 = arrayOfString[1];
        if (str2.startsWith("0"))
            str2 = str2.substring(1, 2);
        int j = Integer.valueOf(str2).intValue();
        arrayOfInt[0] = i;
        arrayOfInt[1] = j;
        return arrayOfInt;
    }

    public static int getTimeSecond(String paramString) {
        String[] arrayOfString = paramString.split(":");
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[1];
        if (str1.startsWith("0"))
            str1 = str1.substring(1, 2);
        int i = Integer.valueOf(str1).intValue();
        if ((i - 8 < 0) || ((i - 8 == 0) && (Integer.valueOf(str2).intValue() == 0))) ;
        for (int j = 24 + (i - 8); ; j = i - 8) {
            if (str2.startsWith("0"))
                str2 = str2.substring(1, 2);
            return j * 3600 + 60 * Integer.valueOf(str2).intValue();
        }
    }

    public static int getTimeSecondNoZone(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return 0;
        String[] arrayOfString = paramString.split(":");
        String str1 = arrayOfString[0];
        if (str1.startsWith("0"))
            str1 = str1.substring(1, 2);
        int i = Integer.valueOf(str1).intValue();
        String str2 = arrayOfString[1];
        if (str2.startsWith("0"))
            str2 = str2.substring(1, 2);
        return i * 3600 + 60 * Integer.valueOf(str2).intValue();
    }

    public static String getTimestamp(String paramString1, String paramString2) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString2, Locale.CHINA);
        try {
            String str = String.valueOf(localSimpleDateFormat.parse(paramString1).getTime()).substring(0, 10);
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public static String getTodayDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String getTodayDateTimes() {
        return new SimpleDateFormat("MM月dd日", Locale.getDefault()).format(new Date());
    }


    public static String tim(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String time(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String timeMinute(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("HH:mm");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String timedate(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }


    public static String times(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String timesTwo(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String timeslash(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public static String timeslashData(String paramString) {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date(1000L * Long.valueOf(paramString).longValue()));
    }

    public static String[] timestamp(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString))).split("[年月日时分秒]");
    }

    public static String timet(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }

    public String[] division(String paramString) {
        return paramString.split("[年月日时分秒]");
    }

    public String timesOne(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Long.valueOf(paramString).longValue();
        return localSimpleDateFormat.format(new Date(1000L * Integer.parseInt(paramString)));
    }


}