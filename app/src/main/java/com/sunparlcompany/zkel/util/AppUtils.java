package com.sunparlcompany.zkel.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.model.LongToothRspModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zhanghuanhuan on 2018/4/8.
 */

public class AppUtils {
    private static Context mContext = MyApp.getContext();
    private static final AppUtils instance = new AppUtils();
    private final Executor executor = Executors.newFixedThreadPool(4);
    public static void decodeRspCode(LongToothRspModel ltrm) {
        if(ltrm != null) {
            switch(ltrm.getCODE()) {
                case 0:
                    MyApp.showToast("设备绑定成功");
                    break;
                case 1:
                    MyApp.showToast("绑定失败");
                    return;
                case 2:
                    MyApp.showToast("设备已被绑定");
                    return;
                default:
                    return;
            }
        }

    }
    public static void runOnAsync(Runnable runnable) {
        instance.executor.execute(runnable);
    }
    public static boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }
}
