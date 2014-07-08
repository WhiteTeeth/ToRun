package org.dian.torun.ui.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.Invitation;
import org.dian.torun.dao.InvitationDataHelper;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.ui.adapter.InvitationAdapter;
import org.dian.torun.ui.viewcontrol.CustomCrouton;
import org.dian.torun.utils.CommonUtils;
import org.dian.torun.vendor.TorunApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by BaiYa on 2014/4/11.
 */
public class InvitationFragment extends BasePullToRefreshFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private InvitationAdapter mAdapter;

    private boolean loadFirstPage = false;

    private InvitationDataHelper mDataHelper;

    private static InvitationFragment mFragment;

    private ListView mListView;

    private boolean isLoad = true;

    public static InvitationFragment getInstance() {
        if (mFragment == null) {

        }
        mFragment = new InvitationFragment();
        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseArgument();

        mListView = (ListView) view.findViewById(android.R.id.list);
        mDataHelper = InvitationDataHelper.getInstance(AppData.getContext());
        mAdapter = new InvitationAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

        loadFirstPage();
    }

    private void parseArgument() {

    }

    @Override
    public void onStop() {
        Crouton.cancelAllCroutons();
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0 && isLoad) {
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

    private void loadData(final int page) {
        String user_id = AccountKeeper.getUid(getActivity());
        TorunApi.getInvitations(getRequestTag(), user_id, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                isLoad = false;
                Log.i("ToRun", "response  "+response);
                boolean loadResult = false;
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("result") && object.getInt("result") == 1) {
                        JSONArray array = new JSONArray();
                        if (!TextUtils.isEmpty(object.get("data")+"")) {
                            array = object.getJSONArray("data");
                        }
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
                isLoad = false;
                showLoadResult(false);
            }
        });
    }

    private void refreshDb(final JSONArray array) {
        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                mDataHelper.deleteAll();

                List<Invitation> invitations = getInvitationsFromJson(array);
                mDataHelper.bulkInsert(invitations);
                return null;
            }

        });
    }

    private List<Invitation> getInvitationsFromJson(JSONArray array) {
        List<Invitation> invitations = new ArrayList<Invitation>();
        String sponsorId = AccountKeeper.getUid(getActivity());
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Invitation invitation = Invitation.fromJson(item.toString());
                invitation.setSponsorId(sponsorId);
                invitations.add(invitation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return invitations;
    }

    private void showLoadResult(boolean result) {
        loadFirstPage = false;
        mPullToRefreshLayout.setRefreshComplete();

        int toast = result ? R.string.refresh_list_success : R.string.refresh_list_failed;
        Style style = result ? CustomCrouton.confirmStyle : CustomCrouton.alertStyle;
        Crouton.makeText(getActivity(), toast, style, R.id.drawer_content).show();
    }


    @Override
    protected Object getRequestTag() {
        return super.getRequestTag();
    }

    @Override
    public void onRefreshStarted(View view) {
        super.onRefreshStarted(view);
        loadFirstPage();
    }
}
