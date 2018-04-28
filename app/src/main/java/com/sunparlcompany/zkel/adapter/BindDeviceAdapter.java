package com.sunparlcompany.zkel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.model.Device;

import java.util.List;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 *adapter里面设置一个变量int iSelect，记录你点击的item，然后在viewholder那里设置一个变量boolean bBgChange，记录当前item是否变色。
 * 然后如果你点击了某个item，就把那个item的position设置给adapter的int，然后getView的时候先判断，
 if（position == iSelsct）{ // 如果是当前点击的Item

 if(vh.bBgChang) { // 如果已经变了颜色
 // 换回原来的颜色
 } else {
 // 改变颜色
 }

 } else {  // 如果不是当前点击的Item
 // 换回原来的颜色
 }

 *
 */

public class BindDeviceAdapter extends BaseAdapter {
    private int currentIndex = 0;
    private String tag ="DeviceAdater";
    private List<Device> deviceList;
    private LayoutInflater inflater;
    private Context mContext;
    public BindDeviceAdapter(Context mContext, List<Device> deviceList){
        this.mContext = mContext;
        this.deviceList = deviceList;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return deviceList.size();
    }
    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
        if(convertView == null){
            view = inflater.inflate(R.layout.item_bind_device_info,parent,false);
            holder = new ViewHolder();
            holder.tv_deviceName = view.findViewById(R.id.tv_bindDeviceName);
            holder.tv_deviceMac = view.findViewById(R.id.tv_bindDeviceMac);
            holder.iv_deviceState = view.findViewById(R.id.iv_deviceState);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        Device device = deviceList.get(position);
        holder.tv_deviceName.setText(device.getDeviceName());
        holder.tv_deviceMac.setText(device.getMac());
        if(device.getDeviceSta().equals("离线")){
            holder.iv_deviceState.setImageResource(R.drawable.off_line);
        }else{
            holder.iv_deviceState.setImageResource(R.drawable.on_line);
        }
        return view;
    }
     static class  ViewHolder{
        private TextView tv_deviceName;
        private TextView tv_deviceMac;
        private ImageView iv_deviceState;
    }
}
