package com.shiluying.wordbook.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shiluying.wordbook.enity.*;

import java.util.ArrayList;
import java.util.Map;

public class SQLHelper {
    public ArrayList<Record> queryData(SQLiteDatabase db,String arg) {
        String selection = null;
        String[] selectionArgs=null;
        if (arg != "") {
            selection = "word=?";
            selectionArgs = new String[]{arg};
        }
        ArrayList<Record> recordList = new ArrayList<Record>();
        Cursor cursor;

        cursor = db.query(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMNS, selection,selectionArgs, null, null, null);
        try {
            while (cursor != null && cursor.moveToNext()) {
                Record record = new Record();
                record.setWord(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD)));
                record.setWordMeaning(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_MEANING)));
                record.setWordphonetic(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_PHONETIC)));
                record.setWordSample(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_SAMPLE)));
                record.setWordtype(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_TYPE)));
                recordList.add(record);
                Log.i("QUERY", "queryData record = " + record.toString());
            }
        } catch (SQLException e) {
            Log.e("QUERY", "queryData exception", e);
        }
        cursor.close();
        return recordList;
    }
    public void updateDataType(SQLiteDatabase db,String word,String type) {
        ContentValues values;
        values = new ContentValues();
        values.put("wordtype", type);
        db.update(DBHelper.TABLE_NAME, values, "word=?", new String[]{word});
    }
    public void insertData(SQLiteDatabase db, Map worddata){
        ContentValues contentValues = new ContentValues();
        contentValues.put("word",worddata.get("word").toString());
        contentValues.put("wordmeaning",worddata.get("explains").toString());
        contentValues.put("wordphonetic",worddata.get("phonetic").toString());
        contentValues.put("wordsample",worddata.get("sample").toString());
        contentValues.put("wordtype",worddata.get("type").toString());
        db.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("INSERT", "INSERT DATA");
    }
    public void insertData(SQLiteDatabase db,String word,String phonetic,String meaning,String sample,String type){
        ContentValues contentValues = new ContentValues();
        contentValues.put("word",word);
        contentValues.put("wordmeaning",meaning);
        contentValues.put("wordphonetic",phonetic);
        contentValues.put("wordsample",sample);
        contentValues.put("wordtype",type);
        db.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("INSERT", "INSERT DATA");
    }
    public void deleteData(SQLiteDatabase db,String arg){
        db.delete(DBHelper.TABLE_NAME,"word=?", new String[]{arg});
    }
    public void updateData(SQLiteDatabase db,String word,String wordmeaning,String wordphonetic,String wordsample){
        ContentValues values;

        values=new ContentValues();
        values.put("word",word);
        db.update(DBHelper.TABLE_NAME,values,"word=?", new String[]{word});

        values=new ContentValues();
        values.put("wordmeaning",wordmeaning);
        db.update(DBHelper.TABLE_NAME,values,"word=?", new String[]{word});

        values=new ContentValues();
        values.put("wordphonetic",wordphonetic);
        db.update(DBHelper.TABLE_NAME,values,"word=?", new String[]{word});

        values=new ContentValues();
        values.put("wordsample",wordsample);
        db.update(DBHelper.TABLE_NAME,values,"word=?", new String[]{word});
    }
    public ArrayList<Record> fuzzyQuery(SQLiteDatabase db,String str){
        String selection = selection = "word";
        ArrayList<Record> recordList = new ArrayList<Record>();
        Cursor cursor;
        cursor = db.query(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMNS, selection+" LIKE ? ",
                new String[] { "%" + str + "%" }, null, null, null);
        try {
            while (cursor != null && cursor.moveToNext()) {
                Record record = new Record();
                record.setWord(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD)));
                record.setWordMeaning(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_MEANING)));
                record.setWordSample(cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_WORD_SAMPLE)));
                recordList.add(record);
                Log.i("QUERY", "queryData record = " + record.toString());
            }
        } catch (SQLException e) {
            Log.e("QUERY", "queryData exception", e);
        }
        cursor.close();
        return recordList;
    }
}
