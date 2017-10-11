package cn.artobj.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public abstract class AOService extends Service {

	private static LocalBroadcastManager broadcaster;

	private static String SERVICE_RESULT="cn.aoandroid.service.ArtService";
	public final static String RESULT_TAG="tag";
	private static boolean isSendbroad=false;
	private static AOService self;


	
	public static void setServiceResult(String tag)
	{
		SERVICE_RESULT=tag;
		if(SERVICE_RESULT==null)isSendbroad=false;
		
	}
	
	public static void setBroadcaster(LocalBroadcastManager bdcaster)
	{
		broadcaster=bdcaster;
	}
	
	public  static String getserviceSign()
	{
		return SERVICE_RESULT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		serviceRun();
		return super.onStartCommand(intent, flags, startId);
	}

	public abstract void serviceRun();
	
	public void sendResult(String message)
	{
		 Intent intent = new Intent(SERVICE_RESULT);
		 if(message != null&&isSendbroad)
		        intent.putExtra(RESULT_TAG,message);
		    if(broadcaster!=null)
		    	broadcaster.sendBroadcast(intent);
	}
	
	class ToClass {
		
		public Class getToClass()
		{
			return this.getClass();
		}
	}

}
