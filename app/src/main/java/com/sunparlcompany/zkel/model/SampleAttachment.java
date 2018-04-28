package com.sunparlcompany.zkel.model;

import android.util.Log;

import xpod.longtooth.LongToothAttachment;

/**
 * Created by zhanghuanhuan on 2018/4/9.
 */

public class SampleAttachment implements LongToothAttachment{

        private boolean ckHex = false;

        @Override
        public Object handleAttachment(Object... arg0) {
            // TODO Auto-generated method stub
            Log.i("SampleAttachment", "arg0:" + arg0);
            return arg0;
        }

        public void setCkHex(boolean ckHex) {
            this.ckHex = ckHex;
        }

        public boolean getCkHex() {
            return this.ckHex;
        }

}
