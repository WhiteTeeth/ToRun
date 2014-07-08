package org.dian.torun.ui.fragment;

import android.app.Fragment;

import org.dian.torun.model.RequestManager;

/**
 * Created by BaiYa on 2014/4/25.
 */
public abstract class BaseFragment extends Fragment {

    private Object tag;

    @Override
    public void onStop() {
        super.onStop();
        cancelAll(getRequestTag());
    }

    protected Object getRequestTag() {
        return this;
    }

    protected void cancelAll(Object tag) {
        RequestManager.cancelAll(tag);
    }
}
