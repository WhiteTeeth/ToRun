package org.dian.torun.ui.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.LocationData;
import org.dian.torun.bean.User;
import org.dian.torun.dao.UserDataHelper;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.support.location.LocationHelper;
import org.dian.torun.ui.adapter.NeighborAdapter;
import org.dian.torun.ui.viewcontrol.CustomCrouton;
import org.dian.torun.utils.CommonUtils;
import org.dian.torun.utils.Logger;
import org.dian.torun.utils.TimeUtils;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NeighborFragment extends BasePullToRefreshFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private boolean loadFirstPage = false;

    private LocationHelper mLocationHelper;
    private LocationHelper.MLocationListener mLocationListener;
    private LocationData mLocationData;

    private NeighborAdapter mAdapter;

    private UserDataHelper mDataHelper;

    private ListView mListView;

    private boolean load = true;

	private static NeighborFragment mFragment;
	
	public static NeighborFragment getInstance() {
        if (mFragment == null) {
        }
        mFragment = new NeighborFragment();
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationHelper = new LocationHelper(getActivity());
        mLocationHelper.startLocation();
        mLocationHelper.requestLocation();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parseArgument();

        mLocationListener = new LocationHelper.MLocationListener() {
            @Override
            public void onReceiveLocation(LocationData locationData) {
                if (mLocationData == null) {
                    mLocationData = locationData;
                    loadData(1);
                }
                mLocationData = locationData;
                updateLocation();
            }
        };
        mLocationHelper.setLocationListener(mLocationListener);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mDataHelper = UserDataHelper.getInstance(AppData.getContext());
        mAdapter = new NeighborAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    private void parseArgument() {

    }


    private void updateLocation() {
        String user_id = AccountKeeper.getUid(getActivity());
        if (mLocationData == null) return;
        long time = TimeUtils.getFormatTime(mLocationData.getTime());
        TorunApi.updateLocation(
                getRequestTag(), user_id, time,
                mLocationData.getAddr(),
                mLocationData.getLontitude(),
                mLocationData.getLatitude(),
                (int) mLocationData.getRadius(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.i("to run", "response:"+response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.i("to run", "location:" + error);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationHelper != null) {
            mLocationHelper.stopLocation();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0 && load) {
            loadFirstPage();
        }  else if (data == null || (data != null && data.getCount() == 0)) {
            mListView.setVisibility(View.GONE);
            getView().findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void loadFirstPage() {
        if (loadFirstPage) return;
        loadFirstPage = true;
        if (!mPullToRefreshLayout.isRefreshing()) {
            mPullToRefreshLayout.setRefreshing(true);
        }
        loadData(1);
    }

    private void loadData(int page) {
        getNeighbor();
    }

    private void getNeighbor() {
        if (mLocationData == null) return;
        TorunApi.getNeighbor(getRequestTag(),
                mLocationData.getLontitude(),
                mLocationData.getLatitude(),
                10,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                load = false;
                boolean loadResult = false;
                Logger.i("to run", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("result") && jsonObject.optInt("result") == 1) {
                        JSONArray array = jsonObject.getJSONArray("neighbors");
                        refreshDb(array);
                        loadResult = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showLoadResult(loadResult);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                load = false;
                showLoadResult(false);
            }
        });
    }

    private void refreshDb(final JSONArray array) {
        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                mDataHelper.deleteAll();

                List<User> users = getUsersFromJson(array);
                mDataHelper.bulkInsert(users);
                return null;
            }
        });
    }

    private List<User> getUsersFromJson(JSONArray array) {
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject item = array.getJSONObject(i);
                User user = User.fromJson(item.toString());
                users.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    private void showLoadResult(final boolean result) {
        loadFirstPage = false;
        mPullToRefreshLayout.setRefreshComplete();
        int toast = result ? R.string.refresh_list_success : R.string.refresh_list_failed;
        Style style = result ? CustomCrouton.confirmStyle : CustomCrouton.alertStyle;
        Crouton.makeText(getActivity(), toast, style, R.id.drawer_content).show();
    }

    @Override
    public void onStop() {
        Crouton.cancelAllCroutons();
        super.onStop();
    }

    @Override
    protected Object getRequestTag() {
        return super.getRequestTag();
    }

    @Override
    public void onRefreshStarted(View view) {
        loadFirstPage();
    }
}
