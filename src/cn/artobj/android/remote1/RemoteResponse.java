package cn.artobj.android.remote1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RemoteResponse {
	public abstract void setSource(byte[] source);
    public abstract void setReqError(ServiceException e);
	public abstract boolean succeed();
	public abstract int getCount();
	
	public abstract String getMessage();
	public abstract String getResultStr() throws JSONException;
	public abstract JSONObject getResultObj() throws JSONException;
	public abstract JSONArray getResultArray() throws JSONException;
	protected abstract JSONObject getFailJson(int code,String info);
}
