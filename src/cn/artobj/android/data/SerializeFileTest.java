package cn.artobj.android.data;

import cn.aoandroid.object.BinMap;

import android.test.AndroidTestCase;
import android.util.Log;

public class SerializeFileTest extends AndroidTestCase {
	private final static String tag="SerializeFileTest";
	public void testSerialize()
	{
		BinMap para=new BinMap();
		para.put("id", "00001");
		para.put("name", "张三");
		para.put("sex", "男");
		
		assertEquals(SerializeFile.saveObject("/data/data/com.AOAndroid/files/com_AOAndroid_data","info", para), true);
		
		BinMap rs=(BinMap)SerializeFile.readObject("/data/data/com.AOAndroid/files/com_AOAndroid_data","info");
		Log.d(tag, rs.getItem().toString());
	}
}
