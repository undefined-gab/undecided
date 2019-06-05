package com.my_note.com.my_note_app;


import android.content.*;
import android.widget.*;
import android.view.*;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import java.util.*;

import static com.my_note.com.my_note_app.DatabaseHelper.DB_NAME;
import static com.my_note.com.my_note_app.DatabaseHelper.TABLE_NAME;
import static com.my_note.com.my_note_app.DatabaseHelper.VERSION;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private List<Note> diary=new ArrayList<>();
    public static final int TAG_INSERT=1;
    public static final int TAG_UPDATE=0;
    private Note select_item;
    private int Id;
    ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    public static DatabaseHelper getDbHelper(){
        return dbHelper;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Detail.action);
        registerReceiver(broadcastReceiver, filter);

        Button add=(Button)findViewById(R.id.add);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout
                .OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        dbHelper=new DatabaseHelper(MainActivity.this,DB_NAME,null,VERSION);
        dbHelper.getWritableDatabase();
        init();
        //添加笔记
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Detail.class);
                intent.putExtra("TAG",TAG_INSERT);
                startActivity(intent);
            }
        });

        //设置列表项目点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent=new Intent(MainActivity.this,Detail.class);
                Id=getDiaryId(position);
                //  Log.d("MainActivity",""+id);
                intent.putExtra("ID",Id);
                intent.putExtra("TAG",TAG_UPDATE);
                startActivity(intent);
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            refresh();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.MainMenuAbout:
                MyDialogFragment f = new MyDialogFragment();
                f.show(getFragmentManager(), "mydialog");
                Toast.makeText(this, "This is my note app", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.MainMenuExit:
                finish();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        db=dbHelper.getWritableDatabase();
        diary.clear();
        //查询数据库，将title一列添加到列表项目中
        Cursor cursor=db.query(TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            String diary_item;
            String diary_time_item;
            do{
                diary_item=cursor.getString(cursor.getColumnIndex("title"));
                diary_time_item=cursor.getString( cursor.getColumnIndex("time"));
                Note n = new Note(diary_item, diary_time_item);
                diary.add(n);
            }while(cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter adapter=new MyAdapter(this,diary);
        listView=(ListView)findViewById(R.id.list_item);
        listView.setAdapter(adapter);


    }

    class MyAdapter extends ArrayAdapter<Note>
    {
        public MyAdapter(Context context, List<Note> values)
        {
            super(context, R.layout.entry, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.entry, parent, false);

            Note n = diary.get(position);

            TextView textView = (TextView) view.findViewById(R.id.entryTextView1);
            textView.setText(n.getName());

            TextView textView1 = (TextView) view.findViewById(R.id.entryTextView2);
            textView1.setText(n.getTime());

            ImageView imageView = (ImageView) view.findViewById(R.id.entryImageView1);
            imageView.setImageResource(android.R.drawable.ic_menu_info_details);

            return view;
        }

    }

    protected void refresh(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private int getDiaryId(int position){
        //获取所点击的日记的title
        int Id;
        select_item=diary.get(position);
        //获取id
        db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{"id"},"title=?",
                new String[]{select_item.getName()},null,null,null);
        cursor.moveToFirst();
        Id=cursor.getInt(cursor.getColumnIndex("id"));
        return Id;
    }
}




