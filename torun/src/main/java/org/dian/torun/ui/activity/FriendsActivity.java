package org.dian.torun.ui.activity;

import android.os.Bundle;

import org.dian.torun.R;
import org.dian.torun.ui.fragment.FriendsFragment;

public class FriendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getFragmentManager().
                beginTransaction().replace(R.id.fragment_friends, FriendsFragment.getInstance()).commit();
    }

}
