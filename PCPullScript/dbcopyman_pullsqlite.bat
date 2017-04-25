@rem This is a batch file for debug android(TM) app's database file quickly.  we sending a broadcast to notice the app copy internal db file to Download directory, and then we pull the db file from android device's Download directory, open it and browse
set android_download_dir_filename="/mnt/internal_sd/Download/my.db"
set LOCAL_DB_NAME="my.db"
set SQLITEEXPERT_PATH_NAME="D:\soft\xpwin7_commonsoft\greentools\SQLite Expert Personal 3\SQLiteExpertPers.exe"

@echo "1. Please make sure there is only ONE android devices on adb 请确保只有1个android设备在线"
@echo "2. Please define db file name path and name请确保预先定义好android的Download文件路径"
@echo "3. Please define db name 请定义数据库名"
@echo "4. Please define SQLiteExpert exe file path and name 请确保定义SQLiteExpert可执行文件全路径"

adb devices
adb shell am broadcast -a cn.zhuguangsheng.android.DbCopyManService.action.ACTION_STARTCOPYDB
@rem delay 5 seconds by ping延时
ping -n 5 127.0.0.1
adb pull %android_download_dir_filename% %LOCAL_DB_NAME%
%SQLITEEXPERT_PATH_NAME% %LOCAL_DB_NAME%
