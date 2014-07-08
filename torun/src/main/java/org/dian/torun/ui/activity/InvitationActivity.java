package org.dian.torun.ui.activity;

import android.os.Bundle;

import org.dian.torun.R;
import org.dian.torun.ui.fragment.InvitationFragment;

public class InvitationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

        getFragmentManager().beginTransaction().replace(
                R.id.fragment_arrangement, InvitationFragment.getInstance()).commit();
    }

}
