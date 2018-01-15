package com.hojy.www.hojyupgrader163.application;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * Created by Ron on 2016/10/25.
 */

public class HojyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }
}
