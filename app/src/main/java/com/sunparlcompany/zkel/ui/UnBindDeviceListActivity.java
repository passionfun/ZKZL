package com.sunparlcompany.zkel.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunparlcompany.zkel.MyApp;
import com.sunparlcompany.zkel.MyLongToothService;
import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.adapter.UnbindDeviceAdapter;
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
import com.sunparlcompany.zkel.util.GetCurrentTimeUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
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

public class UnBindDeviceListActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,MyLongToothService.MyLongToothListener{
    private static final String tag = UnBindDeviceListActivity.class.getSimpleName();
    private static final int BIND_DEVICE_SUCCESS = 100;
    private static final int BIND_DEVICE_FAIL = 200;


//    private String ltid = "2001110001.1.2984.725.511";
    private String sendCMD = "";
    private String bindDeviceLtid = "";
    private long sendTime = 0L;

    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;

    private DBDao dbDao = null;
    private SwipeMenuListView smlv_unbind;
    private List<DbDeviceEntity> dbDeviceList = null;
    private List<Device> deviceList = null;
    private List<String> bindDevices = null;
    private List<String> bindFailDevices = null;//绑定失败的设备

    private Device device;
    private UnbindDeviceAdapter adapter;
    private Gson gson = null;
    private int deviceSize = 0;
    private int bindTotalSize = 0;
    private int bindFailSize = 0;
    private int bindSuccessSize = 0;
    private int resCount = 0;

    private boolean[] isChecked = null;
    private Timer mBindTimer = null;
    private long DELAY_TIME = 1000L;
    private long PERIOD_TIME = 3000L;

