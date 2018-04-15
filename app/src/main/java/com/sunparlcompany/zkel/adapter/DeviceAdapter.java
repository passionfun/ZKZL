package com.sunparlcompany.zkel.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunparlcompany.zkel.R;
import com.sunparlcompany.zkel.model.Device;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by zhanghuanhuan on 2018/4/3.
 */

public class DeviceAdapter extends BaseAdapter {
    private List<Device> deviceList;
    private LayoutInflater inflater;
    private Context mContext;
    public DeviceAdapter(Context mContext,List<Device> deviceList){
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
            view = inflater.inflate(R.layout.item_device_info,parent,false);
            holder = new ViewHolder();
            holder.tv_deviceName = view.findViewById(R.id.tv_deviceName);
            holder.tv_deviceMac = view.findViewById(R.id.tv_deviceMac);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Device device = deviceList.get(position);
        holder.tv_deviceName.setText(device.getDeviceName());
        holder.tv_deviceMac.setText(device.getMac());
        return view;
    }
     static class  ViewHolder{
        private TextView tv_deviceName;
        private TextView tv_deviceMac;
    }
}
