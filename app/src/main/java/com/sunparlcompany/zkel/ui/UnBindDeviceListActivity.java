package com.sunparlcompany.zkel.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sunparlcompany.zkel.ConstUtil;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.adapter.DeviceAdapter;
import com.sunparlcompany.zkel.db.DBDao;
import com.sunparlcompany.zkel.db.DbDeviceEntity;
import com.sunparlcompany.zkel.model.Device;

import java.util.ArrayList;
import java.util.List;

import io.fog.fog2sdk.MiCODevice;
import xpod.longtooth.LongTooth;
import xpod.longtooth.LongToothAttachment;
import xpod.longtooth.LongToothEventHandler;
import xpod.longtooth.LongToothServiceRequestHandler;
import xpod.longtooth.LongToothServiceResponseHandler;
import xpod.longtooth.LongToothTunnel;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class UnBindDeviceListActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final String tag = UnBindDeviceListActivity.class.getSimpleName();
    private String ltid = "2001110001.1.2353.358.104";
    private long sendTime = 0L;
    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;

    private DBDao dbDao = null;
    private ListView lv_device;
    private List<DbDeviceEntity> dbDeviceList = null;
    private List<Device> deviceList = null;
    private Device device;
    private DeviceAdapter adapter;

    private boolean isChecked = true;
    private int currentIndex = 0;
    private MiCODevice miCODevice = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind_devicelist);
        initalView();
        initData();
        startMyLongTooth();
//        send();
    }

    private void send() {
//        LongTooth.request(ltid, "servicename",
//                LongToothTunnel.LT_ARGUMENTS, ("BR" + String.valueOf(sendTime)).getBytes(), 0,
//                ("BR" + String.valueOf(sendTime)).getBytes().length, new SampleAttachment(),
//                new LongToothResponse());
    }


    private void initData() {
        tv_title.setText("未绑定设备的列表");
        iv_back.setImageResource(R.drawable.top_back);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("绑定");

        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        dbDao = new DBDao(this);
        lv_device.setOnItemClickListener(this);

        miCODevice = new MiCODevice(this);
        dbDeviceList = dbDao.getAllDeviceInfo();
        Log.i(tag,"数据库中的设备大小："+dbDeviceList.size());
        deviceList = new ArrayList<>();
        for(int i = 0;i < dbDeviceList.size();i++){
            device = new Device();
            device.setName(dbDeviceList.get(i).getName());
            device.setIp(dbDeviceList.get(i).getIp());
            device.setPort(dbDeviceList.get(i).getPort());
            device.setMac(dbDeviceList.get(i).getMac());
            device.setFirmwareRev(dbDeviceList.get(i).getFirmwareRev());
            device.setHardwareRev(dbDeviceList.get(i).getHardwareRev());
            device.setMicoosRev(dbDeviceList.get(i).getMicoosRev());
            device.setBoundStatus(dbDeviceList.get(i).getBoundStatus());
            device.setModel(dbDeviceList.get(i).getModel());
            device.setProtocol(dbDeviceList.get(i).getProtocol());
            device.setLtID(dbDeviceList.get(i).getLtID());
            device.setManufacturer(dbDeviceList.get(i).getManufacturer());
            device.setDeviceName(dbDeviceList.get(i).getDeviceName());
            device.setSeed(dbDeviceList.get(i).getSeed());
            deviceList.add(device);
        }
        adapter = new DeviceAdapter(this,deviceList);
        lv_device.setAdapter(adapter);
    }

    private void initalView() {
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);
        lv_device = findViewById(R.id.lv_device);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
//                iv_back.setImageResource(R.drawable.top_back_sel);
                startActivity(new Intent(UnBindDeviceListActivity.this,AddDeviceActivity.class));
                finish();
            case R.id.tv_next:
                bindDevice();
                finish();
                break;
                default:break;
        }
    }

    private void bindDevice() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundColor(Color.rgb(0,172,243));
        currentIndex = position;
