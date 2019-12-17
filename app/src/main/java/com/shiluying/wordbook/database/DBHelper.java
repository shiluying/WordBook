package com.shiluying.wordbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public final static String TABLE_NAME = "wordList";
    public final static String RECORD_WORD = "word";
    public final static String RECORD_WORD_MEANING = "wordmeaning";
    public final static String RECORD_WORD_PHONETIC = "wordphonetic";
    public final static String RECORD_WORD_SAMPLE = "wordsample";
    public final static String RECORD_WORD_TYPE = "wordtype";
    public static final String[] TABLE_COLUMNS = {
            RECORD_WORD,
            RECORD_WORD_MEANING,
            RECORD_WORD_PHONETIC,
            RECORD_WORD_SAMPLE,
            RECORD_WORD_TYPE
    };
    public DBHelper(Context context) {
        super(context, "Database.db", null, 1);
//        deleteDatabase(context);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            SQLiteStatement stmt;
            // 涉及多个表和数据用事务保证完整一致
            db.beginTransaction();
//             IF NOT EXISTS
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    RECORD_WORD + " TEXT," +
                    RECORD_WORD_MEANING + " TEXT," +
                    RECORD_WORD_PHONETIC + " TEXT," +
                    RECORD_WORD_TYPE + " TEXT, "+
                    RECORD_WORD_SAMPLE + " TEXT)";
            Log.i("TEST", sql);
            db.execSQL(sql);
            //成功提交
            db.setTransactionSuccessful();
        }catch (Exception e) {
            Log.i("TEST","初始化数据库失败!!");
        }finally{
            db.endTransaction();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("word","dog");
        contentValues.put("wordmeaning","狗");
        contentValues.put("wordphonetic","dɒɡ");
        contentValues.put("wordsample","This is a dog.");
        contentValues.put("wordtype","false");
        db.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("INSERT", "INSERT DATA");
        contentValues = new ContentValues();
        contentValues.put("word","this");
        contentValues.put("wordmeaning","[pron. 这，这个；这样；今，本；……的这个；有个, adj. 这，这个（离说话人较近的）, adv. 这样地，这么, n. (This) （法、美、印、巴）蒂斯（人名）]");
        contentValues.put("wordphonetic","ðɪs");
        contentValues.put("wordsample","");
        contentValues.put("wordtype","false");
        db.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("INSERT", "INSERT DATA");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase("Database.db");
    }

}
