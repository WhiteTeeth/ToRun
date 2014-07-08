package org.dian.torun.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.dao.weibo.UserWeiboInfo;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.support.account.AccountHelper;
import org.dian.torun.utils.Logger;
import org.dian.torun.utils.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    private static final int REGISTER_OK = 1;
    private static final int REGISTER_FAILED = 0;
    private static final int REGISTER_ERROR = -1;

    private User user;

    private boolean registering = false;

    private ImageView mAvatarImageView;
    private TextView mNameTextView;
    private EditText mAgeTextView;
    private RadioGroup mGenderGroup;

    private ImageLoader.ImageContainer mAvartarRequest;
    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);

    private static Map<Integer, User.Runtime> runtimeMap;

    static {
        runtimeMap = new HashMap<Integer, User.Runtime>();
        runtimeMap.put(R.id.register_runtime_earlymorning, User.Runtime.EARLY_MORNING);
        runtimeMap.put(R.id.register_runtime_morning, User.Runtime.MORNING);
        runtimeMap.put(R.id.register_runtime_noon, User.Runtime.NOON);
        runtimeMap.put(R.id.register_runtime_afternoon, User.Runtime.AFTERNOON);
        runtimeMap.put(R.id.register_runtime_eveing, User.Runtime.EVENING);
        runtimeMap.put(R.id.register_runtime_night, User.Runtime.NIGHT);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterActivity.this.setResult(RESULT_CANCELED, getIntent());

        mAvatarImageView = (ImageView) findViewById(R.id.register_avatar);
        mNameTextView = (EditText) findViewById(R.id.register_name);
        mAgeTextView = (EditText) findViewById(R.id.register_birth);
        mGenderGroup = (RadioGroup) findViewById(R.id.register_gender);

        user = new User();
        user.setName(UserWeiboInfo.getName(this));
        user.setHeadImg(UserWeiboInfo.getIcon(this));
        user.setGender(UserWeiboInfo.getGender(this));
        user.setWeiboId(UserWeiboInfo.getUid(this));

        initView(user);
    }

    private void initView(User user) {
        mAvartarRequest = ImageManager.getInstance().loadImage(user.getHeadImg(),
                ImageManager.getImageListener(mAvatarImageView, mDefaultAvatarBitmap, mDefaultAvatarBitmap));
        mNameTextView.setText(user.getName());
        if (User.Gender.MALE == user.getGender()) {
            ((RadioButton) findViewById(R.id.register_male)).setChecked(true);
        } else if (User.Gender.FEMALE == user.getGender()) {
            ((RadioButton) findViewById(R.id.register_female)).setChecked(true);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            registering = false;
            dismissDialog();
            switch (msg.what) {
                case REGISTER_OK:
                    Intent intent = getIntent();
                    RegisterActivity.this.setResult(RESULT_OK, intent);
                    RegisterActivity.this.finish();
                    break;
                default:
                    ToastUtils.showToast(RegisterActivity.this, "注册失败");
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:
                if (ensureUser(user)) {
                    showDialog();
                    register(user);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO 确保user足够信息
    private boolean ensureUser(User user) {
        user.setHeadImg(this.user.getHeadImg());
        user.setWeiboId(this.user.getWeiboId());
        if (TextUtils.isEmpty(mNameTextView.getText())) {
            ToastUtils.showToast(this, "请输入昵称");
            return false;
        }
        user.setName(mNameTextView.getText() + "");
        if (TextUtils.isEmpty(mAgeTextView.getText())) {
            ToastUtils.showToast(this, "请输入年龄");
            return false;
        }
        user.setAge(Integer.decode(mAgeTextView.getText()+""));
        User.Gender gender = getGender();
        if (gender == User.Gender.UNKNOWN) {
            ToastUtils.showToast(this, "请选择性别");
            return false;
        }
        user.setGender(gender);
        List<User.Runtime> runtimeList = getRuntime();
        if (runtimeList.isEmpty()) {
            ToastUtils.showToast(this, "请选择喜欢跑步时间段");
            return false;
        }
        user.setRunTime(getRuntime());

        Logger.i("to run", new Gson().toJson(user));
        return true;
    }

    // 获取gender
    private User.Gender getGender() {
        User.Gender gender;
        switch (mGenderGroup.getCheckedRadioButtonId()) {
            case R.id.register_male:
                gender = User.Gender.MALE;
                break;
            case R.id.register_female:
                gender = User.Gender.FEMALE;
                break;
            default:
                gender = User.Gender.UNKNOWN;
                break;
        }
        return gender;
    }

    // 获取runtime
    private List<User.Runtime> getRuntime() {
        List<User.Runtime> runtimeList = new ArrayList<User.Runtime>();
        CheckBox checkBox;
        for (Map.Entry<Integer, User.Runtime> entry : runtimeMap.entrySet()) {
            int key = entry.getKey();
            User.Runtime value = entry.getValue();
            checkBox = (CheckBox) findViewById(key);
            if (checkBox.isChecked()) {
                runtimeList.add(value);
            }
        }
        return runtimeList;
    }

    ProgressDialog dialog = null;

    private void showDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("正在注册");
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void register(final User user) {
        if (registering) return;
        registering = true;
        AccountHelper.register(RegisterActivity.this, user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("to run", "register:::"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("result") && jsonObject.getInt("result") == 0) {
                        handler.sendEmptyMessage(REGISTER_FAILED);
                    } else {
                        AccountKeeper.writeAccountInfo(RegisterActivity.this, user);
                        handler.sendEmptyMessage(REGISTER_OK);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(REGISTER_FAILED);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.sendEmptyMessage(REGISTER_ERROR);
            }
        });
    }

}
