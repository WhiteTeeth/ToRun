package org.dian.torun.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.ui.activity.NewInvitationActivity;
import org.dian.torun.ui.viewcontrol.GenderViewControl;
import org.dian.torun.ui.viewcontrol.RuntimeControl;
import org.dian.torun.utils.TimeUtils;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BaiYa on 2014/4/11.
 */
public class UserFragment extends BaseFragment {

    private static UserFragment mFragment;

    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);

    public static UserFragment getInstance() {
        mFragment = new UserFragment();
        return mFragment;
    }

    public UserFragment() {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.user_edit:
//                getActivity().finish();
//
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = AccountKeeper.readAccountInfo(getActivity());
        getActivity().getActionBar().setTitle(user.getName());
        showUser(user);
        loadData(user.getUid());
    }

    private void loadData(String user_id) {
        TorunApi.userShow(getRequestTag(), user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("to run", "response:::" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("result") && object.getInt("result") == 0) {
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = User.fromJson(object.getJSONObject("data").toString());
                        showUser(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.refresh_list_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUser(final User user) {
        Holder holder = new Holder(this.getView());
        String headUrl = user.getHeadImg();
        holder.avatarRequest = ImageManager.getInstance().loadImage(headUrl,
                ImageManager.getImageListener(
                        holder.avatar, mDefaultAvatarBitmap, mDefaultAvatarBitmap));

        holder.lastplace.setText(user.getLast_login_place());
        holder.lasttime.setText(TimeUtils.getFormatTime(user.getLast_login_time() * 1000));
        holder.age.setText(String.valueOf(user.getAge()));
        holder.runtimeControl.setRuntime(user.getRunTime());
        holder.genderViewControl.setGender(user.getGender());

        String host = AccountKeeper.getUid(getActivity());
        // 逻辑上不应该做这样的判断...
        if (host.equals(user.getWeiboId())) {
            holder.request.setVisibility(View.INVISIBLE);
        } else {
            holder.request.setVisibility(View.VISIBLE);
        }

        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NewInvitationActivity.class);
                intent.putExtra("user", user.toJson());
                startActivity(intent);
            }
        });
    }

    @Override
    protected Object getRequestTag() {
        return super.getRequestTag();
    }

    private class Holder{
        public ImageView avatar;
        public TextView userName;
        public TextView age;
        public TextView lastplace;
        public TextView lasttime;
        public RuntimeControl runtimeControl;
        public Button request;
        public GenderViewControl genderViewControl;

        public ImageLoader.ImageContainer avatarRequest;

        public Holder(View view) {
            avatar = (ImageView) view.findViewById(R.id.neighbor_avatar);
            userName = (TextView) view.findViewById(R.id.neighbor_name);
            age = (TextView) view.findViewById(R.id.neighbor_age);
            lastplace = (TextView) view.findViewById(R.id.neighbor_lastplace);
            lasttime = (TextView) view.findViewById(R.id.neighbor_lasttime);
            LinearLayout runtime = (LinearLayout) view.findViewById(R.id.neighbor_runtime);
            runtimeControl = new RuntimeControl(view.getContext(), runtime);

            genderViewControl = new GenderViewControl(view.getContext(), age);

            request = (Button) view.findViewById(R.id.neighbor_request);
        }
    }
}
