package cn.artobj.android.remote;

public interface IService {
	
	public abstract void openConn(ServerConfig config);
	public abstract byte[] invoke(RData data) throws ServiceException;
	public abstract void closeConn();
}