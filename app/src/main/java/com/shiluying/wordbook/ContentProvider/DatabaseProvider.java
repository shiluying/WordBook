package com.shiluying.wordbook.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.shiluying.wordbook.database.DBHelper;

public class DatabaseProvider extends ContentProvider {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    //添加整形常亮
    public static final int WORD_ITEM = 1;
    public static final int WORD_ITEM_ID = 2;
    //创建authority
    public static final String AUTHORITY = "com.shiluying.wordbook.provider";
    static final String URL = "content://" + AUTHORITY + "/"+DBHelper.TABLE_NAME;
    private static UriMatcher uriMatcher;
    public static final Uri CONTENT_URI = Uri.parse(URL);
    //创建静态代码块
    static {
        //实例化UriMatcher对象
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //可以实现匹配URI的功能
        uriMatcher.addURI(AUTHORITY, DBHelper.TABLE_NAME, WORD_ITEM);
        uriMatcher.addURI(AUTHORITY, DBHelper.TABLE_NAME+"/#", WORD_ITEM_ID);
    }

    //onCreate()方法
    @Override
    public boolean onCreate() {
        //实现创建DBHelper对象
        dbHelper = new DBHelper(getContext());
        db=dbHelper.getWritableDatabase();
        return true;
    }

    //删除数据表数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteInt = db.delete(DBHelper.TABLE_NAME, selection,selectionArgs);
        return deleteInt;
    }

    //插入数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID=db.insert(DBHelper.TABLE_NAME, null, values);
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    //查询数据
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c = db.query(DBHelper.TABLE_NAME,projection, selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    //更新数据
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int id=db.update(DBHelper.TABLE_NAME,values,selection,selectionArgs);
        return id;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}