package cn.zhuguangsheng.android.dbcopyman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * DBCopyManService
 * Created by zhuguangsheng on 2017/4/25.
 */

public class DBCopyManService extends Service {
    private static final String TAG = DBCopyManService.class.getSimpleName();
    private final static String ACTION_STARTCOPYDB = "cn.zhuguangsheng.android.DbCopyManService.action.ACTION_STARTCOPYDB";

    private boolean myRunFlag = false;
    private boolean mStop = false;

    //database file define
    private String DB_CUSTOM_NAME = "my.db";   //change your database file define here, e.g. "my.db";

    private String DOWNLOAD_DIR = "Download";
    private String mSrcDbFilePathName = "";
    private String mDstFilePathName =
            "/mnt/sdcard/Download/" + DB_CUSTOM_NAME;



    /**
     * generate source and destination database path file name
     */
    private void genSrcAndDstPath(){
        setSrcDbPathFileName( getAppDbPathString(DB_CUSTOM_NAME) );
        setDstDbPathFileName( getExtSdDir() + "/" + DOWNLOAD_DIR + "/" + DB_CUSTOM_NAME);
    }

    private void setSrcDbPathFileName(String srcPathName){
        mSrcDbFilePathName = srcPathName;
        Log.i(TAG, "set mSrcDbFilePathName=" + mSrcDbFilePathName);
    }
    public void setDstDbPathFileName(String dstPathName){
        mDstFilePathName = dstPathName;
        Log.i(TAG, "set mDstFilePathName=" + mDstFilePathName);
    }

    /**
     * get database file dir
     * @param name The name of the database for which you would like to get
     *          its path.
     * @return
     */
    private String getAppDbPathString(String name){
        return getApplicationContext().getDatabasePath(name).getAbsolutePath();
    }

    /**
     * get external storage dir
     * @return
     */
    private String getExtSdDir(){
        return Environment.getExternalStorageDirectory().toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(TAG, "DbDumperService onBind()...");
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "DBCopyManService onCreate()...");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "DBCopyManService onDestroy()...");
        unresigerDbDumperReceiver();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(TAG, "DBCopyManService onStartCommand()...");
        registerDbDumperReceiver();
        Log.i(TAG, "getExtSdDir()=" + getExtSdDir());
        genSrcAndDstPath();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 注册DbDumper的广播接收器
     */
    void registerDbDumperReceiver(){
        Log.i(TAG, "DBCopyManService registering receiver...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STARTCOPYDB);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);
    }

    void unresigerDbDumperReceiver(){
        Log.i(TAG, "DBCopyManService unregistering receiver...");
        unregisterReceiver(myReceiver);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "DBCopyManService receiver received...");
            new MyDumpThread().start();
        }
    };

    private boolean cloneFile(File src, File dest)
    {
        boolean retValue = true;
        final int READ_BUF_SIZE = 524288;//32768;//4096;
        long fileLength;
        int progress = 0;

        final InputStream fis;
        final OutputStream fos;

        if(src.exists()) {
            fileLength = src.length();
            if(fileLength <=0)
                return false;
        }
        else {
            return false;
        }

        try {
            fis = new BufferedInputStream(new FileInputStream(src));
            fos = new BufferedOutputStream(new FileOutputStream(dest));

            byte[] buf = new byte[READ_BUF_SIZE];
            int i;
            long totalCopyCounter=0;
            while( (i=fis.read(buf)) != -1 &&!mStop){
                fos.write(buf,0,i);
                //Log.v(TAG, "read i="+i);
                totalCopyCounter += i;
                if(i<READ_BUF_SIZE && totalCopyCounter == fileLength)	{//reach the files end
                    progress = 100;
                }
                else {
                    long longprogress = totalCopyCounter * 100 / fileLength;
                    progress = Long.valueOf( longprogress ).intValue();
                }
                Log.v(TAG, "clone progress="+progress);
                //publishCloneProgress(progress);
            }
            fis.close();
            fos.close();
            if(mStop){
                Log.v(TAG, "detect mStop==true, quit file clone process...");
                retValue = false;
            }
            else {
                retValue = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            //reportUpgradeError(OtaEventChangeListener.ERROR_PACKAGE_CLONE_FILE_ERROR);
            retValue = false;
        }finally{
        }

        Log.v(TAG, "clone file finished... retValue=" + retValue);
        return retValue;
    }

    private class MyDumpThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            //when you start
            if(myRunFlag == true){
                Log.w(TAG, "avoid duplicate thread! return");
                return;
            }else{
                myRunFlag = true;
            }

            //copy db file to Download directory
            File srcFile = null;
            File dstFile = null;
            Log.i(TAG, "mSrcDbFilePathName=" + mSrcDbFilePathName
                + ", "
                + "mDstFilePathName=" + mDstFilePathName);
            try {
                srcFile = new File(mSrcDbFilePathName);
                dstFile = new File(mDstFilePathName);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                Log.w(TAG, "File create fail 文件创建失败");
                return;
            }

            //get src file created date
            long modifiyTime = srcFile.lastModified();
            Log.i(TAG, "srcFile modifyTime 原数据库文件最后修改时间 :" + new Timestamp(modifiyTime).toString());

            boolean bResult = cloneFile(srcFile, dstFile);

            if(bResult){
                Log.i(TAG, "Db File is copied to(数据库文件拷贝到)\n" + mDstFilePathName);
            }else{
                Log.w(TAG, "Db File copy failed(拷贝失败)\n" + mDstFilePathName);
            }
            //in the end
            myRunFlag = false;
        }
    }


}
