package org.dian.torun.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.support.account.AccountHelper;
import org.dian.torun.utils.Logger;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

public class LoginActivity extends Activity implements Handler.Callback{

    private final int AUTHORIZE_SUCCESS = 1;
    private final int AUTHORIZE_CANCEL = 0;
    private final int AUTHORIZE_ERROR = -1;

    private final int REQUEST_REGISTER = 10;

    private boolean isAuthorize = false;
    private boolean isRegister = false;

    private boolean isFinish = true;

    private ImageView mImageView;
    private AnimationDrawable mAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShareSDK.initSDK(this);

        account();

        setContentView(R.layout.activity_login);

        mImageView = (ImageView) findViewById(R.id.login_progress);
        mAnimationDrawable = (AnimationDrawable) mImageView.getBackground();

        findViewById(R.id.login_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAuthorize = true;
                startLoading();
                account();
            }
        });
    }

    private void startLoading() {
        mImageView.setVisibility(View.VISIBLE);
        mAnimationDrawable.start();
    }

    private void stopLoading() {
        mAnimationDrawable.stop();
        mImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (isFinish) {
            ShareSDK.stopSDK(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    private void account() {
        if (AccountHelper.isRegister(this)) {
            startMainActivity();    // 进入主界面
        } else if (isRegister && AccountHelper.isAuthorize(this)) {
            startRegisterActivity();    // 进入注册界面
            isRegister = false;
        } else if (isAuthorize) {
            authorize();    // 授权
            isAuthorize = false;
        } else {
        }
    }

    private void startMainActivity() {
        isFinish = false;
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void startRegisterActivity() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
    }

    private void authorize() {
        AccountHelper.authorize(this, mPlatformActionListener);
    }

    private PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
            UIHandler.sendEmptyMessage(AUTHORIZE_SUCCESS, LoginActivity.this);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            UIHandler.sendEmptyMessage(AUTHORIZE_ERROR, LoginActivity.this);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            UIHandler.sendEmptyMessage(AUTHORIZE_CANCEL, LoginActivity.this);
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case AUTHORIZE_SUCCESS:
                // 授权成功后，判断是否为已注册用户，并获取或注册用户信息
                TorunApi.userShow(this, AccountKeeper.getWeiboId(this), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.i("to run", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("result") && jsonObject.getInt("result") == 0) {
                                isRegister = true;
                                account();
                            } else {
                                User user = User.fromJson(jsonObject.getJSONObject("data").toString());
                                AccountKeeper.writeAccountInfo(LoginActivity.this, user);
                                account();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopLoading();
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case AUTHORIZE_CANCEL:
                stopLoading();
                Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT).show();
                break;
            case AUTHORIZE_ERROR:
                stopLoading();
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_REGISTER:
                if (resultCode == RESULT_OK) {
                    account();
                } else if (resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "登陆取消", Toast.LENGTH_LONG).show();
                    stopLoading();
                }
                break;
        }
    }
}
