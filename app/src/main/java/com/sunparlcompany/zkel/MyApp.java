package com.sunparlcompany.zkel;

import android.app.Application;
import android.content.Context;

import com.sunparlcompany.zkel.ui.MainActivity;

import org.xutils.x;

import xpod.longtooth.LongTooth;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class MyApp extends Application {
    public Context mContext = null;
    public Context getContext(){
        mContext = getApplicationContext();
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
//    public void startLongToothService(){
//        LongTooth.setRegisterHost("118.178.233.149", 53199);
//        LongTooth
//                .start(mContext,
//                        1,
//                        1,
//                        "00ADCA26F8E1E9C60B7469AE166FE0EBB30BD760A5F32A7274EB392B6931A906D22F7503B27BDA06808F6C4A40E479D76FC5B35BBB7112BF1F3D7AFC1A0BE99E168970BADACFB0AE779C1CA132837CB0BA13396F402FEB57E21A06E255AF3D76F781180BF984BA65F2CAF4BCA0E3B9101D0A2CB2319B94DD19DA90FE65155DD2A3010001",
//                        new LongToothHandler());
//    }
}
