package org.dian.torun.support.push;

import android.content.Context;
import android.os.Handler;

import org.dian.torun.AppData;
import org.dian.torun.utils.Logger;
import org.dian.torun.utils.NetworkUtils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by BaiYa on 2014/5/28.
 */
public class JPushHelper {

    private static final String TAG = "JPush";

    private Context mContext;

    public JPushHelper(Context context) {
        mContext = context;
    }

    public void setAlias(String alias, TagAliasCallback tagAliasCallback) {
        JPushInterface.setAlias(mContext, alias, tagAliasCallback);
    }

    public void setAlias(String alias) {
        setAlias(alias, mAliasCallback);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Logger.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Logger.i(TAG, logs);
                    if (NetworkUtils.isConnected(AppData.getContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Logger.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Logger.e(TAG, logs);
            }

        }

    };

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Logger.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(AppData.getContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                default:
                    Logger.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
}
