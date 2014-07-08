package org.dian.torun.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by BaiYa on 2014/5/25.
 */
public class ToastUtils {

    public static void showToast(Context context, String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }

    public static void showToast(Context context, int content, int duration) {
        Toast.makeText(context, content, duration).show();
    }

    public static void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

}
