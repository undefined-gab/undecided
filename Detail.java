package com.my_note.com.my_note_app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.net.Uri;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.io.*;

import static com.my_note.com.my_note_app.DatabaseHelper.TABLE_NAME;
import static com.my_note.com.my_note_app.MainActivity.TAG_INSERT;
import static com.my_note.com.my_note_app.MainActivity.TAG_UPDATE;
import static com.my_note.com.my_note_app.MainActivity.dbHelper;
import static com.my_note.com.my_note_app.MainActivity.getDbHelper;

public class Detail extends AppCompatActivity {

    private SQLiteDatabase db;
    EditText title;  //标题
    EditText content;  //内容
    public DatabaseHelper deHelper=getDbHelper();
    private int tag;
    private int id;
    public static final String action = "jason.broadcast.action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        title=(EditText)findViewById(R.id.detail_title);
        content=(EditText)findViewById(R.id.detail_content);
        title.setSelection(title.getText().length());
        content.setSelection(content.getText().length());
        db= dbHelper.getWritableDatabase();
        Intent intent=getIntent();
        tag=intent.getIntExtra("TAG",-1);
        switch(tag){
            case TAG_INSERT:
                break;
            case TAG_UPDATE:
                id=intent.getIntExtra("ID",-1);
                Cursor cursor=db.query(TABLE_NAME,null,"id=?",
                        new String[]{String.valueOf(id)},null,null,null);
                if(cursor.moveToFirst()){
                    String select_title=cursor.getString(cursor.getColumnIndex("title"));
                    String select_content=cursor.getString(cursor.getColumnIndex("content"));
                    title.setText(select_title);
                    content.setText(select_content);
                    //  Log.d("Ditail","title:"+select_title);
                    //  Log.d("Detail","content"+select_content);
                }
                break;
            default:
        }
    }

    //将menu中的actionbar添加进来
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    //设置“保存”或者“删除”按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);//获取年份
        int month=ca.get(Calendar.MONTH)+1;//获取月份
        int day=ca.get(Calendar.DATE);//获取日
        int minute=ca.get(Calendar.MINUTE);//分
        int hour=ca.get(Calendar.HOUR);//小时
        int second=ca.get(Calendar.SECOND);//秒
        int WeekOfYear = ca.get(Calendar.DAY_OF_WEEK);
        String time=year+"."+month+"."+day+" "+hour+":"+minute+":"+second;
        switch(item.getItemId()){
            case R.id.save:
                if(tag==TAG_INSERT) {
                    ContentValues values = new ContentValues();
                    values.put("title", title.getText().toString());
                    values.put("content", content.getText().toString());
                    values.put("time", time);
                    db.insert(TABLE_NAME, null, values);
                    values.clear();
                    Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }else if(tag==TAG_UPDATE){
                    //修改title和content
                    String update_title=title.getText().toString();
                    String update_content=content.getText().toString();
                    ContentValues values=new ContentValues();
                    values.put("title",update_title);
                    values.put("content",update_content);
                    values.put("time", time);
                    db.update(TABLE_NAME,values,"id=?",new String[]{String.valueOf(id)});
                    finish();
                    break;
                }
            case R.id.delete:
                if(tag==TAG_UPDATE) {
                    db.delete(TABLE_NAME,"id=?",new String[]{String.valueOf(id)});
                }
                Toast.makeText(this,"Delete",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(action);
                sendBroadcast(intent);
                finish();
                break;
            case R.id.share:
                Intent share_intent = new Intent(Intent.ACTION_SEND);
                String share_title=title.getText().toString();
                String share_content=content.getText().toString();
                /*String path = getApplicationContext().getCacheDir() + "/Note/Share/" + File.separator + share_title + ".txt";
                //boolean txt = saveTxt(share_title,share_content,path);
                File file = new File(path);
                try{
                    boolean txt = createFile(file);
                    writeTxtFile(share_content,file);
                    if(txt) {
                        share_intent.setType("application/txt");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
                            share_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            share_intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path + title + ".txt"  ));
                        }
                        share_intent.putExtra(Intent.EXTRA_SUBJECT, "发送文件...");
                        share_intent.putExtra(Intent.EXTRA_TEXT, "发送文件...");
                        startActivity(Intent.createChooser(share_intent, "Share File"));
                    }
                    else
                        Toast.makeText(this,"Txt is not exit!",Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
                */
                share_intent.setType("text/plain");
                String text=share_title+"\n"+
                        share_content+"\n"+
                        "——From My Note app.";
                share_intent.putExtra(Intent.EXTRA_TEXT, text+"\n"+time);
                startActivity(share_intent);

                break;
            default:
        }
        return true;
    }

    public static boolean createFile(File fileName)throws Exception{
        boolean flag=false;
        try{
            if(!fileName.exists()){
                fileName.createNewFile();
                flag=true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static boolean writeTxtFile(String content,File fileName)throws Exception{
        RandomAccessFile mm=null;
        boolean flag=false;
        FileOutputStream o=null;
        try {
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("GBK"));
            o.close();
//   mm=new RandomAccessFile(fileName,"rw");
//   mm.writeBytes(content);
            flag=true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            if(mm!=null){
                mm.close();
            }
        }
        return flag;
    }


    public static boolean saveTxt(String title,String context,String path){
        //检测文件夹是否存在
        File file = new File(path);
        file.mkdirs();
        FileOutputStream outputStream = null;
        try {
            //创建文件，并写入内容
            outputStream = new FileOutputStream(new File(path));
            String msg = new String(context);
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;


    }
}
