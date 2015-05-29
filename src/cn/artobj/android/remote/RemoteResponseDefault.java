package cn.artobj.android.remote;


import android.util.Log;
import cn.artobj.json.JSONArray;
import cn.artobj.json.JSONException;
import cn.artobj.json.JSONObject;

public class RemoteResponseDefault extends RemoteResponse {
	private final static String tag="RemoteResponse2";
	protected String source;
	private String ctlStr="";
	protected JSONObject basObj;
	
	public String getSource() {
		return source;
	}

	public RemoteResponseDefault()
	{
		
	}
	
	public RemoteResponseDefault(String source)
	{
		
	}
	

	public void setSource(byte[] source)
	{
		this.source=new String(source);
		if("".equals(this.source))this.source="0101010199010201#"+getFailJson(-1, "数据加载失败").toString();
		try {
			this.parse();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.source="0101010199010201#"+getFailJson(-1, "数据加载失败").toString();
			try {
				this.parse();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{}
	}

    @Override
    public void setReqError(ServiceException e) {
        this.source="0101010199010201#"+getFailJson(-1, e.getMessage()).toString();
    }


    @Override
	public boolean succeed() {
		if(getCode()>0)
			return true;
		return false;
	}
	
	public int getCode()
	{
		int rsInt=-1;
		try {
			rsInt=Integer.parseInt(basObj.getString("code"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rsInt;
	}
	
	public int getCount()
	{
		try {
			return Integer.valueOf(basObj.get("count").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{}
		return 0;
	}

	public String getMessage()
	{
		try {
			return basObj.get("msg").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{}
		return "信息解析错误";
	}
	
	public String getResultStr() throws JSONException
	{
		return basObj.getString("result");
	}
	
	public JSONObject getResultObj() throws JSONException
	{
		return (JSONObject) basObj.get("result");
	}
	
	public JSONArray getResultArray() throws JSONException
	{
		return (JSONArray) basObj.get("result");
	}
	
	//得到一个失败的json对象
	protected JSONObject getFailJson(int code,String info) 
	{
		JSONObject result = new JSONObject();
		try 
		{
			result.put("code", code);
			result.put("msg", info);
			result.put("count", 0);
			result.put("result", "");
		} 
		catch (JSONException e) 
		{			
		}		
		return result;
	}
	
	private void parse() throws JSONException
	{
		Log.d(tag, "source="+source);
		String[] pStr=source.split("#");
		this.ctlStr=pStr[0];
		this.basObj=new JSONObject(pStr[1]);
	}


}
