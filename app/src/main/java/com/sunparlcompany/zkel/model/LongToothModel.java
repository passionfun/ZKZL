package com.sunparlcompany.zkel.model;

/**
 * Created by zhanghuanhuan on 2018/4/8.
 */

public class LongToothModel {
    private long UUID = 0L;
    private String CMD = "";

    public LongToothModel(long UUID, String CMD) {
        this.UUID = UUID;
        this.CMD = CMD;
    }

    public long getUUID() {
        return UUID;
    }

    public void setUUID(long UUID) {
        this.UUID = UUID;
    }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String CMD) {
        this.CMD = CMD;
    }
}
