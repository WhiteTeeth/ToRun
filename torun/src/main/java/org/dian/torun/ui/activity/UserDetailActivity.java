package org.dian.torun.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import org.dian.torun.R;
import org.dian.torun.ui.fragment.UserFragment;

public class UserDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        getFragmentManager().
                beginTransaction().replace(R.id.fragment_user, UserFragment.getInstance()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
