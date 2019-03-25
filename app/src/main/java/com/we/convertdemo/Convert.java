package com.we.convertdemo;

/**
 * @author created by honghengqiang
 * @date 2019/3/24 10:18
 */
public class Convert {
    static {
        System.loadLibrary("lame");
    }

    public native String stringFromJNI();
    public native void init(String pcmPath,int audioChannels,int bitRate,int sampleRate,String mp3Path);
    public native void encode();
    public native void destroy();
}
