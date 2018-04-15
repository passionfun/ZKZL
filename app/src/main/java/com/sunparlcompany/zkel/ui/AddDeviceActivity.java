package com.sunparlcompany.zkel.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.db.DBDao;
import com.sunparlcompany.zkel.db.DbDeviceEntity;
import com.sunparlcompany.zkel.model.Device;

import org.json.JSONArray;
import org.xutils.ex.DbException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.fog.fog2sdk.MiCODevice;
import io.fogcloud.easylink.helper.EasyLinkCallBack;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String tag = AddDeviceActivity.class.getSimpleName();
    private TextView tv_wifi;
    private TextView tv_netTip;
    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;
    private DBDao dbDao = null;
    private ProgressDialog pd = null;
    private MiCODevice micodev;
    private String ssid;
    private String password;
    private int count;
    private Toast mToast;
    private String serviceName = "_easylink._tcp.local.";
    private static final int EASY_LINK_SUCCESS = 0;
    private static final int EASY_LINK_FAIL = 1;
    private static final int STOP_EASY_LINK_SUCCESS = 2;
    private static final int STOP_EASY_LINK_FAIL = 3;
    private static final int FIND_DEVICE_SUCCESS = 4;
    private static final int FIND_DEVICE_FINISH = 5;
    private static final int FIND_DEVICE_EXP = 6;
    private static final int STOP_FIND_DEVICE_SUCCESS = 7;
    private static final int STOP_FIND_DEVICE_FAIL = 8;

    private EditText et_ssid;
    private EditText et_password;

    private void storeDeviceInDb(String deviceInfo) {
        if(pd.isShowing() && pd != null){
            pd.dismiss();
            pd = null;
        }
        //清空消息队列中的消息
        if(mHandler.obtainMessage() != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        /**
         * "Name":"EMW3080B Module#152890",
         "IP":"192.168.100.194",
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
         "Seed":"3"
         */
        Gson gson = new Gson();
        List<Device> deviceList =  gson.fromJson(deviceInfo, new TypeToken<List<Device>>(){}.getType());
        List<DbDeviceEntity> dbDeviceEntities = new ArrayList<>();

        Log.i(tag,"局域网内的设备总数："+deviceList.size());
        Device device = null;
        DbDeviceEntity dbDevice = null;
        List<DbDeviceEntity> dbData = dbDao.getAllDeviceInfo();
        boolean isInDb = false;

        int dbDeviceSize = dbData.size();
        int lanDeviceSize = deviceList.size();
        Log.i(tag,"dbDeviceSize:"+dbDeviceSize+",lanDeviceSize:"+lanDeviceSize);
        for(int i = 0;i < lanDeviceSize;i++){
            for(int j = 0;j < dbDeviceSize;j++){
                if(deviceList.get(i).getMac().equals(dbData.get(j).getMac())){
                    isInDb = true;
                    Log.i(tag,"isInDb(true):"+isInDb);
                    break;
                }else{
                    isInDb = false;
                    Log.i(tag,"isInDb(false):"+isInDb);
                }
            }
            if(!isInDb){
                device = deviceList.get(i);
                dbDevice = new DbDeviceEntity();
                dbDevice.setName(device.getName());
                dbDevice.setIp(device.getIp());
                dbDevice.setPort(device.getPort());
                dbDevice.setMac(device.getMac());
                dbDevice.setFirmwareRev(device.getFirmwareRev());
                dbDevice.setHardwareRev(device.getHardwareRev());
                dbDevice.setMicoosRev(device.getMicoosRev());
                dbDevice.setBoundStatus(device.getBoundStatus());
                dbDevice.setModel(device.getModel());
                dbDevice.setProtocol(device.getProtocol());
                dbDevice.setLtID(device.getLtID());
                dbDevice.setManufacturer(device.getManufacturer());
                dbDevice.setDeviceName(device.getDeviceName());
                dbDevice.setSeed(device.getSeed());
                dbDeviceEntities.add(dbDevice);
            }

        }
        try {
            dbDao.getDBUtils().saveOrUpdate(dbDeviceEntities);
            Log.i(tag,"插入成功:"+dbDeviceEntities.size());
            startActivity(new Intent(AddDeviceActivity.this,UnBindDeviceListActivity.class));
            finish();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void stopSearchDevice() {
        micodev.stopSearchDevices(new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Message msg = Message.obtain();
                msg.what = STOP_FIND_DEVICE_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String message) {
                Message msg = Message.obtain();
                msg.what = STOP_FIND_DEVICE_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void startSearchDevice() {
        micodev.startSearchDevices(serviceName, new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Message msg = Message.obtain();
                msg.what = FIND_DEVICE_FINISH;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String message) {
                Message msg = Message.obtain();
                msg.what = FIND_DEVICE_EXP;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onDevicesFind(int code, JSONArray deviceStatus) {
                Message msg = Message.obtain();
                msg.what = FIND_DEVICE_SUCCESS;
                msg.obj = deviceStatus.toString();
                mHandler.sendMessage(msg);
            }
        });

    }
    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }
    private void stopEasyLink(){
        micodev.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Message msg = Message.obtain();
                msg.what = STOP_EASY_LINK_SUCCESS;
                mHandler.sendMessage(msg);
//                if(code == 4000){
//                }
            }

            @Override
            public void onFailure(int code, String message) {
                Message msg = Message.obtain();
                msg.what = STOP_EASY_LINK_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }
    private MyHandler mHandler = new MyHandler(this);
    private class MyHandler extends Handler {
        private  WeakReference<AddDeviceActivity> mWeakReference;

        public MyHandler(AddDeviceActivity activity) {
            this.mWeakReference = new WeakReference<AddDeviceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AddDeviceActivity addDeviceActivity = mWeakReference.get();
            if (addDeviceActivity == null) {
                return;
            }
            switch (msg.what){
                case EASY_LINK_SUCCESS://配网成功后开始停止配网
                    pd.setMessage("配网成功");
                    Log.i(tag,"配网成功");
                    stopEasyLink();
                    break;
                case EASY_LINK_FAIL://配网失败
//                    showToast("配网失败");
                    Log.i(tag,"配网失败");
                    break;
                case STOP_EASY_LINK_SUCCESS://停止配网成功之后开始搜索局域网内的设备
                    pd.setMessage("开始搜索设备");
                    Log.i(tag,"停止配网成功，开始搜索设备");
                    startSearchDevice();
                    break;
                case STOP_EASY_LINK_FAIL://停止配网失败
//                    showToast("停止配网失败");
                    Log.i(tag,"停止配网失败");
                    break;
                case FIND_DEVICE_EXP://未发现局域网内有设备（异常）
//                    showToast("未发现设备：-1");
                    break;
                case FIND_DEVICE_FINISH://未发现局域网内有设备（正常结束）

                    Log.i(tag,"未发现设备：0");
//                    showToast("未发现设备：0");
                    break;
                case FIND_DEVICE_SUCCESS://发现局域网内有设备
                    count++;
                    Log.i(tag,"发现局域网内有设备----count>"+count);
                    pd.dismiss();
//                    showToast("发现局域网内有设备");
                    Log.i(tag,"device:"+msg.obj.toString());
                    stopSearchDevice();
                    if(count == 1){
                        storeDeviceInDb(msg.obj.toString());
                    }


                    break;
                case STOP_FIND_DEVICE_FAIL://停止搜索设备结束
                    Log.i(tag,"停止搜索设备失败");
//                    showToast("停止搜索设备失败");
//                    break;
                case STOP_FIND_DEVICE_SUCCESS://停止搜索设备成功（未发现设备，但是回调方法执行了）
                    Log.i(tag,"停止搜索设备成功");
//                    showToast("停止搜索设备成功");
                    break;
                default:break;
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initalView();
        initData();
    }
    public void connectWiFi(){
        if(android.os.Build.VERSION.SDK_INT > 10) {
            startActivity(new Intent( android.provider.Settings.ACTION_WIFI_SETTINGS));
        } else {
           startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }


    private void initData() {
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_wifi.getText().toString());
        //ForegroundColorSpan 为文字前景色
        //ssid tips
        ForegroundColorSpan yellowWifiTip = new ForegroundColorSpan(Color.parseColor("#FDB46D"));
        builder.setSpan(yellowWifiTip, 6, tv_wifi.getText().toString().trim().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_wifi.setText(builder);

        tv_netTip.setText(Html.fromHtml(getResources().getString(R.string.tv_configTips)));

        tv_title.setText("添加设备");
        iv_back.setImageResource(R.drawable.top_back);
        tv_right.setText("下一步");
        dbDao = new DBDao(this);
        micodev = new MiCODevice(this);
        ssid = micodev.getSSID();
        et_ssid.setText(ssid);
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    private void initalView() {
        tv_netTip = findViewById(R.id.tv_configTip);
        tv_wifi = findViewById(R.id.tv_wifiTip);
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);

        et_ssid = findViewById(R.id.et_ssid);
        et_password = findViewById(R.id.et_password);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("开始配网");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
//                iv_back.setImageResource(R.drawable.top_back_sel);
                finish();
                break;
            case R.id.tv_next:
                password = et_password.getText().toString().trim();
                ssid = et_ssid.getText().toString().trim();
                if(TextUtils.isEmpty(ssid)){
                    showToast("未连接WiFi，请检查无线网络" );
                    return;
                }
                startEasyLink();
                break;
                default:break;
        }

    }
    private void startEasyLink() {
        pd.show();
        Log.i(tag,"开始配网");
        micodev.startEasyLink(ssid, password, true, 20000, 50, "", "", new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Message msg = Message.obtain();
                msg.what = EASY_LINK_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int code, String message) {
                Message msg = Message.obtain();
                msg.what = EASY_LINK_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pd.isShowing() && pd != null){
            pd.dismiss();
            pd = null;
        }
        //清空消息队列中的消息
        if(mHandler.obtainMessage() != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
