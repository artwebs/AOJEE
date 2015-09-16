package cn.artobj.android.data;

import cn.artobj.android.app.AppDefault;
import cn.artobj.object.AOList;

public class AODBCmd {
	private final static String tag="ZCTechDBCmd";
	private static DataBase db= AppDefault.getInstance().buildDataBase();

	public static void instance(){
		if(db == null){
			db= AppDefault.getInstance().buildDataBase();
		}
	}

	public static void deleteAll()
	{
		instance();
		db.execute("delete from zccmdcache ", new String[]{});
	}
	
	public static void saveCmdResult(String cmd,String result)
	{
		instance();
		saveCmdResult(cmd, result, 60*24*7);
	}
	
	public static void saveCmdResult(String cmd,String result,int mm)
	{
		instance();
		db.execute("delete from zccmdcache where rcmd=?", new String[]{cmd.trim()});
		db.execute("insert into zccmdcache(rcmd,rresult,retime)values(?,?,?)", new Object[]{cmd,result,"datetime('now','+"+mm+" minute')"});
	}
	
	public static String queryCmdResult(String cmd) throws Exception
	{
		instance();
        AOList list=db.query("select * from zccmdcache where rcmd=? and rtime<datetime('now', 'localtime') and datetime('now', 'localtime')<retime", new String[]{cmd.trim()});
        if(list.size()==1)
        {
            return list.getValue(0, "rresult").toString();
        }

		return null;
	}


}
