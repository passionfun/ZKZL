package com.sunparlcompany.zkel.model;

/**
 * Created by zhanghuanhuan on 2018/4/8.
 */

public class LongToothRspModel {
    public static final int BIND_FAILED = 1;
    public static final int EQUIPMENT_HAVE_BINDED = 2;
    public static final int SUCCEED = 0;
    private int CODE;
    private String OpMode;
    private String softVer;
    private int updateStat;

    public int getCODE()
    {
        return this.CODE;
    }

    public String getOpMode()
    {
        return this.OpMode;
    }

    public String getSoftVer()
    {
        return this.softVer;
    }

    public int getUpdateStat()
    {
        return this.updateStat;
    }

    public void setCODE(int paramInt)
    {
        this.CODE = paramInt;
    }

    public void setOpMode(String paramString)
    {
        this.OpMode = paramString;
    }

    public void setSoftVer(String paramString)
    {
        this.softVer = paramString;
    }

    public void setUpdateStat(int paramInt)
    {
        this.updateStat = paramInt;
    }
}
