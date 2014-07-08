package org.dian.torun.bean;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.dao.FriendDataHelper;
import org.dian.torun.utils.database.CursorHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiYa on 2014/4/9.
 * 用户信息类
 * 用户好友数据库，通过ormlite包建立
 * TODO 完善跑步时间部分
 */
public class User implements Serializable{

    private int id = -1;
    /**weibo的id，在需要时赋值、*/
    private String weiboId = "";
    /**Unique id*/
    @SerializedName("user_id")
    private String uid = "";
    /**名字*/
    private String name = "";
    /**性别*/
    private Gender gender = Gender.UNKNOWN;
    /**年龄*/
    private int age;
    /**头像图片路径*/
    private String headImg = "";

    @SerializedName("run_time")
    private List<Runtime> runTime = new ArrayList<Runtime>();

    private long last_login_time;
    private String last_login_place;

    private double longitude;
    private double latitude;
    private int accuracy;

    public User() {
        // required
    }

    // TODO
    public static User fromJson(String jsonObject) {
        User user = new User();
        try {
            JSONObject object = new JSONObject(jsonObject);
            user.setUid(object.optString("user_id"));
            user.setName(object.optString("name"));
            user.setGender(object.optString("sex"));
            user.setAge(object.optInt("age"));
            user.setHeadImg(object.optString("user_head"));
            user.setRunTime(object.optString("run_time"));
            user.setLast_login_time(object.optLong("last_login_time"));
            user.setLast_login_place(object.optString("last_login_place"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    // 将sex转化为Gender型 并赋值
    public void setGender(String sex) {
        Gender gender;
        try {
            gender = Gender.valueOf(sex.toUpperCase());
        } catch (IllegalArgumentException e) {
            gender = Gender.UNKNOWN;
        }
        setGender(gender);
    }

    // TODO
    public void setRunTime(String runtimeJson) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Runtime>>(){}.getType();
        List<Runtime> runTime = gson.fromJson(runtimeJson, listType);
        if (runTime != null) {
            runTime.remove(null);
            this.runTime = runTime;
        } else {
            this.runTime = new ArrayList<Runtime>();
        }
    }

    public static User fromCursor(Cursor cursor) {
        User user = new User();
        user.setId(CursorHelper.getInteger(cursor, FriendDataHelper.FriendDbInfo._ID));
        user.setUid(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.UID));
        user.setAge(CursorHelper.getInteger(cursor, FriendDataHelper.FriendDbInfo.AGE));
        user.setName(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.NAME));
        user.setHeadImg(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.HEADIMG));
        user.setGender(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.SEX));
        user.setRunTime(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.RUN_TIME));
        user.setLast_login_time(CursorHelper.getInteger(cursor, FriendDataHelper.FriendDbInfo.LAST_LOGIN_TIME));
        user.setLast_login_place(CursorHelper.getString(cursor, FriendDataHelper.FriendDbInfo.LAST_LOGIN_PLACE));
        user.setLongitude(CursorHelper.getDouble(cursor, FriendDataHelper.FriendDbInfo.LONGITUDE));
        user.setLatitude(CursorHelper.getDouble(cursor, FriendDataHelper.FriendDbInfo.LATITUDE));
        user.setAccuracy(CursorHelper.getInteger(cursor, FriendDataHelper.FriendDbInfo.ACCURACY));
        return user;
    }

    public String getRuntimeJson() {
        return getGson().toJson(runTime);
    }

    // 将User对象转化为json字符串并转回，用于intent中传值
    public String toJson() {
        return getGson().toJson(this);
    }

    public static User fromJsonByGson(String jsonObject) {
        return getGson().fromJson(jsonObject, User.class);
    }

    public static Gson getGson() {
        Gson gson = new Gson();
        return gson;
    }


    public enum Gender {
        UNKNOWN(R.string.gender_unknown),
        MALE(R.string.gender_male),
        FEMALE(R.string.gender_female);

        private int mName;

        Gender(int name) {
            mName = name;
        }

        public String getName() {
            return AppData.getContext().getString(mName);
        }

    }


    /**
     * 跑步时间段信息
     */
    public enum Runtime {
        EARLY_MORNING(R.string.runtime_earlymorning),
        MORNING(R.string.runtime_morning),
        NOON(R.string.runtime_noon),
        AFTERNOON(R.string.runtime_afternoon),
        EVENING(R.string.runtime_evening),
        NIGHT(R.string.runtime_night);

        private int mName;
        Runtime(int name) {
            mName = name;
        }

        public String getName() {
            return AppData.getContext().getString(mName);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public List<Runtime> getRunTime() {
        return runTime;
    }

    public void setRunTime(List<Runtime> runTime) {
        this.runTime = runTime;
    }

    public long getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(long last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_login_place() {
        return last_login_place;
    }

    public void setLast_login_place(String last_login_place) {
        this.last_login_place = last_login_place;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
}

