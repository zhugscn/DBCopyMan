@rem This is a batch file for debug android(TM) app's database file quickly.  we sending a broadcast to notice the app copy internal db file to Download directory, and then we pull the db file from android device's Download directory, open it and browse
set android_download_dir_filename="/mnt/internal_sd/Download/my.db"
set LOCAL_DB_NAME="my.db"
set SQLITEEXPERT_PATH_NAME="D:\soft\xpwin7_commonsoft\greentools\SQLite Expert Personal 3\SQLiteExpertPers.exe"

@echo "1. Please make sure there is only ONE android devices on adb ��ȷ��ֻ��1��android�豸����"
@echo "2. Please define db file name path and name��ȷ��Ԥ�ȶ����android��Download�ļ�·��"
@echo "3. Please define db name �붨�����ݿ���"
@echo "4. Please define SQLiteExpert exe file path and name ��ȷ������SQLiteExpert��ִ���ļ�ȫ·��"

adb devices
adb shell am broadcast -a cn.zhuguangsheng.android.DbCopyManService.action.ACTION_STARTCOPYDB
@rem delay 5 seconds by ping��ʱ
ping -n 5 127.0.0.1
adb pull %android_download_dir_filename% %LOCAL_DB_NAME%
%SQLITEEXPERT_PATH_NAME% %LOCAL_DB_NAME%
