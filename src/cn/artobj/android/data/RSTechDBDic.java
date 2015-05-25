package cn.artobj.android.data;


import android.content.ContentValues;
import android.util.Log;
import cn.artwebs.object.BinList;
import com.rstechbase.comm.RSTechApp;
import org.json.JSONArray;

public class RSTechDBDic {
	private final static String tag="RSTechDBDic";
	private static DataBase db= RSTechApp.getInstance().buildDataBase();
	
	
	public static void deleteAll()
	{
		db.execute("delete from zcdic ", new String[]{});
	}
	
	public static JSONArray getGroupJSONArray(String rgcode)
	{
		BinList list=db.query("select rkey,rvalue from zcdic where rgcode=? order by rsort", new String[]{rgcode.trim()});
		return list.getArray2JSONArray();
	}
	
	public synchronized static void saveDic(String rgcode,String rgname,String rkey,String rvalue,String rsort)
	{
		db.execute("delete from zcdic where rgcode=? and rkey=?", new String[]{rgcode.trim(),rkey.trim()});
		db.execute("insert into zcdic(rgcode,rgname,rkey,rvalue,rsort)values(?,?,?,?,?)", new Object[]{rgcode,rgname,rkey,rvalue,rsort});
	}
	
	public synchronized static void updateDic(String rgcode,String rkey,String rvalue)
	{
		int count=db.query("select * from zcdic where rgcode=? and rkey=?", new String[]{rgcode.trim(),rkey.trim()}).size();
		if(count==1)
		{
			ContentValues values=new ContentValues();
			values.put("rvalue", rvalue);
			db.update("zcdic", values, "rid=?", new String[]{rvalue.trim()});
		}
		else
		{
			Log.e(tag, "数据异常，无法完成更新"+count);
		}
	}
	
	public  static String dicOut(String rgcode,String rkey,String defaultValue)
	{
		BinList list=db.query("select * from zcdic where rgcode=? and rkey=?", new String[]{rgcode.trim(),rkey.trim()});
		if(list.size()==1)
			return list.getValue(0, "rvalue").toString();
		return defaultValue;
	}
	
	public  static String dicIn(String rgcode,String rvalue,String defaultkey)
	{
		BinList list=db.query("select * from zcdic where rgcode=? and rvalue=?", new String[]{rgcode.trim(),rvalue.trim()});
		if(list.size()==1)
			return list.getValue(0, "rkey").toString();
		return defaultkey;
	}
}
