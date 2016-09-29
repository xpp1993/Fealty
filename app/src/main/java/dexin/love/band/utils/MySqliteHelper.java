package dexin.love.band.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/23.
 */
public class MySqliteHelper extends SQLiteOpenHelper {
    private static final String DATANAME = "mygps.db";
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    /*
     * 参数说明 1：上下文。 2：数据库的名字。 3:是否需要自己创建Cursor的工厂，一般的情况都不自己创建，所以就写null, 4:数据库的版本，
	 */
    public MySqliteHelper(Context context) {
        super(context, DATANAME, null, VERSION);
        db = this.getReadableDatabase();
    }
    // 创建表结构
    // 操作表结构：SQLiteDatabase
    // 这方法只会执行一次。
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table gps(_id integer primary key autoincrement,time,lat,lon)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists gps");
            onCreate(db);
        }
    }
   // 插入
    // insert into gps(time,lat,lon) values (?,?,?)
    public void insertGPS(String sql, ContentValues values) {
        long _id = db.insert("gps", null, values);
        System.out.println("_id----" + _id);
    }
    // 更改
    public void update(ContentValues values, String[] args) {
        int count = db.update("gps", values, "_id=?", args);
        System.out.println("--update---" + count);
    }
    // 查询数据。
    public Cursor getAllGPS(String sql, String[] args) {
        return db.rawQuery(sql, args);
    }
    // 查询数据将数据转换为List集合。
    public List<Map<String, String>> CursorToList(String sql, String[] args) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Cursor cursor = db.rawQuery(sql, args);
        String[] columnNames = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < columnNames.length; i++) {
                int index = cursor.getColumnIndex(columnNames[i]);
                int type = cursor.getType(index);
                String value = "";
                switch (type) {
                    case Cursor.FIELD_TYPE_STRING:
                        value = cursor.getString(index);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = cursor.getFloat(index) + "";
                        break;

                    default:
                        break;
                }
                map.put(columnNames[i], value);
            }
            list.add(map);
        }
        return list;
    }
    // 返回数据库的数据的总条数
    public int getCount(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public Cursor getQueryGPS(String sql, String[] args) {
        return db.rawQuery(sql, args);
    }
    // 删除
    public void delete(String[] args) {
        int num = db.delete("gps", "time < ?", args);
        System.out.println(num);
    }

    // 更改数据库中的数据:插入，更改，删除
    public void update_all(String sql) {
        db.execSQL(sql);
    }

}
