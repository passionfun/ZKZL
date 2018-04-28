package com.sunparlcompany.zkel.model;

/**
 * Created by zhanghuanhuan on 2018/4/10.
 */

public class LongToothLightModel {
    private String CMD = "";
    private String SWITCH = "";
    private int BRIGHTNESS = 0;
    private long UUID = 0L;

    public LongToothLightModel() {
    }
//    public LongToothLightModel(String CMD, String SWITCH, int BRIGHTNESS, long UUID) {
//        this.CMD = CMD;
//        this.SWITCH = SWITCH;
//        this.BRIGHTNESS = BRIGHTNESS;
//        this.UUID = UUID;
//    }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String CMD) {
        this.CMD = CMD;
    }

    public String getSWITCH() {
        return SWITCH;
    }

    public void setSWITCH(String SWITCH) {
        this.SWITCH = SWITCH;
    }

    public int getBRIGHTNESS() {
        return BRIGHTNESS;
    }

    public void setBRIGHTNESS(int BRIGHTNESS) {
        this.BRIGHTNESS = BRIGHTNESS;
    }

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }
}
