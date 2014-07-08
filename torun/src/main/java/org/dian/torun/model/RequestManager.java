package org.dian.torun.model;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.dian.torun.AppData;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class RequestManager {

    private final static RequestQueue mRequestQueue = newRequestQueue();

    private RequestManager() {
        // no instance
    }

    private static RequestQueue newRequestQueue() {
        return Volley.newRequestQueue(AppData.getContext());
    }

    public static void addRequest(Request request) {
        addRequest(request, "");
    }

    public static void addRequest(Request request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
