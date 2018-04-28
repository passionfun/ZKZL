package temp;


import android.os.Handler;
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

public class MyLongToothHandlerTemp implements LongToothEventHandler,LongToothServiceResponseHandler {
    private static final String tag = "MyLongToothHandler";
    private Handler mHandler ;
    private String opType;
    public MyLongToothHandlerTemp(Handler mHandler, String opType){
        this.mHandler = mHandler;
        this.opType = opType;
    }

    /**
     *
     * @param code
     * @param ltid_str
     * @param srv_str
     * @param result
     * @param longToothAttachment
     */
    @Override
    public void handleEvent(int code, String ltid_str, String srv_str, byte[] result, LongToothAttachment longToothAttachment) {
        Log.i(tag, "handleEvent: " + code);
        if(code != 131073) {
            if(code == 131074) {
                Message message = mHandler.obtainMessage();
                message.obj = result.toString();
                message.what = ConstUtil.START_LONGTOOTH_SUCCESS;
                mHandler.sendMessage(message);
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
    private class LongToothServer implements LongToothServiceRequestHandler {
        private LongToothServer() {
        }

        public void handleServiceRequest(LongToothTunnel ltt, String ltid_str, String service_str, int data_type, byte[] msg) {
            if(msg != null) {
                try {
                    Message message = new Message();
                    message.obj = msg.toString();
                    message.what = ConstUtil.ADDSERVICE_SUCCESS;
                    mHandler.sendMessage(message);

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

    /**
     *
     * @param longToothTunnel
     * @param s
     * @param s1
     * @param i
     * @param bytes
     * @param longToothAttachment
     */
    @Override
    public void handleServiceResponse(LongToothTunnel longToothTunnel, String s, String s1, int i, byte[] bytes, LongToothAttachment longToothAttachment) {
        if(bytes != null){
            String res = new String(bytes);
            if(!TextUtils.isEmpty(res) && res.contains("CODE")) {
                Message message = new Message();
                if(opType.equals(ConstUtil.OP_BIND)){
                    message.obj = res;
                    message.what = ConstUtil.BIND_DEVICE_SUCCESS;
                    mHandler.sendMessage(message);
                }else if(opType.equals(ConstUtil.OP_QUERRY)){
                    message.obj = res;
                    message.what = ConstUtil.QUERRY_DEVICE_SUCCESS;
                    mHandler.sendMessage(message);
                }else if(opType.equals(ConstUtil.OP_BRIGHTNESS)){
                    message.obj = res;
                    message.what = ConstUtil.CONTROL_DEVICE_BRIGHTNESS_SUCCESS;
                    mHandler.sendMessage(message);
                }
                Log.i(tag,"LongToothServiceResponseHandler绑定成功");


            }else{
                Log.i(tag,"LongToothServiceResponseHandler绑定失败");
                Message message = new Message();
                message.obj = res;
                message.what = ConstUtil.BIND_DEVICE_FAIL;
                mHandler.sendMessage(message);
            }
        }
    }
}
