package org.dian.torun.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.dian.torun.AppData;

/**
 * Created by BaiYa on 2014/4/24.
 */
public class DatabaseProvider extends ContentProvider {

    private static final String TAG = "DataProvider";

    static final Object DbLock = new Object();

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "org.dian.torun.provider";

    public static final String PATH_USER = "/user";

    public static final String PATH_ARRANGEMENT = "/arrangement";

    public static final String PATH_FRIEND = "/friend";

    public static final Uri URI_USER = Uri.parse(SCHEME + AUTHORITY + PATH_USER);

    public static final Uri URI_ARRANGEMENT = Uri.parse(SCHEME + AUTHORITY + PATH_ARRANGEMENT);

    public static final Uri URI_FRIEND = Uri.parse(SCHEME + AUTHORITY + PATH_FRIEND);

    private static final int USER = 0;

    private static final int ARRANGEMENT = 1;

    private static final int FRIEND = 2;

    /*
    MIME type definitions
     */
    public static final String TYPE_USER = "vnd.android.cursor.dir/vnd.torun.user";

    public static final String TYPE_ARRANGEMENT = "vnd.android.cursor.dir/vnd.torun.arrangement";

    public static final String TYPE_FRIEND = "vnd.android.cursor.dir/vnd.torun.friend";

    private static final UriMatcher sUriMather;

    static {
        sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMather.addURI(AUTHORITY, "user", USER);
        sUriMather.addURI(AUTHORITY, "arrangement", ARRANGEMENT);
        sUriMather.addURI(AUTHORITY, "friend", FRIEND);
    }

    private static DbHelper mDbHelper;

    public static DbHelper getDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(AppData.getContext());
        }
        return mDbHelper;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (DbLock) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            String table = matchTable(uri);
            queryBuilder.setTables(table);

            SQLiteDatabase db = getDbHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(
                    db, // The database to queryFromDB
                    projection, // The columns to return from the queryFromDb
                    selection,  // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder // The sort order
                    );

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMather.match(uri)) {
            case USER:
                return TYPE_USER;
            case ARRANGEMENT:
                return TYPE_ARRANGEMENT;
            case FRIEND:
                return TYPE_FRIEND;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (DbLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getDbHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insert(table, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DbLock) {
            SQLiteDatabase db = getDbHelper().getWritableDatabase();

            int count = 0;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.delete(table, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (DbLock) {
            SQLiteDatabase db = getDbHelper().getWritableDatabase();
            int count;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.update(table, values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);

            return count;
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        synchronized (DbLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getDbHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    db.insertWithOnConflict(table, BaseColumns._ID, contentValues,
                            SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    private String matchTable(Uri uri) {
        String table;
        switch (sUriMather.match(uri)) {
            case FRIEND:
                table = FriendDataHelper.FriendDbInfo.TABLE_NAME;
                break;
            case ARRANGEMENT:
                table = InvitationDataHelper.InvitationDbInfo.TABLE_NAME;
                break;
            case USER:
                table = UserDataHelper.UserDbInfo.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }


    static class DbHelper extends SQLiteOpenHelper {

        public static final String DB_NAME = "torun.db";

        public static final int VERSION = 1;

        private DbHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            FriendDataHelper.FriendDbInfo.TABLE.create(db);
            InvitationDataHelper.InvitationDbInfo.TABLE.create(db);
            UserDataHelper.UserDbInfo.TABLE.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
