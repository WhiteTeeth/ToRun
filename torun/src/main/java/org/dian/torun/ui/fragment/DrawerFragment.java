package org.dian.torun.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.support.account.AccountHelper;
import org.dian.torun.ui.activity.LoginActivity;
import org.dian.torun.ui.activity.MainActivity;
import org.dian.torun.ui.activity.UserDetailActivity;
import org.dian.torun.ui.adapter.DrawerAdapter;
import org.dian.torun.view.CircleImageView;

public class DrawerFragment extends BaseFragment {

    private ListView mListView;

    private DrawerAdapter mAdapter;

    private MainActivity mActivity;

    private QuitControl mQuitControl;
    private Button mLogoutBtn;

    private CircleImageView mHeadView;
    private ImageLoader.ImageContainer mAvartarRequest;
    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);


    public static DrawerFragment newInstance() {
        DrawerFragment fragment = new DrawerFragment();
        return fragment;
    }

    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View contentView = inflater.inflate(R.layout.fragment_drawer, container, false);
        mListView = (ListView) contentView.findViewById(android.R.id.list);
        mAdapter = new DrawerAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mActivity);

        mHeadView = (CircleImageView) contentView.findViewById(R.id.drawer_head);
        String headUrl = AccountKeeper.readAccountInfo(getActivity()).getHeadImg();
        mAvartarRequest = ImageManager.getInstance().loadImage(headUrl,
                ImageManager.getImageListener(mHeadView, mDefaultAvatarBitmap, mDefaultAvatarBitmap));
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserDetailActivity.class));
            }
        });

        mQuitControl = mActivity;
        mLogoutBtn = (Button) contentView.findViewById(R.id.logout);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHelper.logout(getActivity());
                mQuitControl.quit(false);
                Intent intent = new Intent();
                intent.setClass(mActivity, LoginActivity.class);
                startActivity(intent);
                mActivity.finish();
            }
        });
        return contentView;
    }



    public interface QuitControl {
        public void quit(boolean isQuit);
    }


}
