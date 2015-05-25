package cn.artobj.android.remote;


import cn.artobj.object.AOMap;

public abstract class RData {
	private long sn;
	private String cmdCode;
	private String params;
	private DATATYPE type;
	private RemoteResponse response;
	private IServiceCallBack callObj;
	private RData[] arrChild;
	private boolean isCache=false;
	public static enum DATATYPE
	{
		ENCRYPTION(8),
		NOENCRYPTION(4);
		
		private int inmode;
		
		private DATATYPE(int inmode)
		{
			this.inmode=inmode;
		}
		
		public int getInMode()
		{
			return inmode;
		}
	}
	
	
	public long getSn() {
		return sn;
	}

	public String getCmdCode() {
		return cmdCode;
	}

	public String getParams() {
		return params;
	}

	

	public DATATYPE getType() {
		return type;
	}

	public RemoteResponse getResponse() {
		return response;
	}
	

	public IServiceCallBack getCallObj() {
		return callObj;
	}

	public RData toCallObj(IServiceCallBack callObj) {
		this.callObj = callObj;
		return this;
	}
	
	public RData[] getChildren() {
		return arrChild;
	}

	public RData setChild(RData child) {
		return setChildren(new RData[]{child});
	}
	
	public RData setChildren(RData[] childarr)
	{
		this.arrChild = childarr;
		return this;
	}
	
	
	
	public RData setCache(boolean isCache) {
		this.isCache = isCache;
		return this;
	}

	public boolean isCache() {
		return isCache;
	}

	public RData(DATATYPE type,String cmdCode, String params) {
		this(System.currentTimeMillis(),type,cmdCode,params);
	}
	public RData(DATATYPE type,String cmdCode, String params,IServiceCallBack callObj) {
		this(System.currentTimeMillis(),type,cmdCode,params,callObj);
	}
	
	public RData(long sn, DATATYPE type, String cmdCode, String params) {
		this(sn,type,cmdCode,params,false);
	}
	public RData(long sn, DATATYPE type, String cmdCode, String params,boolean iscatche) {
		this.sn = sn;
		this.cmdCode = cmdCode;
		this.params = params;
		this.type = type;
		this.response=this.buildResponse();
		this.isCache=iscatche;
	}
	
	public RData(long sn, DATATYPE type, String cmdCode, String params,IServiceCallBack callObj) {
		this(sn,type,cmdCode,params,false);
		this.callObj=callObj;
	}
	
	public RData(long sn, DATATYPE type, String cmdCode, String params,IServiceCallBack callObj,boolean iscatche) {
		this(sn,type,cmdCode,params,iscatche);
		this.callObj=callObj;
	}

	public abstract String getAllCommand();
	public abstract RemoteResponse buildResponse();
	public abstract ServerConfig buildServer();
	public abstract AOMap wrapRequest()  throws ServiceException, Exception;
	
	public void buildParamValue(String[] args)
	{
		if(args==null)return;
		int i=0;
		while(i<args.length)
		{
			this.params=this.params.replaceFirst("\\?", args[i++]);
		}
	}
	
	public void buildChildParamValue(String[] args)
	{
		if(args==null||this.arrChild==null)return;
		for(int i=0;i<arrChild.length;i++)
			arrChild[i].buildParamValue(args);
	}
	
	public void buildChildParamValue(int[] indexs,String[] args)
	{
		if(args==null||this.arrChild==null)return;
		for(int i=0;i<arrChild.length;i++)
			for(int j=0;j<indexs.length;j++)
				if(indexs[j]==i)arrChild[i].buildParamValue(args);
	}
	
	public static int getBytesLength(String str) 
    {
    	try 
    	{    		
    		return 	str.getBytes("UTF-8").length;
    	} 
    	catch (Exception e) 
    	{
			return -1;
		}    	
    }
	
}
