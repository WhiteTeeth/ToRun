package org.dian.torun.ui.viewcontrol;

import android.content.Context;
import android.widget.TextView;

import org.dian.torun.R;
import org.dian.torun.bean.User;

/**
 * Created by BaiYa on 2014/5/26.
 */
public class GenderViewControl {

    private Context mContext;
    private TextView mGenderView;

    public GenderViewControl(Context context, TextView view) {
        mContext = context;
        mGenderView = view;
    }

    public void setGender(User.Gender gender) {
        int genderIcon = R.drawable.ic_user_male;
        int genderBg = R.drawable.bg_gender_male;
        switch (gender) {
            case MALE:
                genderIcon = R.drawable.ic_user_male;
                genderBg = R.drawable.bg_gender_male;
                break;
            case FEMALE:
                genderIcon = R.drawable.ic_user_famale;
                genderBg = R.drawable.bg_gender_female;
                break;
            case UNKNOWN:
                break;
        }
        mGenderView.setCompoundDrawablesWithIntrinsicBounds(genderIcon, 0, 0, 0);
        mGenderView.setBackgroundResource(genderBg);
    }

}
