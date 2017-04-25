# DBCopyMan
Quick pull the android sqlite db file from device to pc for viewing and debugging within several seconds.

It is very simple to porting with the following steps.
1. in AndroidManifest.xml add
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	
	<service android:name="cn.zhuguangsheng.android.dbcopyman.DBCopyManService">
        <action android:name="com.hisense.pos.lib.DbCopymanService.action.ACTION_STARTCOPYDB"/>
    </service>

2. add package cn.zhuguangsheng.android.dbcopyman and its 2 java files to your project:
    DBCopyManService.java and DemoDBHelper.java
    
3. init sqlite database and start dbcopyman service
    private void initDemoDb(){
        DemoDBHelper dh = new DemoDBHelper(this);

        SQLiteDatabase sd = dh.getWritableDatabase();
        sd.close();
    }
	
    private void startDBCopyManService(){
        Intent intent = new Intent(this, DBCopyManService.class);
        startService(intent);
    }
		
4.  run the batch file on windows(TM) to pull the database file.
     1)   adb shell am broadcast -a cn.zhuguangsheng.android.DbCopyManService.action.ACTION_STARTCOPYDB
     2)   wait a few seconds for the db copying procedure to android device "Download" directory, by using "ping -n 5 127.0.0.1"
     3)   set android_download_dir_filename="/mnt/internal_sd/Download/my.db"
          set LOCAL_DB_NAME="my.db"
     4)   adb pull %android_download_dir_filename% %LOCAL_DB_NAME%
	 
5.  use any sqlite UI tools you love to view the database file!( e.g.Sqlite Expert or DBBrowser for Sqlite )
     