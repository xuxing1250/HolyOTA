package com.hojy.www.hojyupgrader163.utils;

import android.util.Log;

/**
 * Created by Ron on 2016/11/16.
 */

public class HojyLoger {
    static String Tag = "HojyUpgrade163LogFlag";
    public static void d(String tag,String message){
        Log.d(Tag,tag+"->"+message);
    }
    public static void i(String tag,String message){
        Log.i(Tag,tag+"->"+message);
    }
    public static void e(String tag,String message){
        Log.e(Tag,tag+"->"+message);
    }
}
