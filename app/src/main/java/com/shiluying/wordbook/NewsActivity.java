package com.shiluying.wordbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.shiluying.wordbook.News.NewsContent;
import com.shiluying.wordbook.News.NewsContent.NewsItem;
import com.shiluying.wordbook.Server.NewsServer;
import com.shiluying.wordbook.Server.TranslateServer;
import com.shiluying.wordbook.database.DBHelper;
import com.shiluying.wordbook.database.SQLHelper;
import com.shiluying.wordbook.http.HttpUtilsSafe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class NewsActivity extends AppCompatActivity implements NewsListFragment.OnListFragmentInteractionListener,NewsFragment.OnFragmentInteractionListener{
    private NewsListFragment newsListFragment;
    private NewsFragment newsFragment;
    private FragmentTransaction transaction;
    FragmentManager fragmentManager;
    SQLiteDatabase db;
    SQLHelper sqlHelper;
    public Handler mHandler;
    private NewsServer newsServer;
    private ArrayList<String> urlList=new ArrayList<String>();
    private ArrayList newsList=new ArrayList();
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newsServer=new NewsServer();
        mHandler=new Handler(getMainLooper());
        db=new DBHelper(this).getWritableDatabase();
        sqlHelper  = new SQLHelper();
        getNewsList();
    }
    public void setLayout(){
        fragmentManager = getSupportFragmentManager();//fragment管理器
        newsListFragment=new NewsListFragment();
        newsFragment = new NewsFragment();
        Bundle arguments = new Bundle();
        arguments.putCharSequenceArrayList("newsList",newsList);
        newsListFragment.setArguments(arguments);
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.content_main, newsListFragment);//添加fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onListFragmentInteraction(NewsContent.NewsItem item) {
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.hide(newsFragment);
        transaction.commit();
        Bundle arguments = new Bundle();
        arguments.putString("title", item.title);
        arguments.putString("content", item.content);
        newsFragment = new NewsFragment();
        newsFragment.setArguments(arguments);
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.content_main, newsFragment);//添加一个fragment
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void getNewsList(){
        String url = "https://www.globaltimes.cn/odd/World/index.html";
        HttpUtilsSafe.getInstance().get(this,"srca",url, new HttpUtilsSafe.OnRequestCallBack() {
            @Override
            public void onSuccess(final String text) {//s为页面数据
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        urlList= newsServer.getNewsList(text);
                        getContents();
                    }
                });
            }
            @Override
            public void onFail(Exception e) {
                Log.e("TEST", "onFail: "+e.getMessage() );
            }
        });
    }
    public void getContents(){
        final int[] num = {1};
        final ArrayList content=new ArrayList();
        for(int i=0;i<urlList.size();i++){
            String url = urlList.get(i);
            HttpUtilsSafe.getInstance().get(this,"srca",url, new HttpUtilsSafe.OnRequestCallBack() {
                @Override
                public void onSuccess(final String text) {//text为页面数据
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> con = newsServer.getContents(text);
                            if(con!=null){
                                content.add(con);
                            }else{
                                Log.i("TTT","fail to add content");
                            }
                            NewsItem newsItem=new NewsItem(String.valueOf(num[0]),con.get(0).toString(),con.get(1).toString());
                            num[0] = num[0] +1;
                            newsList.add(newsItem);
                            if(num[0]==urlList.size()){
                                Log.i("XXX", String.valueOf(newsList.size()));
                                setLayout();
                            }
                        }
                    });
                }
                @Override
                public void onFail(Exception e) {
                    Log.e("TEST", "onFail: "+e.getMessage() );
                }
            });
        }
    }
    public void getTranslate(String word){
        final TranslateServer translateServer = new TranslateServer();
        String url=translateServer.getURL(word);
        HttpUtilsSafe.getInstance().get(this,"youdao",url, new HttpUtilsSafe.OnRequestCallBack() {
            @Override
            public void onSuccess(final String text) {//text为返回数据
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map dataMap=translateServer.getData(text);
                            if("0".equals(dataMap.get("errorCode").toString())){
                                showDialog(dataMap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFail(Exception e) {
                Log.e("TEST", "onFail: "+e.getMessage() );
            }
        });
    }
    public void showDialog(final Map worddata){
        final View layout = View.inflate(this, R.layout.word_add,
                null);
        final EditText addword,addmeaning,addphonetic,addsample;
        addword= (EditText) layout.findViewById(R.id.addword);
        addword.setText(worddata.get("word").toString());

        addmeaning= (EditText) layout.findViewById(R.id.addmeaning);
        addmeaning.setText(worddata.get("explains").toString());
        addphonetic= (EditText) layout.findViewById(R.id.addphonetic);
        addphonetic.setText(worddata.get("phonetic").toString());
        addsample=(EditText)layout.findViewById(R.id.addsample);
        builder = new AlertDialog.Builder(this)
                .setTitle("单词详情")
                .setView(layout)
                .setPositiveButton("添加至列表", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        worddata.put("sample",addsample.getText());
                        worddata.put("type",false);
                        sqlHelper.insertData(db,worddata);
                    }
                });
        builder.create().show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.wordbook:
                intent=new Intent(NewsActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.news:
                intent=new Intent(NewsActivity.this,NewsActivity.class);
//                startActivity(intent);
                break;
            default:

        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
