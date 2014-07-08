package org.dian.torun.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.dian.torun.AppData;
import org.dian.torun.R;
import org.dian.torun.bean.Invitation;
import org.dian.torun.model.images.ImageManager;
import org.dian.torun.utils.TimeUtils;
import org.dian.torun.vendor.TorunApi;

/**
 * Created by BaiYa on 2014/4/11.
 */
public class InvitationAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;

    private BitmapDrawable mDefaultAvatarBitmap =
            (BitmapDrawable) AppData.getContext().getResources().getDrawable(R.drawable.default_avatar);

    public InvitationAdapter(Context context) {
        super(context, null, false);
        mLayoutInflater = ((Activity) context).getLayoutInflater();
    }

    @Override
    public Invitation getItem(int position) {
        mCursor.moveToPosition(position);
        return Invitation.fromCursor(mCursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mLayoutInflater.inflate(R.layout.listitem_invitation, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = getHolder(view);

        if (holder.avatarRequest != null) {
            holder.avatarRequest.cancelRequest();
        }

        final Invitation invitation = Invitation.fromCursor(cursor);
        String headUrl = invitation.getInviteeInfo().getHeadImg();
        holder.avatarRequest = ImageManager.getInstance().loadImage(headUrl,
                ImageManager.getImageListener(
                        holder.avatar, mDefaultAvatarBitmap, mDefaultAvatarBitmap));
        holder.userName.setText(invitation.getInviteeInfo().getName());
        holder.createTime.setText(TimeUtils.getFormatTime(invitation.getCreate_time() * 1000));
        holder.place.setText(invitation.getPlace());
        holder.time.setText(TimeUtils.getFormatTime(invitation.getTime() * 1000));
        holder.content.setText(invitation.getContent());

        switch (invitation.getResult()) {
            case 0:
                holder.result.setText("已拒绝");
                break;
            case 1:
                holder.result.setText("已接受");
                break;
            default:
                if (TimeUtils.isExpired(invitation.getTime() * 1000 )) {
                    holder.result.setText("已过期");
                    holder.acceptBtn.setVisibility(View.GONE);
                    holder.refuseBtn.setVisibility(View.GONE);
                } else {
                    holder.result.setText("等待确认");
                    holder.acceptBtn.setVisibility(View.VISIBLE);
                    holder.refuseBtn.setVisibility(View.VISIBLE);
                }
                break;
        }

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInvitationResult(invitation.getUid(), 1);
            }
        });
        holder.refuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInvitationResult(invitation.getUid(), 0);
            }
        });
    }

    private void setInvitationResult(String invitationId, int result) {
        TorunApi.setInvitationResult(this, invitationId, result, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO 显示状态为已接受/已拒绝
                //TODO 自动加好友

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO 错误提示
            }
        });
    }

    private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class Holder {
        public ImageView avatar;
        public TextView userName;
        public TextView createTime;
        public TextView place;
        public TextView time;
        public TextView content;
        public TextView result;
        public Button acceptBtn;
        public Button refuseBtn;

        public ImageLoader.ImageContainer avatarRequest;

        public Holder(View view) {
            avatar = (ImageView) view.findViewById(R.id.invitation_avatar);
            userName = (TextView) view.findViewById(R.id.invitation_name);
            createTime = (TextView) view.findViewById(R.id.invitation_create_time);
            content = (TextView) view.findViewById(R.id.invitation_content);
            place = (TextView) view.findViewById(R.id.invitation_place);
            time = (TextView) view.findViewById(R.id.invitation_time);
            acceptBtn = (Button) view.findViewById(R.id.invitation_accept);
            refuseBtn = (Button) view.findViewById(R.id.invitation_refuse);
            result = (TextView) view.findViewById(R.id.invitation_result);
        }

    }
}
