package com.shiluying.wordbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shiluying.wordbook.ContentProvider.DatabaseProvider;
import com.shiluying.wordbook.Word.WordContent;
import com.shiluying.wordbook.Word.WordContent.WordItem;
import com.shiluying.wordbook.database.DBHelper;
//import com.shiluying.wordbook.database.SQLHelper;
import com.shiluying.wordbook.enity.Record;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private String mParam1="";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private WordContent.WordItem item;
    private ListRecyclerViewAdapter mAdapter = null;
    ArrayList<WordItem> wordList=new ArrayList<WordItem>();


    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RefreshData();

    }
    public void RefreshData(){
        wordList=new ArrayList<WordItem>();
//        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
//        SQLHelper sqlHelper = new SQLHelper();
        ArrayList<Record> recordList=new ArrayList<>();

        MainActivity mainActivity=(MainActivity)getActivity();
        Cursor cursor;
        if(!"".equals(mParam1)){
            cursor = mainActivity.getContentResolver().query(
                    DatabaseProvider.CONTENT_URI,DBHelper.TABLE_COLUMNS,"word LIKE ? ",new String[] { "%" + mParam1 + "%" },null);

//            recordList=sqlHelper.fuzzyQuery(db,mParam1);
        }else{
            cursor = mainActivity.getContentResolver().query(
                    DatabaseProvider.CONTENT_URI,DBHelper.TABLE_COLUMNS,null,null,null);

//            recordList=sqlHelper.queryData(db,"");
        }
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
        for(int i=0;i<recordList.size();i++){
            if("true".equals(recordList.get(i).getWordtype())){
                WordItem word = new WordItem(recordList.get(i).getWord(),recordList.get(i).getWordMeaning());
                wordList.add(word);
            }else{
                Log.i("WORD","it is not word");
            }

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RefreshData();
        View view = inflater.inflate(R.layout.fragment_left_list, container, false);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            mAdapter=new ListRecyclerViewAdapter(wordList, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(WordContent.WordItem item);
    }
}
