package com.sunparlcompany.zkel.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.db.DBDao;
import com.sunparlcompany.zkel.db.DbDeviceEntity;
import com.sunparlcompany.zkel.model.Device;
import com.sunparlcompany.zkel.util.AppUtils;

import org.json.JSONArray;
import org.xutils.ex.DbException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import io.fog.fog2sdk.MiCODevice;
import io.fogcloud.easylink.api.EasyLink;
import io.fogcloud.easylink.helper.EasyLinkCallBack;
import io.fogcloud.easylink.helper.EasyLinkParams;
import io.fogcloud.fog_mdns.api.MDNS;
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
    private EasyLink easyLink = new EasyLink(this);
    private MDNS mdns = new MDNS(this);

    private String ssid;
    private String password;
    private int count;
    private Toast mToast;
    private String serviceName = "_easylink._tcp.local.";
    private static final int FIND_DEVICE_SUCCESS = 1;
    private static final int FIND_DEVICE_FINISH = 2;

    private static final String WIFI_PASSWORD = "ssid";
    private static final String WIFI_SSID = "pwd";

    private String save_ssid = "";
    private String save_password = "";

    private EditText et_ssid;
    private EditText et_password;

    private Timer mTimer = null;
    private static final long DELAY_TIME = 60*1000L;

    private String deviceRes = "";



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
                dbDevice.setUuid("20180410");
                dbDeviceEntities.add(dbDevice);
            }

        }
        try {
            dbDao.getDBUtils().saveOrUpdate(dbDeviceEntities);
            Log.i(tag,"插入成功:"+dbDeviceEntities.size());
            startActivity(new Intent(AddDeviceActivity.this,UnBindDeviceListActivity.class));
            finish();
        } catch (DbException e) {
            Log.i(tag,"dbexception!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
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
                case FIND_DEVICE_FINISH://未发现局域网内有设备（正常结束）
                    Log.i(tag,"未发现设备：0");
                    pd.dismiss();
                    MyApp.showToast("未发现设备");
                    break;
                case FIND_DEVICE_SUCCESS://发现局域网内有设备
                    Log.i(tag,"发现局域网内有设备----count>"+count);
                    pd.dismiss();
                    storeDeviceInDb(deviceRes);

//                    showToast("发现局域网内有设备");
//                    Log.i(tag,"device:"+msg.obj.toString());
//                    stopSearchDevice();
//                    if(count == 1){
//                        storeDeviceInDb(msg.obj.toString());
//                    }
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
        ForegroundColorSpan yellowWifiTip = new ForegroundColorSpan(Color.parseColor("#FDB46D"));
        builder.setSpan(yellowWifiTip, 6, tv_wifi.getText().toString().trim().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_wifi.setText(builder);
        //wifi tips设置
//        tv_netTip.setText(Html.fromHtml(getResources().getString(R.string.tv_configTips)));

        tv_title.setText("添加设备");
        iv_back.setImageResource(R.drawable.top_back);
        tv_right.setText("下一步");
        dbDao = new DBDao(this);
        micodev = new MiCODevice(this);
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);


        //WiFi名称为当前手机所连WiFi的名称\n可前往设置-无线局域网查看
        SpannableString textClick = new SpannableString("WiFi名称为当前手机所连WiFi的名称\n可前往设置-无线局域网查看");
        int start = 24;
        int end = 32;
        textClick.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                connectWiFi();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#FDB46D")); //设置颜色
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_EXCLUSIVE
        tv_netTip.append(textClick);
        tv_netTip.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
    }

    @Override
    protected void onResume() {
        super.onResume();
        ssid = micodev.getSSID();
        et_ssid.setText(ssid);
        if(!AppUtils.isWifiConnected()){
            MyApp.showToast("未连接WiFi，请检查无线网络");
            return;
        }

        SharedPreferences sp_save = getSharedPreferences("wifi_config",Context.MODE_PRIVATE);
        save_ssid = sp_save.getString(WIFI_SSID,"<unknow ssid>");
        save_password = sp_save.getString(WIFI_PASSWORD,"");
        Log.i(tag,"ssid:"+ssid+",save_ssid:"+save_ssid+",password:"+save_password);
        if(save_ssid.equals(ssid)){
            et_password.setText(save_password);
        }else{
            et_password.setText("");
        }
    }

    private void initalView() {
        tv_netTip = findViewById(R.id.tv_configTip);
        tv_wifi = findViewById(R.id.tv_wifiTip);
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);

        et_ssid = findViewById(R.id.et_ssid);
        et_password = findViewById(R.id.et_password);

        mTimer = new Timer();

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("配网中，请稍后…");
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

                if(!AppUtils.isWifiConnected()){
                    MyApp.showToast("未连接WiFi，请检查无线网络");
                    return;
                }

                SharedPreferences sp = getSharedPreferences("wifi_config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(WIFI_SSID,ssid);
                editor.putString(WIFI_PASSWORD,password);
                editor.commit();
                Log.i(tag,"ssid:"+ssid+",pwd:"+password);
                startZkEasyLink();
                break;
                default:break;
        }

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
            Log.i(tag," ondestroy remove all messages in messagequeue");
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void startZkEasyLink(){
        EasyLinkParams easyLinkParams = new EasyLinkParams();
        easyLinkParams.ssid = this.ssid;
        easyLinkParams.password = this.password;
        easyLinkParams.runSecond = '\uea60';
        easyLinkParams.sleeptime = 20;
        pd.show();
        Log.i(tag,"开始搜索设备，请稍后…");
        CountDownTimer timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                pd.setMessage("搜索设备，请稍后…"+String.valueOf((int) (millisUntilFinished)/1000)+"s");
            }

            @Override
            public void onFinish() {
                pd.setMessage("搜索设备，请稍后…0s");
            }
        };
        timer.start();
        easyLink.startEasyLink(easyLinkParams, new EasyLinkCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                Log.i(tag,"配网成功");
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
        mdns.startSearchDevices(serviceName, new SearchDeviceCallBack() {
            @Override
            public void onSuccess(int code, String message) {
                super.onSuccess(code, message);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
            }

            @Override
            public void onDevicesFind(int code, JSONArray result) {
                super.onDevicesFind(code,result);
                count++;
                deviceRes = result.toString();
                Log.i(tag,"onDeviceFind(count):"+"("+count+")"+deviceRes);
            }
        });
//        //开始配网
//        micodev.startEasyLink(ssid, password, true, 60000, 20, "", "", new EasyLinkCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//                Log.i(tag,"配网成功");
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//        });
//        //开始搜索设备
//        micodev.startSearchDevices(serviceName, new SearchDeviceCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//
//            @Override
//            public void onDevicesFind(int code, JSONArray result) {
//                count++;
//                deviceRes = result.toString();
//                Log.i(tag,"onDeviceFind(count):"+"("+count+")"+deviceRes);
//            }
//        });

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Log.i(tag,"60s finish stopEasylink and stopSearchDevices!");
               mdns.stopSearchDevices( null);
               easyLink.stopEasyLink(null);
//               micodev.stopSearchDevices(null);
//               micodev.stopEasyLink(null);
               Message deviceMsg = new Message();

               if(deviceRes.equals("[]")){
                   Log.i(tag,"60s finish,have not any devices!");
                   deviceMsg.what = FIND_DEVICE_FINISH;
                   mHandler.sendEmptyMessage(FIND_DEVICE_FINISH);
               }else{
                   Log.i(tag,"60s finish,have devices!");
                   deviceMsg.what = FIND_DEVICE_SUCCESS;
                   mHandler.sendEmptyMessage(FIND_DEVICE_SUCCESS);
               }
           }
       },DELAY_TIME);

    }
}
