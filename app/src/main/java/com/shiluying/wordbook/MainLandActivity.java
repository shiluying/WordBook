package com.shiluying.wordbook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shiluying.wordbook.Server.TranslateServer;
import com.shiluying.wordbook.Word.WordContent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.shiluying.wordbook.database.*;
import com.shiluying.wordbook.http.HttpUtilsSafe;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainLandActivity extends AppCompatActivity implements LeftFragment.OnListFragmentInteractionListener,RightFragment.OnFragmentInteractionListener {
    private AlertDialog.Builder builder;
    SQLiteDatabase db;
    SQLHelper sqlHelper;
    private String fuzzyword="";
    private LeftFragment leftfragment;
    private RightFragment rightfragment;
    private FragmentTransaction transaction;
    FragmentManager fragmentManager;
    Handler mHandler;
    String word="",wordmeaning="",wordsample="",wordphonetic="";
    public static void showActivity(Activity activity){
        Intent intent = new Intent(activity, MainLandActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_land);
        db=new DBHelper(this).getWritableDatabase();
        sqlHelper  = new SQLHelper();

        fragmentManager = getSupportFragmentManager();//fragment管理器
        leftfragment=new LeftFragment();
        rightfragment = new RightFragment();
        mHandler=new Handler(getMainLooper());

        setLayout();
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInput();
            }
        });

        Button search = findViewById(R.id.searchbutton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edittext = (EditText) findViewById(R.id.search_edit_text);
                fuzzyword = edittext.getText().toString();
                setLayout();
            }
        });

        final Button changeList = findViewById(R.id.changeList);
        changeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listType=changeList.getText().toString();
                if("生词本".equals(listType)){
                    ListFragment listFragment = new ListFragment();
                    transaction = fragmentManager.beginTransaction();//开启一个事务
                    transaction.replace(R.id.wordslist, listFragment);//添加fragment
                    transaction.addToBackStack(null);
                    transaction.commit();
                    changeList.setText("单词列表");
                }else{
                    leftfragment = new LeftFragment();
                    transaction = fragmentManager.beginTransaction();//开启一个事务
                    transaction.replace(R.id.wordslist, leftfragment);//添加fragment
                    transaction.addToBackStack(null);
                    transaction.commit();
                    changeList.setText("生词本");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.wordbook:
                intent=new Intent(MainLandActivity.this,MainLandActivity.class);
                break;
            case R.id.news:
                intent=new Intent(MainLandActivity.this,NewsActivity.class);
                startActivity(intent);
                break;
            default:

        }
        return true;
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onListFragmentInteraction(WordContent.WordItem item) {
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.hide(rightfragment);
        transaction.commit();
        Bundle arguments = new Bundle();
        arguments.putString("word", item.word);
        rightfragment = new RightFragment();
        rightfragment.setArguments(arguments);
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.worddetail, rightfragment);//添加一个fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Toast.makeText(getApplicationContext(), "横屏x", Toast.LENGTH_SHORT).show();
            MainLandActivity.showActivity(this);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "竖屏x", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLayout(){
        Bundle arguments = new Bundle();
        Log.i("TEST",fuzzyword);
        arguments.putString("word",fuzzyword);
        fuzzyword="";
        leftfragment = new LeftFragment();
        leftfragment.setArguments(arguments);
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.wordslist, leftfragment);//添加fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void showInput() {

        final View layout = View.inflate(this, R.layout.word_add,
                null);
        builder = new AlertDialog.Builder(this)
                .setTitle("添加单词")
                .setView(layout)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText edittext;
                        edittext = (EditText) layout.findViewById(R.id.addword);
                        word = edittext.getText().toString();
                        edittext = (EditText) layout.findViewById(R.id.addsample);
                        wordsample = edittext.getText().toString();
                        getWordData();
                    }
                });
        builder.create().show();
    }
    public void getWordData(){
        final TranslateServer translateServer = new TranslateServer();
        String url=translateServer.getURL(word);
        HttpUtilsSafe.getInstance().get(this,"youdao",url, new HttpUtilsSafe.OnRequestCallBack() {
            @Override
            public void onSuccess(final String text) {//text为返回数据
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("TRANSLATE",text);
                        Map<String,Object> map = null;
                        try {
                            map = translateServer.getData(text);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String errorCode = map.get("errorCode").toString();
                        if("0".equals(errorCode)){
                            wordphonetic = map.get("phonetic").toString();
                            wordmeaning=map.get("explains").toString();;
                        }else{
                            Log.i("ADD","fail to add.");
                        }
                        sqlHelper.insertData(db,word,wordphonetic,wordmeaning,wordsample,"false");
                        word="";
                        wordmeaning="";
                        wordsample="";
                        wordphonetic="";
                        setLayout();
                    }
                });
            }
            @Override
            public void onFail(Exception e) {
                Log.e("TEST", "onFail: "+e.getMessage() );
            }
        });
    }
    @Override
    public void onRightFragmentInteraction() {
        setLayout();
    }

    @Override
    public void OnRightFragmentInteractionListener(Uri uri) {

    }
}