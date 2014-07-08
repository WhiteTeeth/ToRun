package org.dian.torun.test.model;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dian.torun.model.RequestManager;
import org.json.JSONObject;

/**
 * Created by BaiYa on 2014/4/22.
 */
public class JsonPostRequestTest extends ApplicationTestCase {

    public JsonPostRequestTest(Class applicationClass) {
        super(applicationClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void test() throws Exception {
        createApplication();
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                "http://162.243.143.103/yuepao_login.php",
                null,
                getListener(),
                getErrorListener());

        RequestManager.addRequest(request, "");
    }

    private Response.Listener<JSONObject> getListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("ToRun Test", ""+response);
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ToRun Test", error.toString());
            }
        };
    }
}
