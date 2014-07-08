package org.dian.torun.ui.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import org.dian.torun.Contants;
import org.dian.torun.R;
import org.dian.torun.ui.activity.MainActivity;
import org.dian.torun.utils.Logger;

import java.util.Random;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by BaiYa on 2014/5/22.
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.d(TAG, "[JPushReceiver] onReceiver - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Logger.e(TAG, "JPush 注册成功");
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "接收到推送下来的自定义消息");
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "接收到推送下来的通知");

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d(TAG, "用户点击打开了通知");

        } else {
            Logger.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        showNotification(context, bundle, new Random().nextInt());
    }

    private void showNotification(Context context, Bundle bundle, int id) {
        String title = context.getString(R.string.app_name);    // 标题（可选）
        String message = context.getString(R.string.notify_message);// 消息内容

        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);    // 自定义内容（可选）
        String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE); // 对应api的content_type字段
        String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID); // 唯一标识消息的id
        if (TextUtils.isEmpty(message)) {
            Logger.e(TAG, "Unexpected: empty message. Give up");
            return;
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        /*启动主界面*/
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtras(bundle);
        resultIntent.setAction(Contants.Action.SHOW_PUSH_RESULT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, mBuilder.build());
    }
}
