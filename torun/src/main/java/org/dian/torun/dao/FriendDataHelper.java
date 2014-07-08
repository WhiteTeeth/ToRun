package org.dian.torun.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import org.dian.torun.bean.User;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.utils.database.Column;
import org.dian.torun.utils.database.SQLiteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiYa on 2014/5/5.
 */
public class FriendDataHelper extends BaseDatabseHelper {

    protected FriendDataHelper(Context mContext) {
        super(mContext);
    }

    private static FriendDataHelper mDataHelper;

    private String getOwnerId() {
        return AccountKeeper.getUid(getContext());
    }

    public static FriendDataHelper getInstance(Context context) {
        if (mDataHelper == null) {
            mDataHelper = new FriendDataHelper(context.getApplicationContext());
        }
        return mDataHelper;
    }

    @Override
    protected Uri getContentUri() {
        return DatabaseProvider.URI_FRIEND;
    }


    private ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(FriendDbInfo.UID, user.getUid());
        values.put(FriendDbInfo.NAME, user.getName());
        values.put(FriendDbInfo.AGE, user.getAge());
        values.put(FriendDbInfo.HEADIMG, user.getHeadImg());
        values.put(FriendDbInfo.SEX, user.getGender().toString());
        values.put(FriendDbInfo.RUN_TIME, user.getRuntimeJson());
        values.put(FriendDbInfo.LAST_LOGIN_TIME, user.getLast_login_time());
        values.put(FriendDbInfo.LAST_LOGIN_PLACE, user.getLast_login_place());
        values.put(FriendDbInfo.LONGITUDE, user.getLongitude());
        values.put(FriendDbInfo.LATITUDE, user.getLatitude());
        values.put(FriendDbInfo.ACCURACY, user.getAccuracy());
        values.put(FriendDbInfo.OWNER_ID, getOwnerId());
        return values;
    }

    public User query(long id) {
        User user = null;

        Cursor cursor = query(
                null,
                FriendDbInfo.UID + "=? and " + FriendDbInfo.OWNER_ID + "=?",
                new String[]{ String.valueOf(id), getOwnerId() },
                null);
        if (cursor.moveToFirst()) {
            user = User.fromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    public void bulkInsert(List<User> users) {
        ArrayList<ContentValues> contentValueses = new ArrayList<ContentValues>();
        for (User user : users) {
            ContentValues values = getContentValues(user);
            contentValueses.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValueses.size()];
        bulkInsert(contentValueses.toArray(valueArray));
    }

    public int deleteAll() {
        synchronized (DatabaseProvider.DbLock) {
            DatabaseProvider.DbHelper dbHelper = DatabaseProvider.getDbHelper();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int row = db.delete(
                    FriendDbInfo.TABLE_NAME,
                    FriendDbInfo.OWNER_ID + "=?",
                    new String[]{ getOwnerId() });
            return row;
        }
    }


    public CursorLoader getCursorLoader() {
        return new CursorLoader(
                getContext(),
                getContentUri(),
                null,
                FriendDbInfo.OWNER_ID + "=?",
                new String[]{ getOwnerId() },
                FriendDbInfo.UID + " ASC");
    }

    public static final class FriendDbInfo implements BaseColumns {
        private FriendDbInfo() {}

        public static final String TABLE_NAME = "friend";

        public static final String UID = "uid";

        public static final String NAME = "name";

        public static final String SEX = "sex";

        public static final String AGE = "age";

        public static final String HEADIMG = "headimg";

        public static final String RUN_DAY = "run_day";

        public static final String RUN_TIME = "run_time";

        public static final String LAST_LOGIN_TIME = "last_login_time";

        public static final String LAST_LOGIN_PLACE = "last_login_place";

        public static final String LONGITUDE = "longitude";

        public static final String LATITUDE = "latitude";

        public static final String ACCURACY = "accuracy";

        //TODO
        public static final String OWNER_ID = "owner_id";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(UID, Column.DataType.TEXT)
                .addColumn(NAME, Column.DataType.TEXT)
                .addColumn(SEX, Column.DataType.TEXT)
                .addColumn(AGE, Column.DataType.INTEGER)
                .addColumn(HEADIMG, Column.DataType.TEXT)
                .addColumn(RUN_DAY, Column.DataType.TEXT)
                .addColumn(RUN_TIME, Column.DataType.TEXT)
                .addColumn(LAST_LOGIN_TIME, Column.DataType.INTEGER)
                .addColumn(LAST_LOGIN_PLACE, Column.DataType.TEXT)
                .addColumn(LONGITUDE, Column.DataType.REAL)
                .addColumn(LATITUDE, Column.DataType.REAL)
                .addColumn(ACCURACY, Column.DataType.INTEGER)
                .addColumn(OWNER_ID, Column.DataType.TEXT);

    }

}
