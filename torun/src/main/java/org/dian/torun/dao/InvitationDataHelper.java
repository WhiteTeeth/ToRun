package org.dian.torun.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import org.dian.torun.bean.Invitation;
import org.dian.torun.dao.account.AccountKeeper;
import org.dian.torun.utils.database.Column;
import org.dian.torun.utils.database.SQLiteTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiYa on 2014/4/24.
 */
public class InvitationDataHelper extends BaseDatabseHelper {

    private InvitationDataHelper(Context mContext) {
        super(mContext);
    }

    private static InvitationDataHelper mDataHelper;

    public static InvitationDataHelper getInstance(Context context) {
        if (mDataHelper == null) {
            mDataHelper = new InvitationDataHelper(context.getApplicationContext());
        }
        return mDataHelper;
    }

    @Override
    protected Uri getContentUri() {
        return DatabaseProvider.URI_ARRANGEMENT;
    }

    private String getOwnerId() {
        return AccountKeeper.getUid(getContext());
    }

    private ContentValues getContentValues(Invitation invitation) {
        ContentValues values = new ContentValues();
        values.put(InvitationDbInfo.UID, invitation.getUid());
        values.put(InvitationDbInfo.INVITEEID, invitation.getInviteeJson());
        values.put(InvitationDbInfo.SPONSORID, invitation.getSponsorId());
        values.put(InvitationDbInfo.PLACE, invitation.getPlace());
        values.put(InvitationDbInfo.TIME, invitation.getTime());
        values.put(InvitationDbInfo.EXTRA, invitation.getContent());
        values.put(InvitationDbInfo.CREATETIME, invitation.getCreate_time());
        values.put(InvitationDbInfo.RESULT, invitation.getResult());
        return values;
    }


    public Invitation query(long uid) {
        Invitation invitation = null;
        Cursor cursor = query(
                null,
                InvitationDbInfo.UID + "= ?",
                new String[]{ String.valueOf(uid) },
                null);
        if (cursor.moveToFirst()) {
            invitation = Invitation.fromCursor(cursor);
        }
        cursor.close();
        return invitation;
    }

    public void bulkInsert(List<Invitation> invitations) {
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for (Invitation invitation : invitations) {
            ContentValues values = getContentValues(invitation);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        bulkInsert(contentValues.toArray(valueArray));
    }

    public int deleteAll() {
        synchronized (DatabaseProvider.DbLock) {
            DatabaseProvider.DbHelper mDbHelper = DatabaseProvider.getDbHelper();
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int row = db.delete(InvitationDbInfo.TABLE_NAME,
                    InvitationDbInfo.SPONSORID + "=?",
                    new String[]{ getOwnerId() });
            return row;
        }
    }

    public CursorLoader getCursorLoader() {
        return new CursorLoader(
                getContext(),
                getContentUri(),
                null,
                InvitationDbInfo.SPONSORID + "=?",
                new String[]{ getOwnerId() },
                InvitationDbInfo.RESULT + " DESC, "
                        + InvitationDbInfo.TIME + " DESC, "
                        + InvitationDbInfo.CREATETIME + " DESC ");
    }

    public static final class InvitationDbInfo implements BaseColumns {
        private InvitationDbInfo() {}

        public static final String TABLE_NAME = "arrangement";

        public static final String UID = "uid";
        public static final String SPONSORID = "sponsorId";
        public static final String INVITEEID = "inviteeId";
        public static final String TIME = "time";
        public static final String PLACE = "place";
        public static final String EXTRA = "extra";
        public static final String CREATETIME = "create_time";
        public static final String RESULT = "result";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(UID, Column.DataType.TEXT)
                .addColumn(SPONSORID, Column.DataType.TEXT)
                .addColumn(INVITEEID, Column.DataType.TEXT)
                .addColumn(TIME, Column.DataType.TEXT)
                .addColumn(PLACE, Column.DataType.TEXT)
                .addColumn(EXTRA, Column.DataType.TEXT)
                .addColumn(CREATETIME, Column.DataType.TEXT)
                .addColumn(RESULT, Column.DataType.INTEGER);
    }

}
