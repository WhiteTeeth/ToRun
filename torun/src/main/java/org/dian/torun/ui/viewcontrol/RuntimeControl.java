package org.dian.torun.ui.viewcontrol;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dian.torun.R;
import org.dian.torun.bean.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BaiYa on 2014/5/23.
 */
public class RuntimeControl {

    private LinearLayout mLinearLayout;
    private Context mContext;
    private static Map<User.Runtime, Integer> backgroundMap;
    static {
        backgroundMap = new HashMap<User.Runtime, Integer>();
        backgroundMap.put(User.Runtime.EARLY_MORNING, R.drawable.bg_runtime_earlymorning);
        backgroundMap.put(User.Runtime.MORNING, R.drawable.bg_runtime_evening);
        backgroundMap.put(User.Runtime.NOON, R.drawable.bg_runtime_noon);
        backgroundMap.put(User.Runtime.AFTERNOON, R.drawable.bg_runtime_afternoon);
        backgroundMap.put(User.Runtime.EVENING, R.drawable.bg_runtime_morning);
        backgroundMap.put(User.Runtime.NIGHT, R.drawable.bg_runtime_night);
    }

    public RuntimeControl(Context context, LinearLayout linearLayout) {
        mContext = context;
        mLinearLayout = linearLayout;
    }

    public void setRuntime(List<User.Runtime> runtimeList) {
        mLinearLayout.removeAllViews();
        if (runtimeList.isEmpty()) {
            return;
        }
//        runtimeList = sortRuntime(runtimeList);
        Collections.sort(runtimeList);
        for (int i = 0; i < runtimeList.size(); i++) {
            User.Runtime runtime = runtimeList.get(i);
            View textView = newTextView(runtime);
            if (textView == null) continue;
            mLinearLayout.addView(textView);
            View view = new View(mContext);
            view.setLayoutParams(new LinearLayout.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT));
            mLinearLayout.addView(view);
        }
    }

    private View newTextView(User.Runtime runtime) {
        if (runtime == null) {
            return null;
        }
        TextView textView = (TextView) View.inflate(mContext, R.layout.runtime, null);
        textView.setText(runtime.getName());
        textView.setBackgroundResource(backgroundMap.get(runtime));
        return textView;
    }

    private List<User.Runtime> sortRuntime(List<User.Runtime> runtimeList) {
        Collections.sort(runtimeList, new Comparator<User.Runtime>() {
            @Override
            public int compare(User.Runtime lhs, User.Runtime rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return runtimeList;
    }

}
