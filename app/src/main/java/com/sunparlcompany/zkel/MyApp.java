package com.sunparlcompany.zkel;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.sunparlcompany.zkel.util.CrashHandler;

import org.xutils.x;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class MyApp extends Application {
    private static final String tag = "MyApp";
    public static Context mContext = null;
    public static Toast mToast = null;
    public static Context getContext(){
        return mContext;
    }
    public static void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        }
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.setText(msg);
        mToast.show();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        x.Ext.init(this);
        x.Ext.setDebug(true);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(mContext);
    }

}
