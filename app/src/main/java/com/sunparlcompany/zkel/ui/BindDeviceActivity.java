package com.sunparlcompany.zkel.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.MyLongToothService;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.adapter.BindDeviceAdapter;
import com.sunparlcompany.zkel.db.DBDao;
import com.sunparlcompany.zkel.db.DbDeviceEntity;
import com.sunparlcompany.zkel.model.Device;
import com.sunparlcompany.zkel.model.LongToothModel;
import com.sunparlcompany.zkel.swipemenu.SwipeMenu;
import com.sunparlcompany.zkel.swipemenu.SwipeMenuCreator;
import com.sunparlcompany.zkel.swipemenu.SwipeMenuItem;
import com.sunparlcompany.zkel.swipemenu.SwipeMenuListView;
import com.sunparlcompany.zkel.util.AppUtils;
import com.sunparlcompany.zkel.util.ConstUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */
public class BindDeviceActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private String[] appPermission = new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION};
    private String tag = BindDeviceActivity.class.getSimpleName();
    private static final long DELAY_TIME = 2000L;
    private static final long PERIOD_TIME = 5000L;
    private String sendTime = "123";
    private String deleteDeviceMac = "";
    private static final int REQUEST_CODE_PERMISSION = 1000;
    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;
    private DBDao dbDao;
    private SwipeMenuListView smlv_bind;
    private BindDeviceAdapter adapter;
    private List<DbDeviceEntity> dbDeviceList = null;
    private List<Device> bindDeviceList = null;
    private List<String> uuidList = null;
    private Device device = null;
    private long uuid = 0L;
    private long exitTime = 0L;
    private Timer mTimer = null;

    private  MyHandler mHandler = new MyHandler(this);


    private class MyHandler extends Handler{
        private WeakReference<BindDeviceActivity> weakReference = null;
        public MyHandler(BindDeviceActivity activity){
            weakReference = new WeakReference<BindDeviceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BindDeviceActivity activity = weakReference.get();
            if(activity == null){
                return;
            }
            switch (msg.what){
                case ConstUtil.START_LONGTOOTH_SUCCESS:
                    MyApp.showToast("成功连接长牙服务器");
                    break;
                case ConstUtil.ADDSERVICE_SUCCESS:
                    MyApp.showToast("开启服务成功");
                    break;
                case ConstUtil.RESET_DEVICE_SUCCESS:
                    String dataRes = msg.obj.toString();
                    try {
                        JSONObject jo = new JSONObject(dataRes);
                        int code = jo.getInt("CODE");
                        if(code == 0){
                            MyApp.showToast("设备恢复出厂设置成功");
//                            Toast.makeText(BindDeviceActivity.this,"设备恢复出厂设置成功",Toast.LENGTH_SHORT).show();
                        }else{
                            MyApp.showToast("设备恢复出厂设置失败");
//                            Toast.makeText(BindDeviceActivity.this,"设备恢复出厂设置失败",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case ConstUtil.RESET_DEVICE_FAIL:

                    break;
                case ConstUtil.QUERRY_DEVICE_SUCCESS:
                    String res = msg.obj.toString();
                    String[] result = res.split("==");
                    String ltid = result[1];
                    try {
                        JSONObject jsonObject = new JSONObject(result[0]);
                        int code = jsonObject.getInt("CODE");
                        Log.i(tag,"=======================result[0]:"+code+",result[1]:"+ltid+",bindDeviceListSize:"+bindDeviceList.size());

                        if(code == 0){
                            for(int i = 0;i< bindDeviceList.size();i++){
                                if(ltid.equals(bindDeviceList.get(i).getLtID())){
                                    bindDeviceList.get(i).setDeviceSta("在线");
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            for(int i = 0;i< bindDeviceList.size();i++){
                                if(ltid.equals(bindDeviceList.get(i).getLtID())){
                                    bindDeviceList.get(i).setDeviceSta("离线");
                                    adapter.notifyDataSetChanged();
                                }
                            }
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);
        checkAppPermission();
        initView();
        initLTService();

    }

    private void checkAppPermission() {
        int selfPermission = 0;
        if(Build.VERSION.SDK_INT > 23){
            for(int i = 0;i < appPermission.length;i++){
               selfPermission = ContextCompat.checkSelfPermission(this, appPermission[i]);
               if(selfPermission != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(appPermission,REQUEST_CODE_PERMISSION);
               }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"权限申请成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"权限申请失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initLTService() {
        MyLongToothService.sendDataFrame(BindDeviceActivity.this, ConstUtil.OP_START,"20180411","startservice");
        MyLongToothService.setMyLongToothListener(new MyLongToothService.MyLongToothListener() {
            @Override
            public void updateUI(Message msg) {
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        if(!AppUtils.isWifiConnected()){
            MyApp.showToast("未连接WiFi，请检查无线网络");
            return;
        }
        if(dbDeviceList.size() == 0){
            return;
        }
        if(mTimer == null){
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(tag,"2s到了，开始查询…");
                querryDeviceState();
            }
        },DELAY_TIME,PERIOD_TIME);

    }

    private void querryDeviceState() {
        Gson gson = new Gson();
        try {
            long lsTime = 0;
            String sendCMD = "";
            String querryDeviceLtid = "";
            for (int i = 0;i < bindDeviceList.size();i++){
                querryDeviceLtid = bindDeviceList.get(i).getLtID();
                lsTime = Long.parseLong(uuidList.get(i));
                sendCMD = gson.toJson(new LongToothModel(lsTime,ConstUtil.CMD_GET_WORKMODE));

                MyLongToothService.sendDataFrame(this,ConstUtil.OP_QUERRY,querryDeviceLtid,sendCMD);
                MyLongToothService.setMyLongToothListener(new MyLongToothService.MyLongToothListener() {
                    @Override
                    public void updateUI(Message msg) {
                        mHandler.sendMessage(msg);
                    }

                });
            }

        } catch (NumberFormatException e) {
            Log.i(tag," onResume NumberFormatException");
            e.printStackTrace();
        }

    }


    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);
        smlv_bind = findViewById(R.id.smlv_bind);

        tv_right.setOnClickListener(this);
        tv_title.setText("已绑定设备的列表");
        iv_back.setVisibility(View.INVISIBLE);
        tv_right.setText("添加设备");

    }

    private void initData() {
        initSwipeMenu();

        dbDao = new DBDao(BindDeviceActivity.this);
        dbDeviceList = dbDao.getAllDeviceInfo();
        Log.i(tag,"dbsize:"+dbDeviceList.size());

        smlv_bind.setOnItemClickListener(this);
        bindDeviceList = new ArrayList<Device>();
        uuidList = new ArrayList<String>();
        DbDeviceEntity dbDevice = null;
        for(int i = 0;i < dbDeviceList.size();i++){
            dbDevice = dbDeviceList.get(i);
            if(dbDevice.getBoundStatus().equals("bounded")){
                Log.i(tag,"deviceMAC--deviceUUID--deviceStatus:"+dbDevice.getMac()+"--"+dbDevice.getUuid()+"--"+dbDevice.getBoundStatus());
                device = new Device();
                device.setName(dbDevice.getName());
                device.setIp(dbDevice.getIp());
                device.setPort(dbDevice.getPort());
                device.setMac(dbDevice.getMac());
                device.setFirmwareRev(dbDevice.getFirmwareRev());
                device.setHardwareRev(dbDevice.getHardwareRev());
                device.setMicoosRev(dbDevice.getMicoosRev());
                device.setBoundStatus(dbDevice.getBoundStatus());
                device.setModel(dbDevice.getModel());
                device.setProtocol(dbDevice.getProtocol());
                device.setLtID(dbDevice.getLtID());
                device.setManufacturer(dbDevice.getManufacturer());
                device.setDeviceName(dbDevice.getDeviceName());
                device.setSeed(dbDevice.getSeed());
                device.setDeviceSta("离线");
                device.setIsSelected(false);

                bindDeviceList.add(device);
                uuidList.add(dbDevice.getUuid());
            }else{
                Log.i(tag,"unbounded");
            }
        }
        adapter = new BindDeviceAdapter(this,bindDeviceList);
        smlv_bind.setAdapter(adapter);
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    private void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(BindDeviceActivity.this);
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
                deleteItem .setBackground(R.drawable.selector_red);
                deleteItem.setWidth(dp2px(60));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setIcon(R.drawable.ic_action_delete);
                swipeMenu.addMenuItem(deleteItem);

            }
        };
        smlv_bind.setMenuCreator(creator);
        smlv_bind.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                {
                    //position为pullToRefreshSwipteMenuListView的item项的位置
                    Log.i(tag,"pos:"+position+",index:"+index);
                    deleteDeviceMac = bindDeviceList.get(position).getMac();
                    final String deleteDeviceLtid = bindDeviceList.get(position).getLtID();
                    final String uuid = uuidList.get(position);
                    final Gson gson = new Gson();
                    final long delUUID = Long.parseLong(uuid);
                    final String sendCMD = gson.toJson(new LongToothModel(delUUID,ConstUtil.CMD_DEVICE_RESETFACTORY));
                    Log.i(tag,"pos:"+position+",index:"+index+"delete device:"+deleteDeviceMac);
                    switch (index) {
                        case 0:
                            AlertDialog.Builder dialog = new AlertDialog.Builder(BindDeviceActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("删除设备"+deleteDeviceMac+" ? 将会恢复出厂设置,请谨慎操作！");
                            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        //删除数据库中的数据要指定条件（WhereBuilder）
                                        WhereBuilder b = WhereBuilder.b();
                                        b.and("mac","=",deleteDeviceMac);
                                        dbDao.getDBUtils().delete(DbDeviceEntity.class, b);
                                        bindDeviceList.remove(position);
                                        adapter.notifyDataSetChanged();
                                        MyApp.showToast("删除设备成功");
//                                        Log.i(tag,"删除成功");
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                        MyApp.showToast("删除设备失败");
//                                        Log.i(tag,"删除失败");
                                    }
                                    MyLongToothService.sendDataFrame(BindDeviceActivity.this,ConstUtil.OP_RESET,deleteDeviceLtid,sendCMD);
                                    MyLongToothService.setMyLongToothListener(new MyLongToothService.MyLongToothListener() {
                                        @Override
                                        public void updateUI(Message msg) {
                                            mHandler.sendMessage(msg);
                                        }
                                    });

                                }
                            });
                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.create().show();
                            break;

                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTimer != null){
            Log.i(tag,"onPause:定时器取消");
            mTimer.cancel();
            mTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
            Log.i(tag,"onDestroy:定时器销毁置空");
            mTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        if(dbDeviceList != null){
            dbDeviceList.clear();
            dbDeviceList = null;
        }
        if(bindDeviceList != null){
            bindDeviceList.clear();
            bindDeviceList = null;
        }
        if(uuidList != null){
            uuidList.clear();
            uuidList = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                startActivity(new Intent(BindDeviceActivity.this,AddDeviceActivity.class));
//                startActivity(new Intent(BindDeviceActivity.this,UnBindDeviceListActivity.class));
                break;
                default:break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device device = bindDeviceList.get(position);
        try {
            uuid = Long.parseLong(uuidList.get(position));
            Log.i(tag,"uuid:"+ String.valueOf(uuid));
            Intent toMainControlActivity = new Intent(BindDeviceActivity.this, MainActivity.class);
            toMainControlActivity.putExtra("device",device);
            toMainControlActivity.putExtra("UUID",uuid);
            startActivity(toMainControlActivity);
        } catch (NumberFormatException e) {
            Log.i(tag,"NumberFormatException");
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(BindDeviceActivity.this, "再按一次返回键退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    System.exit(0);
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
