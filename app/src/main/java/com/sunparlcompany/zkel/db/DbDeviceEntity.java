package com.sunparlcompany.zkel.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhanghuanhuan on 2018/4/2.
 */
@Table(name = "deviceinfo",onCreated = "")
public class DbDeviceEntity {
    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "ip")
    private String ip;
    @Column(name = "port")
    private int port;
    @Column(name = "mac")
    private String mac;
    @Column(name = "firmwareRev")
    private String firmwareRev;
    @Column(name = "hardwareRev")
    private String hardwareRev;
    @Column(name = "micoosRev")
    private String micoosRev;
    @Column(name = "boundStatus")
    private String boundStatus;
    @Column(name = "model")
    private String model;
    @Column(name = "protocol")
    private String protocol;
    @Column(name = "ltID")
    private String ltID;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "deviceName")
    private String deviceName;
    @Column(name = "seed")
    private String seed;
    public DbDeviceEntity(){

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
