package com.sunparlcompany.zkel.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhanghuanhuan on 2018/4/2.
 */

public class Device implements Serializable{

    /**
     * Name : EMW3080B Module#152890
     * IP : 192.168.100.194
     * Port : 8000
     * MAC : B0:F8:93:15:28:90
     * Firmware Rev : 1.0.20
     * Hardware Rev : 3080B
     * MICO OS Rev : 3080B002.013
     * BOUND STATUS : bounded
     * Model : EMW3080B
     * Protocol : com.mxchip.basic
     * LTID : 2001110001.1.2353.358.104
     * Manufacturer : MXCHIP Inc.
     * DEVNAME : WG301_2890
     * Seed : 3
     */

    @SerializedName("Name")
    private String name;
    @SerializedName("IP")
    private String ip;
    @SerializedName("Port")
    private int port;
    @SerializedName("MAC")
    private String mac;
    @SerializedName("Firmware Rev")
    private String firmwareRev;
    @SerializedName("Hardware Rev")
    private String hardwareRev;
    @SerializedName("MICO OS Rev")
    private String micoosRev;
    @SerializedName("BOUND STATUS")
    private String boundStatus;
    @SerializedName("Model")
    private String model;
    @SerializedName("Protocol")
    private String protocol;
    @SerializedName("LTID")
    private String ltID;
    @SerializedName("Manufacturer")
    private String manufacturer;
    @SerializedName("DEVNAME")
    private String deviceName;
    @SerializedName("Seed")
    private String seed;
    @SerializedName("deviceSta")
    private String deviceSta;

    @SerializedName("isSelected")
    private boolean isSelected;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getFirmwareRev() {
        return firmwareRev;
    }

    public void setFirmwareRev(String firmwareRev) {
        this.firmwareRev = firmwareRev;
    }

    public String getHardwareRev() {
        return hardwareRev;
    }

    public void setHardwareRev(String hardwareRev) {
        this.hardwareRev = hardwareRev;
    }

    public String getMicoosRev() {
        return micoosRev;
    }

    public void setMicoosRev(String micoosRev) {
        this.micoosRev = micoosRev;
    }

    public String getBoundStatus() {
        return boundStatus;
    }

    public void setBoundStatus(String boundStatus) {
        this.boundStatus = boundStatus;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getLtID() {
        return ltID;
    }

    public void setLtID(String ltID) {
        this.ltID = ltID;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getDeviceSta() {
        return deviceSta;
    }

    public void setDeviceSta(String deviceSta) {
        this.deviceSta = deviceSta;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
