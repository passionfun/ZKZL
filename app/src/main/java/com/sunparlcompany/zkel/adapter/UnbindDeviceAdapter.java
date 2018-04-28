package com.sunparlcompany.zkel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class UnbindDeviceAdapter extends BaseAdapter {
    private int currentIndex = -1;
    private boolean isSelected = false;
    private String tag ="UnbindDeviceAdapter";
    private List<Device> deviceList;
    private LayoutInflater inflater;
    private Context mContext;
    public UnbindDeviceAdapter(Context mContext, List<Device> deviceList){
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
//    public void setSelect(int pos){
//        if(currentIndex == pos){
//            //如果当前项选择过，则改变其状态
//            isSelected = !isSelected;
//        }else{
//            currentIndex = pos;
//            isSelected = true;
//        }
//        notifyDataSetChanged();
//    }
//    public int getBindDeviceIndex(){
//        return currentIndex;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
        if(convertView == null){
            view = inflater.inflate(R.layout.item_unbind_device_info,parent,false);
            holder = new ViewHolder();
            holder.tv_deviceName = view.findViewById(R.id.tv_unBindDeviceName);
            holder.tv_deviceMac = view.findViewById(R.id.tv_unBindDeviceMac);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
//        if(currentIndex == position){
//            if(isSelected){
//                Log.i(tag,"当前选中的绑定设备为："+currentIndex+",mac:"+deviceList.get(currentIndex).getMac());
//                view.setBackgroundColor(Color.rgb(0, 172, 243));
//            }else{
//                view.setBackgroundColor(Color.TRANSPARENT);
//            }
//        }else{
//            view.setBackgroundColor(Color.TRANSPARENT);
//        }

        Device device = deviceList.get(position);
        if(device.getIsSelected()){
            view.setBackgroundColor(Color.rgb(0, 172, 243));
        }else{
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.tv_deviceName.setText(device.getDeviceName());
        holder.tv_deviceMac.setText(device.getMac());
        return view;
    }
     static class  ViewHolder{
        private TextView tv_deviceName;
        private TextView tv_deviceMac;
    }
}
