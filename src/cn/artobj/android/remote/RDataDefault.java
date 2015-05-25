package cn.artobj.android.remote;

import android.util.Log;
import cn.artobj.object.AOMap;
import cn.artobj.security.AOSecurity3DES;
import cn.artobj.utils.Base64;
import cn.artobj.utils.ByteIntLong;
import cn.artobj.utils.Utils;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RDataDefault extends RData {
    private final String tag="RDataDefault";
    private String key="X+v!zSwkUloAQ$Gf/n)PVbi7";
    private String iv="k&dWHiQu";
    private int sysid=0;
    private int cType=1;
    private int ptlVersion=2;

	public RDataDefault(DATATYPE type, String cmdCode, String params) {
		super(type, cmdCode, params);
		// TODO Auto-generated constructor stub
	}

	public RDataDefault(String key, String iv, int sysid, DATATYPE type, String cmdCode, String params){
        super(type, cmdCode, params);
        this.key=key;
        this.iv=iv;
        this.sysid=sysid;
    }

    public int getcType() {
        return cType;
    }

    public String getIv() {
        return iv;
    }

    public String getKey() {
        return key;
    }

    public int getPtlVersion() {
        return ptlVersion;
    }

    public int getSysid() {
        return sysid;
    }

    @Override
	public String getAllCommand() {
		// TODO Auto-generated method stub
		return "cmd="+ Base64.encode(Utils.UrlEncode(this.getParams(), "utf-8"));
	}

    private String encodeParam()
    {
        String tmp= AOSecurity3DES.encode(this.getParams(),key,iv);
        int len=tmp.length()+25;
        byte[] rsByte=new byte[tmp.length()+32];
        rsByte[0]=(byte)this.getSysid();
        rsByte[1]=(byte)this.getcType();
        rsByte[2]=(byte)this.getPtlVersion();
        Log.w(tag, "数据大小=》" + len);
        Log.w(tag, "数据=》" + tmp);
        System.arraycopy(ByteIntLong.bytesToHexString(ByteIntLong.getBytes(len, false)).getBytes(),0,rsByte,3,4);
//        System.arraycopy(ByteIntLong.getBytes(len, false),0,rsByte,3,4);
        rsByte[7]=1;
        System.arraycopy(this.getCmdCode().getBytes(),0,rsByte,8,this.getCmdCode().length()>24?24:this.getCmdCode().length());
        System.arraycopy(tmp.getBytes(),0,rsByte,32,tmp.length());
        Log.w(tag,"字节数据=>"+ Arrays.toString(rsByte));
        try {
            return new String(rsByte,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

	public String decodeResult(byte[] source) throws Exception{
        String result="";
        byte[] lenByte=new byte[4];
        byte[] methodByte=new byte[24];
        byte[] contentByte;
        try {
            System.arraycopy(source, 3, lenByte, 0, 4);
            System.arraycopy(source, 8, methodByte, 0, 24);
            Log.w(tag, "系统类型=》" + source[0]);
            Log.w(tag, "终端类型=》" + source[1]);
            Log.w(tag, "协议版本=》" + source[2]);
            int len = ByteIntLong.getInt(lenByte, true);
            Log.w(tag, "数据大小=》" + len);
            Log.w(tag, "数据类型=》" + source[7]);
            Log.w(tag, "调用接口=》" + new String(methodByte).trim());
            contentByte = new byte[len-25];
            System.arraycopy(source, 32, contentByte, 0, len-25);
            Log.w(tag, "发送数据=》" + new String(contentByte).trim());
            result=AOSecurity3DES.decode(new String(contentByte).trim(),key,iv);
        }catch (Exception e){
            throw new Exception("数据解释错误");

        }
        return  result;
    }
	
	@Override
	public AOMap wrapRequest() throws ServiceException {
        AOMap para=new AOMap();
		para.put("cmd", encodeParam());
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		return para;
	}

	@Override
	public RemoteResponse buildResponse() {
		// TODO Auto-generated method stub
		return new DataResponse();
	}

    class DataResponse extends RemoteResponseDefault{
        public void setSource(byte[] source)
        {
            try {
                this.source=decodeResult(source);
                this.basObj=new JSONObject(this.source);
            } catch (Exception e) {
                e.printStackTrace();
                this.source=getFailJson(-1, e.getMessage()).toString();
            }finally {

            }
        }

        public String getMessage()
        {
            try {
                return this.basObj.get("message").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally{}
            return "信息解析错误";
        }
    }



}
