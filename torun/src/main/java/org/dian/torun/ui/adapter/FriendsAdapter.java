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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.User;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.ui.activity.NewInvitationActivity;

/**
 * Created by BaiYa on 2014/4/11.
 */
public class FriendsAdapter extends CursorAdapter{

    private LayoutInflater mLayoutInflater;

    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);


    public FriendsAdapter(Context context) {
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
        return mLayoutInflater.inflate(R.layout.listitem_friend, null);
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
        holder.age.setText(String.valueOf(user.getAge()));
        // TODO
        int genderIcon = R.drawable.ic_user_male;
        switch (user.getGender()) {
            case MALE:
                genderIcon = R.drawable.ic_user_male;
                break;
            case FEMALE:
                genderIcon = R.drawable.ic_user_famale;
                break;
            case UNKNOWN:
                break;
        }
        holder.age.setCompoundDrawablesWithIntrinsicBounds(genderIcon, 0, 0, 0);

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
        public Button request;

        public ImageLoader.ImageContainer avatarRequest;

        public Holder(View view) {
            avatar = (ImageView) view.findViewById(R.id.neighbor_avatar);
            userName = (TextView) view.findViewById(R.id.neighbor_name);
            age = (TextView) view.findViewById(R.id.neighbor_age);

            request = (Button) view.findViewById(R.id.neighbor_request);
        }
    }
}
