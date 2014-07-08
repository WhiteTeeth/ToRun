package org.dian.torun.dao.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.dian.torun.bean.User;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by BaiYa on 2014/4/30.
 */
public class AccountKeeper {

    private static final String PREFERENCES_NAME = "org_dian.torun_dao_user";

    private static final String KEY_UID = "user_id";

    private static final String KEY_WEIBO_ID = "weibo_id";

    private static final String KEY_NAME = "name";

    private static final String KEY_SEX = "sex";

    private static final String KEY_AGE = "age";

    private static final String KEY_HEAD_URL = "head_url";

    private static final String KEY_RUN_TIME = "run_time";

    private static final String KEY_LAST_LOGIN_TIME = "last_login_time";

    private static final String KEY_LAST_LOGIN_PLACE = "last_login_place";

    private static final String KEY_LONGITUDE = "longitude";

    private static final String KEY_LATITUDE = "latitude";

    private static final String KEY_ACCURACY = "accuracy";


    public static void writeAccountInfo(Context context, User user) {
        if (user == null) return;

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(KEY_UID, user.getUid())
                .putString(KEY_WEIBO_ID, user.getWeiboId())
                .putString(KEY_NAME, user.getName())
                .putString(KEY_SEX, user.getGender().toString())
                .putInt(KEY_AGE, user.getAge())
                .putString(KEY_HEAD_URL, user.getHeadImg())
                .putString(KEY_RUN_TIME, user.getRuntimeJson())
                .putLong(KEY_LAST_LOGIN_TIME, user.getLast_login_time())
                .putString(KEY_LAST_LOGIN_PLACE, user.getLast_login_place())
                .putFloat(KEY_LONGITUDE, (float) user.getLongitude())
                .putFloat(KEY_LATITUDE, (float) user.getLatitude())
                .putInt(KEY_ACCURACY, user.getAccuracy())
                .commit();
    }


    // 返回user信息，uid可能为空
    public static User readAccountInfo(Context context) {
        User user = new User();

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        user.setUid(pref.getString(KEY_UID, ""));
        user.setWeiboId(pref.getString(KEY_WEIBO_ID, ""));
        user.setName(pref.getString(KEY_NAME, ""));
        user.setGender(pref.getString(KEY_SEX, User.Gender.UNKNOWN.toString()));
        user.setHeadImg(pref.getString(KEY_HEAD_URL, ""));
        user.setAge(pref.getInt(KEY_AGE, 0));
        user.setRunTime(pref.getString(KEY_RUN_TIME, ""));
        user.setLast_login_time(pref.getLong(KEY_LAST_LOGIN_TIME, 0));
        user.setLast_login_place(pref.getString(KEY_LAST_LOGIN_PLACE, ""));
        user.setLongitude(pref.getFloat(KEY_LONGITUDE, -1));
        user.setLatitude(pref.getFloat(KEY_LATITUDE, -1));
        user.setAccuracy(pref.getInt(KEY_ACCURACY, 0));

        return user;
    }

    // 如果uid为空，则返回weibo_id，因为uid和weibo_id值一样
    public static String getUid(Context context) {
        User user = readAccountInfo(context);
        String uid = TextUtils.isEmpty(user.getUid()) ? user.getWeiboId() : user.getUid();
        return uid;
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }


    public static String getWeiboId(Context context) {
        return ShareSDK.getPlatform(context, SinaWeibo.NAME).getDb().getUserId();
    }

}
