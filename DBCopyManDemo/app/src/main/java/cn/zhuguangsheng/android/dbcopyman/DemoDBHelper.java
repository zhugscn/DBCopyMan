package cn.zhuguangsheng.android.dbcopyman;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DemoDBHelper
 * Created by zhuguangsheng on 2017/4/25.
 */

public class DemoDBHelper extends SQLiteOpenHelper
{
    public static final String DB_CUSTOME_NAME = "my.db";
    public static final int DB_VERSION = 1;

    public DemoDBHelper(Context context){
        super(context, DB_CUSTOME_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String creaTTable = "create table user (_id integer PRIMARY KEY AUTOINCREMENT NOT NULL,name varchar,age int)";
        db.execSQL(creaTTable);

        ContentValues cv = new ContentValues();

        cv.put("name","tom");
        cv.put("age", "20");
        Long l = db.insert("user", null, cv);

    }

    //升级数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}