package org.dian.torun.support.account;

import android.content.Context;

import com.android.volley.Response;

import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.vendor.TorunApi;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by BaiYa on 2014/5/1.
 */
public class AccountHelper {

    /**
     * 账号是否授权
     * @return
     */
    public static boolean isAuthorize(Context context) {
        boolean isAuthorize = ShareSDK.getPlatform(context, SinaWeibo.NAME).isValid();
        return isAuthorize;
    }

    /**
     * 用户是否注册
     * @param context
     * @return
     */
    public static boolean isRegister(Context context) {
        User user = AccountKeeper.readAccountInfo(context);
        boolean isRegister = !(user.getUid().isEmpty() && user.getWeiboId().isEmpty());
        return isRegister;
    }

    /**
     * 新浪账号 授权
     * @param context
     * @param listener
     */
    public static void authorize(final Context context, PlatformActionListener listener) {
        Platform weibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
        weibo.setPlatformActionListener(listener);
        weibo.authorize();
    }


    /**
     * 使用微博账号注册用户
     * @param context
     * @param user
     * @param listener
     * @param errorListener
     */
    public static void register(final Context context, final User user, Response.Listener listener, Response.ErrorListener errorListener) {
        TorunApi.register(context, user, listener, errorListener);
    }

    public static void logout(final Context context) {
        AccountKeeper.clear(context);
    }
}