    private ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind_devicelist);
        initalView();
        initData();
    }



    private void initData() {
        tv_title.setText("未绑定设备的列表");
        iv_back.setImageResource(R.drawable.top_back);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("绑定");
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        initSwipeMenu();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        gson = new Gson();
        bindDevices = new ArrayList<>();
        bindFailDevices = new ArrayList<>();
        //开始连接长牙服务器（初始化操作）
//        if(!LongToothUtil.isConnnectedLT){
//            LongToothUtil.longToothInit();
//        }
//        Log.i(tag,"isconnected:"+LongToothUtil.isConnnectedLT);


        dbDao = new DBDao(this);
        smlv_unbind.setOnItemClickListener(this);

        dbDeviceList = dbDao.getAllDeviceInfo();
        DbDeviceEntity dbDevice = null;
        Log.i(tag,"数据库中的设备大小："+dbDeviceList.size());
        deviceList = new ArrayList<Device>();
        for(int i = 0;i < dbDeviceList.size();i++){
            device = new Device();
            dbDevice = dbDeviceList.get(i);
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
            device.setIsSelected(false);
            deviceList.add(device);
        }
        deviceSize = deviceList.size();

        adapter = new UnbindDeviceAdapter(this,deviceList);
        smlv_unbind.setAdapter(adapter);
        isChecked = new boolean[deviceSize];


    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    private void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(UnBindDeviceListActivity.this);
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
        smlv_unbind.setMenuCreator(creator);
        smlv_unbind.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                {
                    //position为pullToRefreshSwipteMenuListView的item项的位置
                    Log.i(tag,"pos:"+position+",index:"+index);
                   final String deleteDeviceMac = deviceList.get(position).getMac();
                    Log.i(tag,"pos:"+position+",index:"+index+"delete device:"+deleteDeviceMac);
                    switch (index) {
                        case 0:
                            AlertDialog.Builder dialog = new AlertDialog.Builder(UnBindDeviceListActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("删除设备"+deleteDeviceMac+"?");
                            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
//                                        dbDao.getDBUtils().delete(deviceList.get(position));\
                                        //删除数据库中的数据要指定条件（WhereBuilder）
                                        WhereBuilder b = WhereBuilder.b();
                                        b.and("mac","=",deleteDeviceMac);
                                        dbDao.getDBUtils().delete(DbDeviceEntity.class, b);
                                        deviceList.remove(position);
                                        adapter.notifyDataSetChanged();
                                        MyApp.showToast("删除设备成功");
//                                        Log.i(tag,"删除成功");
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                        MyApp.showToast("删除设备失败");
//                                        Log.i(tag,"删除失败");
                                    }
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

    private void initalView() {
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);
        smlv_unbind = findViewById(R.id.smlv_unbind);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        if(mBindTimer != null){
            Log.i(tag,"onDestory定时器取消了");
            mBindTimer.cancel();
            mBindTimer = null;
        }
        if(pd.isShowing() && pd != null){
            pd.dismiss();
            pd = null;
        }
        if(bindDevices != null){
            bindDevices.clear();
            bindDevices = null;
        }
        if(deviceList != null){
            deviceList.clear();
            deviceList = null;
        }
        if(dbDeviceList != null){
            dbDeviceList.clear();
            dbDeviceList = null;
        }
        if(bindFailDevices != null){
            bindFailDevices.clear();
            bindFailDevices = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!AppUtils.isWifiConnected()){
            MyApp.showToast("未连接WiFi，请检查无线网络");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                startActivity(new Intent(UnBindDeviceListActivity.this,AddDeviceActivity.class));
                finish();
                break;
            case R.id.tv_next:
                if(!AppUtils.isWifiConnected()){
                    MyApp.showToast("未连接WiFi，请检查无线网络");
                    return;
                }
                //当用户没有选择带绑定的设备时提示用户
                if(bindDevices.size() == 0){
                    MyApp.showToast("请选择待绑定的设备");
                    return;
                }
                mBindTimer = new Timer();
//                MyApp.showToast("正在绑定，请稍后…");
//                Toast.makeText(UnBindDeviceListActivity.this,"开始绑定",Toast.LENGTH_SHORT).show();
//                int bindDeviceIndex = adapter.getBindDeviceIndex();
                pd.setMessage("正在绑定，请稍后…");
                pd.show();
                bindTotalSize = bindDevices.size();
                for (int i = 0;i< bindDevices.size();i++){
                    Log.i(tag,"最终选择绑定的设备有："+bindDevices.get(i));
                    bindDeviceLtid = bindDevices.get(i);
                    try {
                        sendTime = Long.parseLong(GetCurrentTimeUtil.getCurrentDateTimes());
                        Log.i(tag,"绑定的设备："+bindDevices.get(i)+",开始绑定的时间："+ String.valueOf(sendTime));
                        sendCMD = gson.toJson(new LongToothModel(sendTime,ConstUtil.CMD_DEVICE_BIND));
                        MyLongToothService.sendDataFrame(this,ConstUtil.OP_BIND,bindDeviceLtid,sendCMD);
                        MyLongToothService.setMyLongToothListener(this);
                    } catch (NumberFormatException e) {
                        Log.i(tag,"NumberFormatException");
                        e.printStackTrace();
                    }
                }

                break;
                default:break;
        }
    }

    private MyHandDataHandler myHandler = new MyHandDataHandler(this);

    @Override
    public void updateUI(Message msg) {
        myHandler.sendMessage(msg);
    }

    private class MyHandDataHandler extends Handler{
        private WeakReference<UnBindDeviceListActivity> weakReference = null;
        public MyHandDataHandler(UnBindDeviceListActivity activity){
            weakReference = new WeakReference<UnBindDeviceListActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
           UnBindDeviceListActivity activity = weakReference.get();
           if(activity == null){
               return;
           }
           switch (msg.what){
               case ConstUtil.BIND_DEVICE_SUCCESS:
                   boolean isBindFailLastOne = false;
                   String res = msg.obj.toString();
                   String[] result = res.split("==");
                   final String ltid = result[1];
                   try {
                       JSONObject jsonObject = new JSONObject(result[0]);
                       int code = jsonObject.getInt("CODE");
                       Log.i(tag,"====res=============result[0]:"+res+",code:"+code+",result[1]:"+ltid+",bindDeviceListSize:"+bindDevices.size());

                       if(code == 0){
                           for(int i = 0;i< bindDevices.size();i++){
                               if(ltid.equals(bindDevices.get(i))){
                                   bindSuccessSize++;
                                   //成功之后更新数据库中的boundStatus字段和uuid字段信息
                                   WhereBuilder b = WhereBuilder.b();
                                   b.and("ltID","=",ltid);
                                   KeyValue uuidKeyValue = new KeyValue("uuid",sendTime);
                                   KeyValue boundedKeyValue = new KeyValue("boundStatus","bounded");
                                   dbDao.getDBUtils().update(DbDeviceEntity.class,b,uuidKeyValue,boundedKeyValue);
                                   if(bindFailDevices.size()>0){
                                       Log.i(tag,"移除之前绑定失败的设备："+ltid);
                                       bindFailDevices.remove(ltid);
                                   }
                                   Log.i(tag,"绑定成功了设备："+bindDevices.get(i)+",存入数据库中的绑定的时间："+ String.valueOf(sendTime));
                               }
                           }
                       }else{
                           //部分失败code = 0
                           //添加绑定失败的设备到列表并去重
                           if(bindFailDevices.size() == 0){
                               bindFailDevices.add(ltid);
                           }else{
                               for(int i = 0;i<bindFailDevices.size();i++){
                                   if(!ltid.equals(bindFailDevices.get(i))){//判断集合中没有此元素才添加，防止重复
                                       bindFailDevices.add(ltid);
                                   }
                               }
                           }
                           //记录绑定失败设备的台数
                           bindFailSize = bindTotalSize - bindSuccessSize;
                           //对绑定失败的设备重新发送绑定指令，发三次
                           for(int i = 0;i < bindFailDevices.size();i++){
                               final String bindFail =  bindDevices.get(i);
                               Log.i(tag,"绑定失败设备有："+(bindFailDevices.size() == bindFailSize ? "数量相等":"数量不等")+bindFailDevices.size()+"台："+bindFail);
                               try {
//                               sendTime = Long.parseLong(GetCurrentTimeUtil.getCurrentDateTimes());
                                   Log.i(tag,"绑定的设备："+bindFail+",开始绑定的时间："+ String.valueOf(sendTime));
                                   if(ltid.equals(bindFail)){
                                       resCount++;
                                       if(resCount == 3){//对绑定失败的设备进行绑定3次
                                           resCount = 0;
                                           Log.i(tag,"已经绑定了3次的LTID:"+ltid);
                                           if(ltid.equals(bindFailDevices.get(0))){
                                               isBindFailLastOne = true;
                                           }

                                       }else{
                                           mBindTimer.schedule(new TimerTask() {
                                               @Override
                                               public void run() {
                                                   Log.i(tag,"绑定定时器启动了……"+bindFail);
                                                   sendCMD = gson.toJson(new LongToothModel(sendTime,ConstUtil.CMD_DEVICE_BIND));
                                                   MyLongToothService.sendDataFrame(UnBindDeviceListActivity.this,ConstUtil.OP_BIND,bindFail,sendCMD);
                                                   MyLongToothService.setMyLongToothListener(UnBindDeviceListActivity.this);
                                               }
                                           },0L,PERIOD_TIME);


                                       }

                                   }
                               } catch (NumberFormatException e) {
                                   Log.i(tag,"NumberFormatException");
                                   e.printStackTrace();
                               }
                           }
                       }

                       if(bindTotalSize == bindSuccessSize){//全部绑定成功
                           pd.dismiss();
                           MyApp.showToast("设备绑定成功");
                           finish();
                       }else{
                           if(isBindFailLastOne){
                               pd.dismiss();
                               MyApp.showToast("总共选择了"+bindTotalSize+"台设备进行绑定，成功绑定"+bindSuccessSize+"台设备。");
                               Log.i(tag,"isBindFailLastOne:"+isBindFailLastOne+"总共选择了"+bindTotalSize+"台设备进行绑定，成功绑定"+bindSuccessSize+"台设备。");
                               finish();
                           }else{
                               Log.i(tag,"30s开始");
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       Log.i(tag,"30s结束，定时器关闭");
                                       if(mBindTimer != null){
                                           mBindTimer.cancel();
                                           mBindTimer = null;
                                       }
                                       //30s到了之后结束绑定，给出提示
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               pd.dismiss();
                                               MyApp.showToast("总共选择了"+bindTotalSize+"台设备进行绑定，成功绑定"+bindSuccessSize+"台设备。");
                                               finish();
                                           }
                                       });
                                   }
                               },30*1000L);
                           }
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                       Log.i(tag,"jsonException");
                   }catch (DbException e) {
                       e.printStackTrace();
                       Log.i(tag,"DbException");
                   }

                   break;
                   default:break;
           }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(tag,"pos:"+position);
        String selectLtid = deviceList.get(position).getLtID();
        if(isChecked[position]){
            isChecked[position] = false;
            deviceList.get(position).setIsSelected(false);
            bindDevices.remove(selectLtid);
        }else{
            isChecked[position] = true;
            bindDevices.add(selectLtid);
            deviceList.get(position).setIsSelected(true);
        }
        adapter.notifyDataSetChanged();
//       adapter.setSelect(position);
    }

}
