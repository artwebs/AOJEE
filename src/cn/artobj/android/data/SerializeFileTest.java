package cn.artobj.android.data;


import android.test.AndroidTestCase;
import android.util.Log;
import cn.artobj.object.AOMap;

public class SerializeFileTest extends AndroidTestCase {
	private final static String tag="SerializeFileTest";
	public void testSerialize()
	{
		AOMap para=new AOMap();
		para.put("id", "00001");
		para.put("name", "张三");
		para.put("sex", "男");
		
		assertEquals(SerializeFile.saveObject("/data/data/com.AOAndroid/files/com_AOAndroid_data","info", para), true);

		AOMap rs=(AOMap)SerializeFile.readObject("/data/data/com.AOAndroid/files/com_AOAndroid_data","info");
		Log.d(tag, rs.getItem().toString());
	}
}
