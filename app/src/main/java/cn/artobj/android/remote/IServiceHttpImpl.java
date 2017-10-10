package cn.artobj.android.remote;

import cn.artobj.object.AOMap;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


public class IServiceHttpImpl implements IService {
	private final static String TAG="IServiceHttpImpl";
	protected int recoverInterval=60;
	protected int conTimeout=5;
	protected int recvTimeout=120;
	protected int maxRouteConnections=32;
	protected int maxTotalConnections=128;
	protected HttpParams ConnectionParams=null;
	protected HttpClient httpClient=null;
	protected ClientConnectionManager ConnectionManager=null;
	protected static IService instance = null;
	protected CHARSET charset;
	public static enum CHARSET{
		UTF8("UTF-8"),
		GBK("GBK");
		private String charset;
		
		public String getCharsetStr() {
			return charset;
		}

		private CHARSET(String charset)
		{
			this.charset=charset;
		}
	};

    public CHARSET getCharset() {
        return charset;
    }

	public IServiceHttpImpl(CHARSET charset) 
	{	
		this.charset=charset;
	}
	
	/* (non-Javadoc)
	 * @see cn.softwaredata.rstechbase.remote.IService#invoke(cn.softwaredata.rstechbase.remote.RData)
	 */
	@Override
	public  byte[] invoke(RData data) throws ServiceException
	{
		this.openConn(data.buildServer());
		return execute(data);
	}
	
	protected  byte[] execute(RData data) throws ServiceException
	{
		String result = "";
        byte buffer [] = null;
		HttpPost request = null;
		
		try 
		{
			
			String curUrl=data.buildServer().getCurServer().getHttpUrl();
			request=new HttpPost(curUrl);
			request.addHeader("Connection","close");
			request.setEntity(new UrlEncodedFormEntity(convertParam(data.wrapRequest()),HTTP.UTF_8));
			HttpResponse response =this.getHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) 
			{
				throw new ServiceException(-900,"请求失败!");				
			}
			HttpEntity responseEntity = response.getEntity();
			InputStream inputStream = responseEntity.getContent();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,charset.getCharsetStr()));
//			String line = "";
//			while ((line = reader.readLine()) != null)
//			{
//				result = result + line;
//			}


            int count=0;
            byte[] temp=new byte[1024];
            int maxlen=0;
            while((count=inputStream.read(temp))!=-1)
            {
                if (buffer==null)
                {
                    buffer=new byte[maxlen+count];
                    System.arraycopy(temp,0,buffer,maxlen,count);
					temp=new byte[1024];
					maxlen+=count;
                    continue;
                }
                byte[] bakByte=new byte[maxlen+count];
                System.arraycopy(buffer,0,bakByte,0,maxlen);
				System.arraycopy(temp,0,bakByte,maxlen,count);
                buffer=new byte[maxlen+count];
                System.arraycopy(bakByte,0,buffer,0,maxlen+count);
                maxlen+=count;
				temp=new byte[1024];
            }
			
		}catch (ConnectTimeoutException e) 
		{			
			throw new ServiceException(-200,"连接主机超时!");
		}
		catch (SocketTimeoutException e) 
		{			
			throw new ServiceException(-300,"执行服务超时!");
		}
		catch (ConnectException e)
		{			
			throw new ServiceException(-400,"主机服务未启动!");
		}
		catch (SocketException e) 
		{			
			throw new ServiceException(-500,"主机过载!");
		}
		catch (ServiceException e) 
		{			
			throw e;
		}	
		catch (Exception e) 
		{			
			throw new ServiceException(-900,"未知异常，" + e.getMessage());
		}
		finally 
		{	
			if (request!=null)
				request.abort();
			closeConn();
		}	
		return buffer;
	}
	
	protected HttpClient getHttpClient()
	{
		if (this.httpClient==null)
		{
			this.httpClient=new DefaultHttpClient(this.ConnectionManager,this.ConnectionParams);
		}
		return this.httpClient;
	}
	
	protected List<NameValuePair> convertParam(AOMap para)
	{
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		for(int i=0;i<para.size();i++)
		{
			requestParams.add(new BasicNameValuePair(para.getKey(i).toString(),para.getValue(i).toString()));
		}
		return requestParams;
	}

	
	@Override
	public void openConn(ServerConfig config) {
		this.ConnectionParams= new BasicHttpParams();		
		ConnManagerParams.setMaxTotalConnections(this.ConnectionParams,this.maxTotalConnections);
		ConnManagerParams.setMaxConnectionsPerRoute(this.ConnectionParams, new ConnPerRouteBean(this.maxRouteConnections));
		//从连接池中取连接的超时时间
		ConnManagerParams.setTimeout(this.ConnectionParams, 2*1000);
		
		HttpProtocolParams.setVersion(this.ConnectionParams,HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(this.ConnectionParams,HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(this.ConnectionParams, false);
		HttpProtocolParams.setUserAgent(this.ConnectionParams,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		
		//连接超时
		HttpConnectionParams.setConnectionTimeout(this.ConnectionParams,this.conTimeout*1000);
		//请求超时
		HttpConnectionParams.setSoTimeout(this.ConnectionParams,this.recvTimeout*1000);
		//接收/传输HTTP消息时，确定socket内部缓冲区缓存数据的大小,默认为8192字节
		HttpConnectionParams.setSocketBufferSize(this.ConnectionParams,32*1024);
		//关闭Nagle算法,数据不作缓冲，立即发送
		HttpConnectionParams.setTcpNoDelay(this.ConnectionParams, true);
		//等于0表示socket关闭时，立即释放资源
		HttpConnectionParams.setLinger(this.ConnectionParams,0);
		
		SchemeRegistry schemeReg = new SchemeRegistry();
		//设置HttpClient支持HTTP和HTTPS两种模式
		schemeReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443)); 
		this.ConnectionManager= new ThreadSafeClientConnManager(this.ConnectionParams,schemeReg);
		this.httpClient=new DefaultHttpClient(this.ConnectionManager,this.ConnectionParams);
		
	}
	
	public void closeConn()
	{
		if (this.httpClient != null && this.httpClient.getConnectionManager()!= null) 
		{
			try 
			{
				httpClient.getConnectionManager().shutdown();
				httpClient=null;
			} 
			catch (Exception e) 
			{				
			}			
		}
	}
	
	protected  boolean isAbort(ServiceException exception)
	{
		boolean result=false;
		int errorCode=exception.getCode();		
		switch (errorCode) 
		{
			case -200:
				{							
					result=true;
					break;
				}
			case -300:
				{				
					result=false;
					break;
				}
			case -400:
				{
					result=true;
					break;
				}			
			case -500:
				{
					result=true;
					break;
				}			
			case -900:
				{
					result=false;
					break;
				}
			default:
				{
					result=false;
					break;
				}
		}		
		return result;
	}

	
}
