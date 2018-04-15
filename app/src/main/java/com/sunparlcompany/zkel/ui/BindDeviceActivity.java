package com.sunparlcompany.zkel.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunparlcompany.zkel.R;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */
public class BindDeviceActivity extends AppCompatActivity implements View.OnClickListener{
    private String tag = BindDeviceActivity.class.getSimpleName();
    private String[] appPermission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int PERMISSION_COARSE_SUCCESS_CODE = 0;
    private final int PERMISSION_READ_PHONE_SUCCESS_CODE = 1;
    private final int PERMISSION_WRITE_STORAGE_SUCCESS_CODE = 2;
    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);
        initView();
        requestPer();
        initData();
    }

    private void requestPer() {
        for(String per:appPermission){
            if(ActivityCompat.checkSelfPermission(this,per ) != PackageManager.PERMISSION_GRANTED){
                //定位
                ActivityCompat.requestPermissions(BindDeviceActivity.this, new String[]{appPermission[0],appPermission[1],appPermission[2]},PERMISSION_COARSE_SUCCESS_CODE);
            }
        }
//        if(ActivityCompat.checkSelfPermission(this,appPermission[0] ) != PackageManager.PERMISSION_GRANTED){
//            //定位
//            ActivityCompat.requestPermissions(BindDeviceActivity.this, new String[]{appPermission[0]},PERMISSION_COARSE_SUCCESS_CODE);
//        }else if(ActivityCompat.checkSelfPermission(this,appPermission[1] ) != PackageManager.PERMISSION_GRANTED){
//            //读取手机状态
//            ActivityCompat.requestPermissions(BindDeviceActivity.this, new String[]{appPermission[1]},PERMISSION_READ_PHONE_SUCCESS_CODE);
//        }else{//读取手机外部存储
//            ActivityCompat.requestPermissions(BindDeviceActivity.this, new String[]{appPermission[2]},PERMISSION_WRITE_STORAGE_SUCCESS_CODE);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       switch (requestCode){
           case PERMISSION_COARSE_SUCCESS_CODE:
               Toast.makeText(BindDeviceActivity.this,"success.",Toast.LENGTH_SHORT).show();
               break;
//           case PERMISSION_READ_PHONE_SUCCESS_CODE:
//               if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                   Toast.makeText(BindDeviceActivity.this,"获取手机状态权限成功.",Toast.LENGTH_SHORT).show();
//               }
//               break;
//           case PERMISSION_WRITE_STORAGE_SUCCESS_CODE:
//               if(grantResults[2] == PackageManager.PERMISSION_GRANTED){
//                   Toast.makeText(BindDeviceActivity.this,"获取读取SD卡权限成功.",Toast.LENGTH_SHORT).show();
//               }
//               break;
//               default:break;
       }
    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);

        tv_right.setOnClickListener(this);
    }

    private void initData() {
        tv_title.setText("已绑定设备的列表");
        iv_back.setVisibility(View.INVISIBLE);
        tv_right.setText("添加设备");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                startActivity(new Intent(BindDeviceActivity.this,AddDeviceActivity.class));
                break;
                default:break;
        }
    }
}
