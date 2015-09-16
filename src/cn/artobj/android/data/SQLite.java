package cn.artobj.android.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.artobj.object.AOList;

public abstract class SQLite{
	protected SQLiteOpenHelper dbHelper;
	protected SQLiteDatabase db=null;
	protected String dbName="artwebs_app.db";
	protected int dbVersion=1;
	protected Context context=null;
	
	public  SQLite(Context context)
	{
		this.context=context;
	}
	public SQLite(Context context,String dbName)
	{
		this.context=context;
		this.dbName=dbName;
	}
	
	public SQLiteDatabase getDb() {
		return db;
	}
	
	public void connection()
	{	
		if(this.db==null)
		{
			this.dbHelper=new SQLiteOpenHelper(context, dbName, null, dbVersion)
			{
				@Override
				public void onCreate(SQLiteDatabase db) {
					SQLite.this.onCreate(db);
				}

				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
					SQLite.this.onUpgrade(db, oldVersion, newVersion);
				}
				
			};
			this.db=dbHelper.getWritableDatabase();
		}
			
	}
	
	public abstract void onCreate(SQLiteDatabase db);
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	
	public AOList query(String sql,String[] selectionArgs)
	{
		this.connection();
		AOList list=new AOList();
		Cursor cursor = this.db.rawQuery(sql, selectionArgs);
		int j=0;
		while (cursor.moveToNext())
		{
			for(int i=0;i<cursor.getColumnCount();i++)
			{
				list.put(j, cursor.getColumnName(i).toString(), cursor.getString(i));
			}
			j++;
		}
		cursor.close();
		this.close();
		return list;
		
	}
	
	public boolean execute(String sql,Object[] bindArgs)
	{
		boolean flag=false;
		this.connection();
		 //开启事务
		this.db.beginTransaction();
		try
		{
			if(bindArgs==null)
				this.db.execSQL(sql);
			else
				this.db.execSQL(sql, bindArgs);
			this.db.setTransactionSuccessful();
			flag=true;
		}
		finally
		{
			this.db.endTransaction();
		}	
		this.close();
		return flag;
	}
	
	public boolean insert(String table, String nullColumnHack, ContentValues values)
	{
		boolean flag=false;
		this.connection();
		this.db.beginTransaction();
		try
		{
			this.db.insert(table, nullColumnHack, values);
			this.db.setTransactionSuccessful();
			flag=true;
		}
		finally
		{
			this.db.endTransaction();
		}	
		this.close();
		
		return flag;
	}


	public boolean insertMult(String table, String nullColumnHack, ContentValues values)
	{
		boolean flag=false;
		this.connection();
		this.db.beginTransaction();
		try
		{
			this.db.insert(table, nullColumnHack, values);
			this.db.setTransactionSuccessful();
			flag=true;
		}
		finally
		{
			this.db.endTransaction();
		}
		return flag;
	}

	
	public boolean delete(String table, String whereClause, String[] whereArgs) 
	{
		boolean flag=false;
		this.connection();
		this.db.beginTransaction();
		try
		{
			this.db.delete(table, whereClause, whereArgs);
			this.db.setTransactionSuccessful();
			flag=true;
		}
		finally
		{
			this.db.endTransaction();
		}	
		this.close();
		
		return flag;
		
	}
	
	public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs)
	{
		boolean flag=false;
		this.connection();
		this.db.beginTransaction();
		try
		{
			this.db.update(table, values, whereClause, whereArgs);
			this.db.setTransactionSuccessful();
			flag=true;
		}
		finally
		{
			this.db.endTransaction();
		}	
		this.close();
		return flag;
	}
	
	public void close()
	{
		if(this.db!=null)
			this.db.close();
		this.db=null;
	}
	
}
