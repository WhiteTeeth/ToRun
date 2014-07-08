package org.dian.torun.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.ui.activity.NewInvitationActivity;
import org.dian.torun.ui.viewcontrol.GenderViewControl;
import org.dian.torun.ui.viewcontrol.RuntimeControl;
import org.dian.torun.utils.TimeUtils;

/**
 * Created by BaiYa on 2014/5/5.
 */
public class NeighborAdapter extends CursorAdapter {


    private LayoutInflater mLayoutInflater;

    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);


    public NeighborAdapter(Context context) {
        super(context, null, false);
        mLayoutInflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public Object getItem(int position) {
        mCursor.moveToPosition(position);
        return User.fromCursor(mCursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.listitem_neighbor, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = getHolder(view);

        if (holder.avatarRequest != null) {
            holder.avatarRequest.cancelRequest();
        }

        final User user = User.fromCursor(cursor);
        String headUrl = user.getHeadImg();
        holder.avatarRequest = ImageManager.getInstance().loadImage(headUrl,
                ImageManager.getImageListener(
                        holder.avatar, mDefaultAvatarBitmap, mDefaultAvatarBitmap));

        holder.userName.setText(user.getName());
        holder.lastplace.setText(user.getLast_login_place());
        holder.lasttime.setText(TimeUtils.getFormatTime(user.getLast_login_time() * 1000));
        holder.runtimeControl.setRuntime(user.getRunTime());
        holder.age.setText(String.valueOf(user.getAge()));
        holder.genderViewControl.setGender(user.getGender());

        String host = AccountKeeper.getUid(mContext);
        if (host.equals(user.getUid())) {
            holder.request.setVisibility(View.INVISIBLE);
        } else {
            holder.request.setVisibility(View.VISIBLE);
        }
        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, NewInvitationActivity.class);
                intent.putExtra("user", user.toJson());
                mContext.startActivity(intent);
            }
        });
    }

    private Holder getHolder(View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class Holder{
        public ImageView avatar;
        public TextView userName;
        public TextView age;
        public TextView lastplace;
        public TextView lasttime;
        public RuntimeControl runtimeControl;

        public GenderViewControl genderViewControl;

        public Button request;

        public ImageLoader.ImageContainer avatarRequest;

        public Holder(View view) {
            avatar = (ImageView) view.findViewById(R.id.neighbor_avatar);
            userName = (TextView) view.findViewById(R.id.neighbor_name);
            age = (TextView) view.findViewById(R.id.neighbor_age);
            lastplace = (TextView) view.findViewById(R.id.neighbor_lastplace);
            lasttime = (TextView) view.findViewById(R.id.neighbor_lasttime);
            LinearLayout runtime = (LinearLayout) view.findViewById(R.id.neighbor_runtime);
            runtimeControl = new RuntimeControl(view.getContext(), runtime);
            genderViewControl = new GenderViewControl(view.getContext(), age);

            request = (Button) view.findViewById(R.id.neighbor_request);
        }
    }
}
