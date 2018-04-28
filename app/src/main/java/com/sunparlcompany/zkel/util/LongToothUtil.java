package com.sunparlcompany.zkel.util;

import android.util.Log;

import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.model.SampleAttachment;

import xpod.longtooth.LongTooth;
import xpod.longtooth.LongToothAttachment;
import xpod.longtooth.LongToothEventHandler;
import xpod.longtooth.LongToothServiceRequestHandler;
import xpod.longtooth.LongToothTunnel;

/**
 * Created by zhanghuanhuan on 2018/4/8.
 */

public class LongToothUtil {
    public static final String tag = "LongToothUtil";
    public static boolean isConnnectedLT = false;
    private static int isResponse = 0;

    public LongToothUtil() {
    }

    public static void longToothInit() {
        AppUtils.runOnAsync(new Runnable() {
            public void run() {
                try {
                    Log.i(tag,"begin longtoothinit……");
                    LongTooth.setRegisterHost(ConstUtil.HOST_NAME, ConstUtil.PORT);
                    LongTooth.start(MyApp.getContext(),ConstUtil.DEVID, ConstUtil.APPID, ConstUtil.APPKEY, new LongToothUtil.LongToothHandler());
                } catch (Exception var2) {
                    Log.i(tag,"longtoothinit exception……");
                    var2.printStackTrace();
                }
            }
        });
    }

    private static class LongToothHandler implements LongToothEventHandler {
        private LongToothHandler() {
        }

        public void handleEvent(int code, String ltid_str, String srv_str, byte[] msg, LongToothAttachment longToothAttachment) {
            //code :code 值131073
            //ltid_str：远程长牙id:2000110293.1.140.360.3353.154
            //srv_str:远程长牙服务名
            Log.i(tag, "handleEvent: " + code);
            if(code != 131073) {
                if(code == 131074) {
                    LongToothUtil.isConnnectedLT = true;
//                    LongTooth.addService(ConstUtil.NSS_SERVICENAME, new LongToothUtil.LongToothNSServer());
                    LongTooth.addService(ConstUtil.LONGTOOTH_SERVICENAME, new LongToothUtil.LongToothServer());
                    return;
                }

                if(code == 163844) {//本地长牙不在线
                    Log.i(tag, "本地长牙不在线: " + code);
                    return;
                }

                if(code == 163842) {//长牙响应超时
                    Log.i(tag, "长牙响应超时:" + code);
                    return;
                }

                if(code == 163843) {//远程长牙不可访问
                    Log.i(tag, "远程长牙不可访问" + code);
                    return;
                }

                if(code == 262145) {//调用的远程服务不存在
                    Log.i(tag, "调用的远程服务不存在：" + code);
                    return;
                }
            }

        }
    }

    private static class LongToothNSServer implements LongToothServiceRequestHandler {
        private LongToothNSServer() {
        }

        public void handleServiceRequest(LongToothTunnel ltt, String ltid_str, String service_str, int data_type, byte[] msg) {
            if(msg != null) {
                Log.i(tag,"n22s response:"+new String(msg));
                try {
                    byte[] data = "n22s response---".getBytes();
                    SampleAttachment var8 = new SampleAttachment();
                    LongTooth.respond(ltt, 0, data, 0, data.length, var8);
                } catch (Exception var9) {
                    var9.printStackTrace();
                    return;
                }
            }

        }
    }

    private static class LongToothServer implements LongToothServiceRequestHandler {
        private LongToothServer() {
        }

        public void handleServiceRequest(LongToothTunnel ltt, String ltid_str, String service_str, int data_type, byte[] msg) {
            if(msg != null) {
                try {
                    Log.i(tag,"longtooth response:"+new String(msg));
                    byte[] data = "longtooth response:".getBytes();
                    SampleAttachment var8 = new SampleAttachment();
                    LongTooth.respond(ltt, 0, data, 0, data.length, var8);
                    if(LongToothUtil.isResponse < 307) {
                        Log.i(tag, "handleServiceRequest: 307");
                    }
                } catch (Exception var9) {
                    var9.printStackTrace();
                    return;
                }
            }

        }
    }

}
