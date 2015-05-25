package cn.artobj.android.remote1;

import java.util.ArrayList;


public abstract class ServerConfig {
	private  ArrayList<Server> list;
	private int curServer=0; 
	
	public Server getCurServer() throws Exception 
	{
		init();
		if(curServer<list.size())
			return list.get(curServer);
		throw new Exception("没有配置服务器");
	}
	
	public void changeServer() throws Exception
	{
		init();
		if(curServer>=list.size())
			curServer=0;
		else
			curServer++;
	}
	
	public void init() throws Exception
	{
		if(list==null)
		{
			list=intServer();
		}
		if(list==null)
			throw new Exception("初始化服务器信息失败");
	}
	
	public abstract ArrayList<Server> intServer();
	
	public class Server
	{
		private String IpAddr;
		private int port;
		private String url;
		
		public String getIpAddr() {
			return IpAddr;
		}

		public int getPort() {
			return port;
		}

		public String getUrl() {
			return url;
		}

		public Server(String IpAddr,int port,String url)
		{			
			this.IpAddr=IpAddr;
			this.port=port;
			this.url=url;
		}
		
		public String getHttpUrl()
		{
			return this.IpAddr + ":" + String.valueOf(this.port) + this.url;
		}
	}
		
}
