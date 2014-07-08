package org.dian.torun.vendor;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.dian.torun.bean.User;
import org.dian.torun.model.RequestManager;
import org.dian.torun.utils.Logger;
import org.dian.torun.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class TorunApi {

    private static final String BASE_URL = "http://162.243.143.103/";

    public static final String LOGIN = BASE_URL + "yuepao_login.php";

    public static final String REGISTER = BASE_URL + "yuepao_register.php";

    public static final String USER_UPDATE = BASE_URL + "yuepao_userdataupdate.php";

    public static final String USER_SHOW = BASE_URL + "yuepao_userdataget.php";

    public static final String INVITATION_CREATE = BASE_URL + "yuepao_new_invitation.php";

    public static final String INVITATION_EDIT = BASE_URL + "yuepao_edit_invitation.php";

    public static final String INVITATION_LIST = BASE_URL + "yuepao_get_invitation_list.php";

    public static final String INVITATION_RESULT = BASE_URL + "yuepao_set_invitation_result.php";

    public static final String FRIEND_NEW = BASE_URL + "yuepao_add_friends.php";

    public static final String FRIEND_REMOVE = BASE_URL + "yuepao_delete_friends.php";

    public static final String FRIEND_LIST = BASE_URL + "yuepao_get_friends_list.php";

    public static final String LOCATION_UPDATE = BASE_URL + "yuepao_update_login_message.php";

    public static final String NEIGHBOR = BASE_URL + "yuepao_get_neighbor.php";

    /**
     * 判断是否注册
     * @param weibo_id
     * @param listener
     * @param errorListener
     */
    public static void login(Object tag, String weibo_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("weibo_id", weibo_id);
        Request request = newRequest(LOGIN, body, listener, errorListener);
        request(request, tag);
    }

    /**
     * 更改用户信息
     * @param weibo_id
     * @param name
     * @param gender
     * @param age
     * @param run_time
     * @param listener
     * @param errorListener
     */
    public static void register(Object tag, String weibo_id, String name, User.Gender gender, int age, List<User.Runtime> run_time, String headUrl, Response.Listener listener, Response.ErrorListener errorListener) {
        StringBuilder runtimeStr = new StringBuilder();
        for (User.Runtime runtime : run_time) {
            runtimeStr.append(runtime).append(" ");
        }
        if (runtimeStr.length() > 0) {
            runtimeStr.deleteCharAt(runtimeStr.length()-1);
        }
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", weibo_id);
        body.put("name", name);
        body.put("sex", gender.toString());
        body.put("age", String.valueOf(age));
        body.put("run_time", runtimeStr.toString());
        body.put("user_head", headUrl);
        Logger.i("to run", Utils.printMap(body));
        Request request = newRequest(REGISTER, body, listener, errorListener);
        request(request, tag);
    }

    public static void register(Object tag, User user, Response.Listener listener, Response.ErrorListener errorListener) {
        register(tag, user.getWeiboId(), user.getName(), user.getGender(), user.getAge(), user.getRunTime(), user.getHeadImg(), listener, errorListener);
    }

    /**
     * 更改用户信息
     * @param user_id
     * @param name
     * @param gender
     * @param age
     * @param run_day
     * @param run_time
     * @param listener
     * @param errorListener
     */
    public static void userUpdate(Object tag, String user_id, String name, User.Gender gender, int age, String run_day, String run_time, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", user_id);
        body.put("name", name);
        body.put("sex", String.valueOf(gender.ordinal()));
        body.put("age", String.valueOf(age));
        body.put("run_day", run_day);
        body.put("run_time", run_time);
        Request request = newRequest(USER_UPDATE, body, listener, errorListener);
        request(request, tag);
    }


    /**
     * 获取用户信息
     * @param user_id
     * @param listener
     * @param errorListener
     */
    public static void userShow(Object tag, String user_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", user_id);
        Request request = newRequest(USER_SHOW, body, listener, errorListener);
        request(request, tag);
    }

    /**
     * 新建一条约定
     * @param tag
     * @param from_id
     * @param to_id
     * @param time 毫秒单位
     * @param place
     * @param content
     * @param listener
     * @param errorListener
     */
    public static void createInvitation(Object tag, String from_id, String to_id, long time, String place, String content, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("from_id", from_id);
        body.put("to_id", to_id);
        body.put("time", String.valueOf(time / 1000));
        body.put("place", place);
        body.put("content", content);
        Request request = newRequest(INVITATION_CREATE, body, listener, errorListener);
        request(request, tag);
    }

    /**
     * 编辑约定
     * @param invitation_id
     * @param time 毫秒单位
     * @param place
     * @param content
     * @param listener
     * @param errorListener
     */
    public static void editInvitation(Object tag, String invitation_id, long time, String place, String content, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("invitation_id", invitation_id);
        body.put("time", String.valueOf(time / 1000));
        body.put("place", place);
        body.put("content", content);
        Request request = newRequest(INVITATION_EDIT, body, listener, errorListener);
        request(request, tag);
    }

    public static void getInvitations(Object tag, String user_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", user_id);
        Request request = newRequest(INVITATION_LIST, body, listener, errorListener);
        request(request, tag);
    }

    public static void setInvitationResult(Object tag, String invitationId, int result, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("invitation_id", invitationId);
        body.put("result", String.valueOf(result));
        Request request = newRequest(INVITATION_RESULT, body, listener, errorListener);
        request(request, tag);
    }

    public static void addFriend(Object tag, String host_id, String guest_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("host_id", host_id);
        body.put("guest_id", guest_id);
        Request request = newRequest(FRIEND_NEW, body, listener, errorListener);
        request(request, tag);
    }

    public static void removeFriend(Object tag, String host_id, String guest_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("host_id", host_id);
        body.put("guest_id", guest_id);
        Request request = newRequest(FRIEND_REMOVE, body, listener, errorListener);
        request(request, tag);
    }

    public static void listFriend(Object tag, String user_id, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", user_id);
        Request request = newRequest(FRIEND_LIST, body, listener, errorListener);
        request(request, tag);
    }

    /**
     *
     * @param tag
     * @param user_id
     * @param login_time 毫秒单位
     * @param login_place
     * @param longitude
     * @param latitude
     * @param accuracy
     * @param listener
     * @param errorListener
     */
    public static void updateLocation(Object tag, String user_id, long login_time, String login_place, double longitude, double latitude, int accuracy, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("user_id", user_id);
        body.put("last_login_time", String.valueOf(login_time / 1000));
        body.put("last_login_place", login_place);
        body.put("longitude", String.valueOf(longitude));
        body.put("latitude", String.valueOf(latitude));
        body.put("accuracy", String.valueOf(accuracy));
        Request request = newRequest(LOCATION_UPDATE, body, listener, errorListener);
        request(request, tag);
    }

    /**
     * 获取附近跑友
     * @param tag
     * @param longitude
     * @param latitude
     * @param distance
     * @param listener
     * @param errorListener
     */
    public static void getNeighbor(Object tag, double longitude, double latitude, double distance, Response.Listener listener, Response.ErrorListener errorListener) {
        Map<String, String> body = new HashMap<String, String>();
//        body.put("longitude", String.valueOf(longitude));
        body.put("longitude", 114.45+"");
        body.put("latitude", 30.56+"");
        body.put("distance", 10.0+"");
//        body.put("latitude", String.valueOf(latitude));
//        body.put("distance", String.valueOf(distance));
        Request request = newRequest(NEIGHBOR, body, listener,errorListener);
        request(request, tag);
    }

    private static Request newRequest(String url, final Map<String, String> requestBody, Response.Listener listener, Response.ErrorListener errorListener) {
        Logger.i("to run", url + " |" + Utils.printMap(requestBody));
        return new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestBody;
            }
        };
    }

    private static void request(Request request, Object tag) {
        RequestManager.addRequest(request, tag);
    }
}
