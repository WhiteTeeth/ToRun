package org.dian.torun.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.dian.torun.R;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by BaiYa on 2014/5/21.
 */
public abstract class BasePullToRefreshFragment extends BaseFragment implements OnRefreshListener{

    protected PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        View emptyView = getEmpty();
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);

        // 下拉刷新功能
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ViewGroup viewGroup = (ViewGroup) view;
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list)
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    protected View getEmpty() {
        View view = View.inflate(getActivity(), R.layout.list_empty, null);
        return view;
    }


    @Override
    public void onRefreshStarted(View view) {
        // nothing to do
    }
}
