package com.sunparlcompany.zkel.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SurperMind on 2017/01/01.
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
    private Context mContext;// 程序的Context对象
    private Map<String, String> infos = new HashMap<String, String>();// 用来存储设备信息和异常信息
    private static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");// 月-日 时:分:秒.毫秒

    private CrashHandler() {
        crashDirPath = getDataPath() + "scrash/";
        deleteOldCrashLog();
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    private static String crashDirPath;

    /**
     * 删除过期的 crash 日志文件
     */
    private void deleteOldCrashLog() {
        File fileDirect = new File(crashDirPath);
        if (!fileDirect.exists()) {
            return;
        }
        File[] crashFiles = fileDirect.listFiles();
        if (null != crashFiles && crashFiles.length > 0) {
            // 获取离现在之前的过期日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -10);// 日期减
            String expire = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            String fileName = null;
            for (File file : crashFiles) {
                fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                if (fileName.compareTo(expire) <= 0) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        mDefaultHandler.uncaughtException(thread, ex);
    }

    /*
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        Log.e(TAG, "handleException " + ex);
        // 收集设备参数信息
        getPackageInfo(mContext);
        // 保存日志文件
//        String[] PERMISSIONS = new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//        Acp.getInstance(mContext).request(new AcpOptions.Builder().setPermissions(PERMISSIONS).build(), new AcpListener() {
//
//            @Override
//            public void onGranted() {
//                saveCrashInfo2File(ex);
//            }
//
//            @Override
//            public void onDenied(List<String> permissions) {
//
//            }
//        });
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveCrashInfo2File(ex);
        }

        return true;
    }

    /*
     * 收集设备参数信息
     * @param ctx
     */
    private void getPackageInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                infos.put("versionName", pi.versionName);
                infos.put("versionCode", String.valueOf(pi.versionCode));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info " + e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info " + e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String fileName = String.format("crash-%s.log", mFormatter.format(new Date()));
            File dir = new File(crashDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(crashDirPath + fileName);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
            return fileName;

        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file..." + e);
        }
        return null;
    }
    public static final String getDataPath() {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "enjia_wifi" + File.separator;
    }

}