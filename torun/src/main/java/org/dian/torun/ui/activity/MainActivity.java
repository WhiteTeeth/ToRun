package org.dian.torun.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;

import org.dian.torun.Contants;
import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.support.push.JPushHelper;
import org.dian.torun.type.DrawerMenu;
import org.dian.torun.ui.adapter.DrawerAdapter;
import org.dian.torun.ui.fragment.DrawerFragment;
import org.dian.torun.ui.fragment.FriendsFragment;
import org.dian.torun.ui.fragment.InvitationFragment;
import org.dian.torun.ui.fragment.NeighborFragment;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, DrawerFragment.QuitControl{

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private boolean quit = true;    // 标记是否退出应用

    private Fragment mCorrectFragment;
    private DrawerMenu mCorrectMenu;
    private DrawerMenu mSelectedMenu;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onLogin();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.app_name));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                replaceFragment();
                getActionBar().setTitle(mCorrectMenu.getDisplayName());
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                getActionBar().setTitle(R.string.drawer_title);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        String action = getIntent().getAction();
        if (Contants.Action.SHOW_PUSH_RESULT.equals(action)) {
            mCorrectMenu = mSelectedMenu = DrawerMenu.arrangement;
        } else {
            mCorrectMenu = mSelectedMenu = DrawerMenu.neighbor;
        }

        mCorrectFragment = getFragment(mCorrectMenu);
        getFragmentManager()
                .beginTransaction().replace(R.id.drawer_content, getFragment(mCorrectMenu)).commit();

        getFragmentManager()
                .beginTransaction().replace(R.id.left_drawer, DrawerFragment.newInstance()).commit();

    }

    private void replaceFragment() {
        if (mCorrectMenu != mSelectedMenu) {
            mCorrectMenu = mSelectedMenu;
            mCorrectFragment = getFragment(mCorrectMenu);
            getFragmentManager().beginTransaction()
                    .replace(R.id.drawer_content, getFragment(mCorrectMenu))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    // 刷新授权消息
    private void onLogin() {
        userId = AccountKeeper.getUid(this);
        updateAlias();
        updateAccount();
        updateAuthorize();
    }

    private void updateAlias() {
        JPushHelper helper = new JPushHelper(this);
        helper.setAlias(userId);
    }

    private void updateAccount() {
        TorunApi.userShow(this, userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("to run", "response:::" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("result") && object.getInt("result") == 0) {
                    } else {
                        User user = User.fromJson(object.getJSONObject("data").toString());
                        AccountKeeper.writeAccountInfo(MainActivity.this, user);
                        getFragmentManager()
                                .beginTransaction().replace(R.id.left_drawer, DrawerFragment.newInstance()).commit();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    // TODO 刷新授权消息
    private void updateAuthorize() {
    }

    @Override
    protected void onDestroy() {
        if (quit) {
            ShareSDK.stopSDK(this);
        }
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            // 控制侧滑栏的实现
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedMenu = ((DrawerAdapter) parent.getAdapter()).getItem(position);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .remove(mCorrectFragment)
                .commit();
        mDrawerLayout.closeDrawers();
//        startActivity(getIntent(drawerMenu));
    }


    private Fragment getFragment(DrawerMenu drawerMenu) {
        Fragment fragment = null;
        switch (drawerMenu) {
            case neighbor:
                fragment = NeighborFragment.getInstance();
                break;
            case arrangement:
                fragment = InvitationFragment.getInstance();
                break;
            case friends:
                fragment = FriendsFragment.getInstance();
                break;
        }
        return fragment;
    }

    private Intent getIntent(DrawerMenu drawerMenu) {
        Intent intent = new Intent();
        switch (drawerMenu) {
            case neighbor:
                intent.setClass(this, UserDetailActivity.class);
                break;
            case arrangement:
                intent.setClass(this, InvitationActivity.class);
                break;
            case friends:
                intent.setClass(this, FriendsActivity.class);
                break;
        }
        return intent;
    }

    @Override
    public void quit(boolean isQuit) {
        quit = isQuit;
    }


}
