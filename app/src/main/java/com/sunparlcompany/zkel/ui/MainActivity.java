package com.sunparlcompany.zkel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sunparlcompany.zkel.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_back;
    private Button btn_controlDevice;
    private SeekBar sb_light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initalView();
        initData();
    }


    private void initData() {
        tv_title.setText("设备详情");
        iv_back.setVisibility(View.INVISIBLE);
        btn_controlDevice.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        iv_back.setOnClickListener(this);

    }

    private void initalView() {
        tv_title = findViewById(R.id.tv_title);
        tv_right = findViewById(R.id.tv_next);
        iv_back = findViewById(R.id.iv_back);
        btn_controlDevice = findViewById(R.id.btn_controlDevice);
        sb_light = findViewById(R.id.seekBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_controlDevice:
                break;
            case R.id.tv_next:
                startActivity(new Intent(MainActivity.this,AddDeviceActivity.class));
                break;
            case R.id.iv_back:
                startActivity(new Intent(MainActivity.this,UnBindDeviceListActivity.class));
                break;
                default:break;
        }
    }
}
