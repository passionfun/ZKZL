package com.sunparlcompany.zkel.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.MyLongToothService;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.model.Device;
import com.sunparlcompany.zkel.util.AppUtils;
import com.sunparlcompany.zkel.util.ConstUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MyLongToothService.MyLongToothListener{
    private static final String tag = "MainActivity";
    private static final int CONTROL_LIGHT_SUCCESS = 0;
    private static final int CONTROL_LIGHT_FAIL = 1;
    private TextView tv_title;
    private TextView tv_right;
    private TextView tv_showDeviceInfo;
    private TextView tv_currentBrightValue;
    private ImageView iv_back;
    private Button btn_controlDevice;
    private SeekBar sb_light;
    private Device device;
    private long UUID = 0L;
    private int currentBrightValue = 0;
    private String switchState = "Off";
    private String ltid = "";
    private String sendCMD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initalView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBrightCMD();

    }

    /**设备信息：
     * "Name":"EMW3080B Module#152890",
     　　　　"IP":"192.168.100.162",
     　　　　"Port":8000,
     　　　　"MAC":"B0:F8:93:15:28:90",
     　　　　"Firmware Rev":"1.0.20",
     　　　　"Hardware Rev":"3080B",
     　　　　"MICO OS Rev":"3080B002.013",
     　　　　"BOUND STATUS":"bounded",
     　　　　"Model":"EMW3080B",
     　　　　"Protocol":"com.mxchip.basic",
     　　　　"LTID":"2001110001.1.2353.358.104",
     　　　　"Manufacturer":"MXCHIP Inc.",
     　　　　"DEVNAME":"WG301_2890",
     　　　　"Seed":"2"
     */
    private void initData() {
        tv_title.setText("设备详情");
        iv_back.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.INVISIBLE);

        if(getIntent() != null){
            UUID = getIntent().getLongExtra("UUID",0L);
            device = (Device) getIntent().getSerializableExtra("device");
            String name = device.getName();
            String ip = device.getIp();
            int port = device.getPort();
            String mac = device.getMac();
            String firmwareRev = device.getFirmwareRev();
            String hardwareRev = device.getHardwareRev();
            String micoosRev = device.getMicoosRev();
            String boundStatus = device.getBoundStatus();
            String model = device.getModel();
            String protocol = device.getProtocol();
            ltid = device.getLtID();
            String manufauturer = device.getManufacturer();
            String deviceName = device.getDeviceName();
            String seed = device.getSeed();
            String deviceInfo = "Name:"+name+"\n\n"+"IP:"+ip+"\n\n"+"Port:"+port+"\n\n"+"MAC:"+mac+"\n\n"+"Firmware Rev:"+firmwareRev+"\n\n"+
                    "Hardware Rev:"+hardwareRev+"\n\n"+"MICO OS Rev:"+micoosRev+"\n\n"+"BOUND STATUS:"+boundStatus+"\n\n"+
                    "Model:"+model+"\n\n"+"Protocol:"+protocol+"\n\n"+"LTID:"+ltid+"\n\n"+"Manufacturer:"+manufauturer+"\n\n"+
                    "DEVNAME:"+deviceName+"\n\n"+"Seed:"+seed+"\n\n";

            tv_showDeviceInfo.setText(deviceInfo);

        }
        
        
        btn_controlDevice.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        sb_light.setMax(100);
        sb_light.setProgress(50);
        currentBrightValue = 50;
        sb_light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentBrightValue = progress;
                tv_currentBrightValue.setText("当前亮度值："+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void initalView() {
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        tv_currentBrightValue = findViewById(R.id.tv_currentBrightValue);
        tv_showDeviceInfo = findViewById(R.id.tv_showDeviceInfo);
        iv_back = findViewById(R.id.iv_back);
        btn_controlDevice = findViewById(R.id.btn_controlDevice);
        sb_light = findViewById(R.id.seekBar);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!AppUtils.isWifiConnected()){
            MyApp.showToast("未连接WiFi，请检查无线网络");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_controlDevice:
                if(!AppUtils.isWifiConnected()){
                    MyApp.showToast("未连接WiFi，请检查无线网络");
                    return;
                }
                if(currentBrightValue == 0){
                    switchState = "Off";
                }else{
                    switchState = "On";
                }
                sendBrightCMD();
                break;
            case R.id.tv_next:
//                startActivity(new Intent(MainActivity.this,AddDeviceActivity.class));
//                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
                default:break;
        }
    }

    private void sendBrightCMD() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CMD",ConstUtil.CMD_DEVICE_BRIGHTNESS);
            jsonObject.put("SWITCH",switchState);
            jsonObject.put("BRIGHTNESS",currentBrightValue);
            jsonObject.put("UUID",UUID);
            sendCMD = String.valueOf(jsonObject);
            Log.i(tag,"brightCMD:"+sendCMD);
            MyLongToothService.sendDataFrame(this,ConstUtil.OP_BRIGHTNESS,ltid,sendCMD);
            MyLongToothService.setMyLongToothListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private HandBrightHandler myHandler = new HandBrightHandler(this);

    @Override
    public void updateUI(Message msg) {
        myHandler.sendMessage(msg);
    }
    private class HandBrightHandler extends Handler {
        private WeakReference<MainActivity> weakReference = null;
        public HandBrightHandler(MainActivity activity){
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            if(activity == null){
                return;
            }
            switch (msg.what){
                case ConstUtil.CONTROL_DEVICE_BRIGHTNESS_SUCCESS:
                    String dataRes = msg.obj.toString();
                    try {
                        JSONObject jo = new JSONObject(dataRes);
                        int code = jo.getInt("CODE");
                        if(code == 0){
                            MyApp.showToast("控制成功");
//                            Toast.makeText(MainActivity.this,"控制成功",Toast.LENGTH_SHORT).show();
                        }else{
//                            Toast.makeText(MainActivity.this,"控制失败",Toast.LENGTH_SHORT).show();
                            MyApp.showToast("控制失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                default:break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }
}
