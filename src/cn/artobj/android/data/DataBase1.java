package cn.artobj.android.data;

import android.database.sqlite.SQLiteDatabase;
import cn.artobj.android.application.AppDefault;

public class DataBase1 extends SQLite {
	
	public DataBase1() {
		super(AppDefault.getAppContext(), AppDefault.getAppName());
		this.dbVersion=1;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * zcdic 系统字典
		 * rid 编号
		 * rgcode 
		 * rgname
		 * rkey 手机号码
		 * rvalue 选号信息
		 * rsort 排序
		 * rtime 更新时间
		 */
		db.execSQL("CREATE TABLE IF NOT EXISTS zcdic(rid integer primary key autoincrement, rgcode varchar(20), rgname varchar(500),rkey varchar(20),rvalue varchar(500),rsort interger,rtime TIMESTAMP default (datetime('now', 'localtime')))"); 
		
		/**
		 * 命令数据缓存
		 * zccmdcache
		 * rid 编号
		 * rcmd 命令
		 * rresult 结果
		 * rtime 更新时间
		 * retime 有效时间止
		 */
		db.execSQL("CREATE TABLE IF NOT EXISTS zccmdcache(rid integer primary key autoincrement, rcmd  varchar(1000), rresult text,rtime TIMESTAMP default (datetime('now', 'localtime')),retime TIMESTAMP default (datetime('now','+7 day')))"); 
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS zcdic(rid integer primary key autoincrement, rgcode varchar(20), rgname varchar(500),rkey varchar(20),rvalue varchar(500),rsort interger,rtime TIMESTAMP default (datetime('now', 'localtime')))"); 
		db.execSQL("CREATE TABLE IF NOT EXISTS zccmdcache(rid integer primary key autoincrement, rcmd  varchar(1000), rresult text,rtime TIMESTAMP default (datetime('now', 'localtime')),retime TIMESTAMP default (datetime('now','+7 day')))"); 
		
	}
	
	

}
