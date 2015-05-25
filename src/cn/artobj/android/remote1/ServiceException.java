package cn.artobj.android.remote1;
/**
 * 
 * @author suzg
 * 
 */
public class ServiceException extends Exception 
{
	private int exceptionCode;	
	public ServiceException(int code,String message) 
	{
	    super(message);
	    this.exceptionCode = code;
	}
	public final int getCode() 
	{
		return exceptionCode;
    }

}
