package cn.zhuguangsheng.android.dbcopymandemo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.zhuguangsheng.android.dbcopyman.DBCopyManService;
import cn.zhuguangsheng.android.dbcopyman.DemoDBHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDemoDb();
        startDBCopyManService();
    }

    /**
     * init a demo db
     */
    private void initDemoDb(){
        DemoDBHelper dh = new DemoDBHelper(this);

        SQLiteDatabase sd = dh.getWritableDatabase();
        sd.close();
    }

    /**
     * start dbcopyman service
     * after start this service, you can use adb command to pull sqlite db to pc with the following steps:
     * 1.   adb shell am broadcast -a cn.zhuguangsheng.android.DbCopyManService.action.ACTION_STARTCOPYDB
     * 2.   wait a few seconds for the db copying procedure by using "ping -n 5 127.0.0.1"
     * 3.   set android_download_dir_filename="/mnt/internal_sd/Download/mydb.db"
     *      set LOCAL_DB_NAME="mydb.db"
     * 4.   adb pull %android_download_dir_filename% %LOCAL_DB_NAME%
     */
    private void startDBCopyManService(){
        //start DBCopyManService...
        Intent intent = new Intent(this, DBCopyManService.class);
        startService(intent);
    }
}
