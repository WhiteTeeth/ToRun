package org.dian.torun;

import android.app.Application;
import android.content.Context;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class AppData extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

}
