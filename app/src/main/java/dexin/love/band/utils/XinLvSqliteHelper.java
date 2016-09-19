package dexin.love.band.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/28/0028.
 */
public class XinLvSqliteHelper extends SQLiteOpenHelper{
    private static final String DATANAME = "myxinlv.db";
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    /*
     * 参数说明 1：上下文。 2：数据库的名字。 3:是否需要自己创建Cursor的工厂，一般的情况都不自己创建，所以就写null, 4:数据库的版本，
	 */
    public XinLvSqliteHelper(Context context) {
        super(context, DATANAME, null, VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table xinlv(_id integer primary key autoincrement,time,rate,phone)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists xinlv");
            onCreate(db);
        }
    }
}
