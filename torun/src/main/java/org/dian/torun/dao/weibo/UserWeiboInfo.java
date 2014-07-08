package org.dian.torun.dao.weibo;

import android.content.Context;

import org.dian.torun.bean.User;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by BaiYa on 2014/5/1.
 */
public class UserWeiboInfo {

    public static String getName(Context context) {
        return ShareSDK.getPlatform(context, SinaWeibo.NAME).getDb().getUserName();
    }

    public static String getUid(Context context) {
        return ShareSDK.getPlatform(context, SinaWeibo.NAME).getDb().getUserId();
    }

    public static User.Gender getGender(Context context) {
        String userGender = ShareSDK.getPlatform(context, SinaWeibo.NAME).getDb().getUserGender();
        User.Gender gender;
        if (userGender.equals("m")) {
            gender = User.Gender.MALE;
        } else if (userGender.equals("f")) {
            gender = User.Gender.FEMALE;
        } else {
            gender = User.Gender.UNKNOWN;
        }
        return gender;
    }

    public static String getIcon(Context context) {
        return ShareSDK.getPlatform(context, SinaWeibo.NAME).getDb().getUserIcon();
    }

}
