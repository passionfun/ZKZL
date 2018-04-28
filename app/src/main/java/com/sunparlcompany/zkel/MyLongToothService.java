package com.sunparlcompany.zkel;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sunparlcompany.zkel.model.SampleAttachment;
import com.sunparlcompany.zkel.util.ConstUtil;

import xpod.longtooth.LongTooth;
import xpod.longtooth.LongToothAttachment;
import xpod.longtooth.LongToothEventHandler;
import xpod.longtooth.LongToothServiceRequestHandler;
import xpod.longtooth.LongToothServiceResponseHandler;
import xpod.longtooth.LongToothTunnel;

/**
 * Created by zhanghuanhuan on 2018/4/11.
 */

public class MyLongToothService extends IntentService {
    private static final String tag = "MyLongToothService";
    public static MyLongToothListener listener;

    public static void setMyLongToothListener(MyLongToothListener myLongToothListener) {
        listener = myLongToothListener;
    }

    public interface MyLongToothListener {
        void updateUI(Message msg);
    }

    public static void sendDataFrame(Context mContext, String opType, String ltID, String cmd) {
        Intent mIntent = new Intent(mContext, MyLongToothService.class);
        mIntent.setAction(ConstUtil.ACTION_SEND_FRAME_SUCCESS);
        Bundle sendData = new Bundle();
        sendData.putString("opType", opType);
        sendData.putString("ltid", ltID);
        sendData.putString("cmd", cmd);
        mIntent.putExtra("bundle", sendData);
        mContext.startService(mIntent);

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MyLongToothService() {
        super("MyLongToothService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(tag, "onHandleIntent");
        if (intent != null) {
            Log.i(tag, "onHandleIntent not null");
            if (ConstUtil.ACTION_SEND_FRAME_SUCCESS.equals(intent.getAction())) {
                Log.i(tag, "onHandleIntent action equal");
                try {
                    Bundle data = intent.getBundleExtra("bundle");
                    String op = data.getString("opType");
                    String ltid = data.getString("ltid");
                    String cmd = data.getString("cmd");
                    Log.i(tag, "onhandleintent cmd:" + cmd);
                    byte[] dataFrame = cmd.getBytes();
//                    String returnData = BroadLinkUtils.sendDataFrame(dataFrame,mac,op);
                    if (op.equals(ConstUtil.OP_START)) {
                        Log.i(tag, "onHandleIntent op_start");
                        LongTooth.setRegisterHost(ConstUtil.HOST_NAME, ConstUtil.PORT);
                        LongTooth.start(MyApp.getContext(), ConstUtil.DEVID, ConstUtil.APPID, ConstUtil.APPKEY, new MyLongtoothStartHandler(op));
                    } else{
                        LongTooth.request(ltid,
                                ConstUtil.LONGTOOTH_SERVICENAME,
                                LongToothTunnel.LT_ARGUMENTS, dataFrame,
                                0,
                                dataFrame.length, new SampleAttachment(),
                                new MyLongtoothResposeHandler(op));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(tag, "onHandleIntent exception…");
                }
            }
        }
    }

    private class MyLongtoothResposeHandler implements LongToothServiceResponseHandler {
        private String op;

        public MyLongtoothResposeHandler(String op) {
            this.op = op;
        }

        @Override
        public void handleServiceResponse(LongToothTunnel longToothTunnel, String ltid_str, String service_str, int data_type, byte[] result, LongToothAttachment longToothAttachment) {
            if (result != null) {
                String res = new String(result);
                if (!TextUtils.isEmpty(res) && res.contains("CODE")) {
                    Log.i(tag, "LongToothServiceResponseHandler:" + res);

                    if (op.equals(ConstUtil.OP_BIND)) {
                        Log.i(tag, "LongToothServiceResponseHandler bind success:" + res);
                        if (listener != null) {
                            Message message = new Message();
                            message.what = ConstUtil.BIND_DEVICE_SUCCESS;
                            message.obj = res +"=="+ltid_str;//用户可以知道绑定哪些设备（成功或者失败）
                            listener.updateUI(message);
                        }
                    } else if (op.equals(ConstUtil.OP_QUERRY)) {
                        Log.i(tag, "LongToothServiceResponseHandler querry success:" + res +","+ltid_str);
                        if (listener != null) {
                            Message message = new Message();
                            message.what = ConstUtil.QUERRY_DEVICE_SUCCESS;
                            message.obj = res +"=="+ltid_str;//发送给绑定设备的列表界面的设备的状态判断（在线或者离线）
                            listener.updateUI(message);
                        }
                    } else if (op.equals(ConstUtil.OP_BRIGHTNESS)) {
                        Log.i(tag, "LongToothServiceResponseHandler bright success:" + res);
                        if (listener != null) {
                            Message message = new Message();
                            message.what = ConstUtil.CONTROL_DEVICE_BRIGHTNESS_SUCCESS;
                            message.obj = res;
                            listener.updateUI(message);
                        }
                    } else if(op.equals(ConstUtil.OP_RESET)){
                        Log.i(tag, "LongToothServiceResponseHandler reset success:" + res);
                        if (listener != null) {
                            Message message = new Message();
                            message.what = ConstUtil.RESET_DEVICE_SUCCESS;
                            message.obj = res;
                            listener.updateUI(message);
                        }
                    }
                } else {
                    Log.i(tag, "LongToothServiceResponseHandler失败");
                }
            }
        }
    }

    private class MyLongtoothStartHandler implements LongToothEventHandler {
        private String op;

        private MyLongtoothStartHandler(String op) {
            this.op = op;
        }

        @Override
        public void handleEvent(int code, String s, String s1, byte[] result, LongToothAttachment longToothAttachment) {
            Log.i(tag, "handleEvent: " + code);
            if (code != 131073) {
                if (code == 131074) {
                    String res = new String(result);
                    LongTooth.addService(ConstUtil.LONGTOOTH_SERVICENAME, new MyLongtoothService(op));
                    if (listener != null) {
                        Message message = new Message();
                        message.what = ConstUtil.START_LONGTOOTH_SUCCESS;
                        message.obj = res;
                        listener.updateUI(message);
                    }
                    return;
                }

                if (code == 163844) {//本地长牙不在线
                    Log.i(tag, "本地长牙不在线: " + code);
                    return;
                }

                if (code == 163842) {//长牙响应超时30-31s
                    Log.i(tag, "长牙响应超时:" + code);
                    return;
                }

                if (code == 163843) {//远程长牙不可访问
                    Log.i(tag, "远程长牙不可访问" + code);
                    return;
                }

                if (code == 262145) {//调用的远程服务不存在
                    Log.i(tag, "调用的远程服务不存在：" + code);
                    return;
                }
            }
        }
    }

    private class MyLongtoothService implements LongToothServiceRequestHandler {
        private String op = "";

        private MyLongtoothService(String op) {
            this.op = op;
        }

        @Override
        public void handleServiceRequest(LongToothTunnel ltt, String s, String s1, int i, byte[] result) {
            if (result != null) {
                try {
                    if (listener != null) {
                        Message message = new Message();
                        message.what = ConstUtil.ADDSERVICE_SUCCESS;
                        message.obj = result.toString();
                        listener.updateUI(message);
                    }
                    Log.i(tag, "longtooth response:" + new String(result));
                    byte[] data = "longtooth response:".getBytes();
                    SampleAttachment var8 = new SampleAttachment();
                    LongTooth.respond(ltt, 0, data, 0, data.length, var8);
                } catch (Exception var9) {
                    var9.printStackTrace();
                    return;
                }
            }
        }
    }

}