//        if(isChecked){
//            view.setBackgroundColor(Color.rgb(0,172,243));
//            isChecked = false;
//        }else{
//            isChecked = true;
//            view.setBackgroundColor(Color.TRANSPARENT);
//        }
    }


    private void startMyLongTooth() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LongTooth.setRegisterHost(ConstUtil.HOST_NAME,ConstUtil.PORT);
                    LongTooth.start(UnBindDeviceListActivity.this, ConstUtil.DEVID, ConstUtil.APPID, ConstUtil.APPKEY, new LongToothEventHandler() {
                        @Override
                        public void handleEvent(int code, String ltid_str, String srv_str, byte[] msg, final LongToothAttachment longToothAttachment) {
                            //code :code 值131073
                            //ltid_str：远程长牙id
                            //srv_str:远程长牙服务名
                            //收到的广播内容
//                            Log.i(tag,"code:"+code);
                            sendTime = System.currentTimeMillis();
                            Log.i(tag,"longtoothid:"+LongTooth.getId()+",ltid_str:"+ltid_str+",srv_str:"+srv_str);//2000110293.1.140.360.3353.154
                            Log.i(tag,"longtoothService;"+LongTooth.getService("ser"));
                            Log.i(tag,"start handleevent方法中的参数："+"code:"+code+",ltid_str:"+ltid_str+",srv_str:"+srv_str+",msg:"+msg.toString()+",LongToothAttachment"+longToothAttachment.toString());
                            Log.i(tag,"code:"+code+",当前系统的时间毫秒数："+sendTime);
                            LongTooth.addService("myservicename", new LongToothServiceRequestHandler() {
                                @Override
                                public void handleServiceRequest(LongToothTunnel longToothTunnel, String s, String s1, int i, byte[] bytes) {

                                }
                            });


                        }
                    });
                    //exectue
                    String msg = "BR"+String.valueOf(sendTime);
                    LongTooth.request(ltid, "myservicename",
                            LongToothTunnel.LT_ARGUMENTS, msg.getBytes(), 0,
                            msg.getBytes().length, new SampleAttachment(),
                            new LongToothResponse());
//                    LongTooth.request(ltid, "myservicename", LongToothTunnel.LT_ARGUMENTS, msg.getBytes(), 2, msg.getBytes().length, new LongToothAttachment() {
//                        @Override
//                        public Object handleAttachment(Object... objects) {
//                            Log.i(tag,"handleAttachment:"+objects.toString());
//                            return null;
//                        }
//                    }, new LongToothServiceResponseHandler() {
//                        @Override
//                        public void handleServiceResponse(LongToothTunnel longToothTunnel, String s, String s1, int i, byte[] bytes, LongToothAttachment longToothAttachment) {
//                            Log.i(tag,"handleServiceResponse方法中的LongToothTunnel参数："+longToothTunnel.getServiceName()+",s"+s+",s1:"+s1+",i:"+i+",bytes:"+bytes.toString()+",LongToothAttachment:"+longToothAttachment.toString());
//                        }
//                    });



//                    LongTooth.request(ltid, "servicename",
//                            LongToothTunnel.LT_ARGUMENTS, ("BR" + String.valueOf(sendTime)).getBytes(), 0,
//                            ("BR" + String.valueOf(sendTime)).getBytes().length, new SampleAttachment(),
//                            new LongToothResponse());
//                    LongTooth.addService("myservicename", new LongToothServiceRequestHandler() {
//                        @Override
//                        public void handleServiceRequest(LongToothTunnel longToothTunnel, String s, String s1, int i, byte[] bytes) {
//                            Log.i(tag,"addService=====handleServiceRequest中的参数LongToothTunnel:"+longToothTunnel.toString()+",s:"+s+",s1:"+s1+",i:"+i+",bytes:"+bytes.toString());
//                        }
//                    });
//                    LongTooth.request(ltid, "myservicename", LongToothTunnel.LT_ARGUMENTS, "BR" + String.valueOf(sendTime), new LongToothAttachment() {
//                        @Override
//                        public Object handleAttachment(Object... objects) {
//                            Log.i(tag,"handleAttachment:"+objects.toString());
//                            return null;
//                        }
//                    }, new LongToothServiceResponseHandler() {
//                        @Override
//                        public void handleServiceResponse(LongToothTunnel longToothTunnel, String s, String s1, int i, byte[] bytes, LongToothAttachment longToothAttachment) {
//                            Log.i(tag,"handleServiceResponse方法中的LongToothTunnel参数："+longToothTunnel.getServiceName()+",s"+s+",s1:"+s1+",i:"+i+",bytes:"+bytes.toString()+",LongToothAttachment:"+longToothAttachment.toString());
//                        }
//                    });
//                    Log.i(tag,"finsh request");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     *  Handle the remote service response results
     *  */
    private class LongToothResponse implements LongToothServiceResponseHandler {
        @Override
        public void handleServiceResponse(LongToothTunnel longToothTunnel, String s,
                                          String s1, int i, byte[] bytes, LongToothAttachment longToothAttachment) {
            try {
                Log.i(tag,"handleServiceResponse方法中的LongToothTunnel参数："+longToothTunnel.getServiceName()+",s"+s+",s1:"+s1+",i:"+i+",bytes:"+bytes.toString()+",LongToothAttachment:"+longToothAttachment.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class SampleAttachment implements LongToothAttachment{

        private boolean ckHex = false;
        @Override
        public Object handleAttachment(Object... arg0) {
            // TODO Auto-generated method stub
            Log.i(tag,"arg0:"+arg0);
            return arg0;
        }

        public void setCkHex(boolean ckHex){
            this.ckHex = ckHex;
        }

        public boolean getCkHex(){
            return this.ckHex;
        }

    }

}
