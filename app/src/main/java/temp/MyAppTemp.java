package temp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sunparlcompany.zkel.model.SampleAttachment;
import com.sunparlcompany.zkel.util.ConstUtil;

import org.xutils.x;

import xpod.longtooth.LongTooth;
import xpod.longtooth.LongToothAttachment;
import xpod.longtooth.LongToothEventHandler;
import xpod.longtooth.LongToothServiceRequestHandler;
import xpod.longtooth.LongToothTunnel;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class MyAppTemp extends Application {
    private static final String tag = "MyApp";
    public static boolean isConnnectedLT = false;
    public static Context mContext = null;
    public static Toast mToast = null;
    public static Context getContext(){
        return mContext;
    }
    public static void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
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
//        LongToothUtil.longToothInit();
        initMyLongTooth();
    }

    private void initMyLongTooth() {
        LongTooth.setRegisterHost(ConstUtil.HOST_NAME, ConstUtil.PORT);
        LongTooth.start(MyAppTemp.getContext(),ConstUtil.DEVID, ConstUtil.APPID, ConstUtil.APPKEY, new LongToothHandler());
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
                    isConnnectedLT = true;
//                    LongTooth.addService(ConstUtil.NSS_SERVICENAME, new LongToothUtil.LongToothNSServer());
                    LongTooth.addService(ConstUtil.LONGTOOTH_SERVICENAME, new LongToothServer());
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
//                    if(LongToothUtil.isResponse < 307) {
//                        Log.i(tag, "handleServiceRequest: 307");
//                    }
                } catch (Exception var9) {
                    var9.printStackTrace();
                    return;
                }
            }

        }
    }
}
