package org.dian.torun.bean;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import org.dian.torun.dao.InvitationDataHelper;
import org.dian.torun.utils.database.CursorHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BaiYa on 2014/4/10.
 * 约定信息
 * 约定数据库，通过ormlite包建立
 */
public class Invitation {

    private int id = -1;
    /**该安排的id*/
    private String uid;
    /**发起邀请的人id*/
    @SerializedName("user_id")
    private String sponsorId;
    /**受邀的人的信息, json格式*/
    @SerializedName("to_id")
    private User inviteeInfo;
    private String inviteeJson; // invitee 的json字符串
    /**约定时间*/
    private long time;
    /**约定地点*/
    private String place;
    /**附加内容*/
    private String content;
    /**创建时间*/
    private long create_time;

    /**邀请结果 0 失败,1成功,2默认*/
    private int result = 2;

    public Invitation() {
        // required
    }

    public static Invitation fromCursor(Cursor cursor) {
        Invitation invitation = new Invitation();
        invitation.setContent(CursorHelper.getString(cursor, InvitationDataHelper.InvitationDbInfo.EXTRA));
        invitation.setInviteeInfo(
                User.fromJson(CursorHelper.getString(cursor, InvitationDataHelper.InvitationDbInfo.INVITEEID)));
        invitation.setSponsorId(CursorHelper.getString(cursor, InvitationDataHelper.InvitationDbInfo.SPONSORID));
        invitation.setUid(CursorHelper.getString(cursor, InvitationDataHelper.InvitationDbInfo.UID));
        invitation.setTime(CursorHelper.getLong(cursor, InvitationDataHelper.InvitationDbInfo.TIME));
        invitation.setPlace(CursorHelper.getString(cursor, InvitationDataHelper.InvitationDbInfo.PLACE));
        invitation.setCreate_time(CursorHelper.getLong(cursor, InvitationDataHelper.InvitationDbInfo.CREATETIME));
        invitation.setResult(CursorHelper.getInteger(cursor, InvitationDataHelper.InvitationDbInfo.RESULT));
        return invitation;
    }

    public static Invitation fromJson(String jsonObject) {
        Invitation invitation = new Invitation();
        try {
            JSONObject object = new JSONObject(jsonObject);
            invitation.setUid(object.optString("invitation_id"));
            invitation.setTime(object.optLong("time"));
            invitation.setPlace(object.optString("place"));
            invitation.setContent(object.optString("content"));
            invitation.setCreate_time(object.optLong("create_time"));
            invitation.setInviteeInfo(User.fromJson(object.getJSONObject("to_id").toString()));
            invitation.setInviteeJson(object.getJSONObject("to_id").toString());
            invitation.setResult(object.optInt("invitation_result", 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return invitation;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public User getInviteeInfo() {
        return inviteeInfo;
    }

    public void setInviteeInfo(User inviteeInfo) {
        this.inviteeInfo = inviteeInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInviteeJson() {
        return inviteeJson;
    }

    public void setInviteeJson(String inviteeJson) {
        this.inviteeJson = inviteeJson;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
