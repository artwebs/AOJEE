package cn.artobj.android.remote1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import cn.artobj.android.application.AppDefault;
import cn.artobj.android.application.DialogStyle;
import cn.artobj.android.data.AODBCmd;
import cn.artobj.android.net.NetworkProber;
import cn.artobj.android.view.ArtCircleDialog;
import cn.artobj.pool.ArtPoolSingleFixed;
import cn.artobj.utils.Base64;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class RemoteFactory extends ArtPoolSingleFixed {
	private final static String tag="RemoteFactory";
	private RData[] arrData;
	private ArrayList<IService> listService;
	private ArrayList<CountDownLatch> listLatch;
	private ExecutorService executor;
	private IRemoteCallBack iRCallObj;
	private ArtCircleDialog dialog=null;
	private Activity window=null;
    public  static enum ERROR{
        NO_INTERENT("当前网络不可用"),NO_INTERENT_PERMISSION("当前网络不可用");

        private String error;

        public String getError() {
            return error;
        }

        private ERROR(String error){
            this.error=error;
        }
    }
	
	public abstract IService buildService();
	
	public void callServiceByAysnc(Activity window,DialogStyle style,RData data)
	{
		callServiceByAysnc(window,style,new RData[]{data});
	}
	public void callServiceByAysnc(Activity window,DialogStyle style,RData[] data)
	{
		callServiceByAysnc(window, style, data, null);
	}
    public void callServiceByAysnc(Activity window,DialogStyle style,RData data,IRemoteCallBack iRCallObj){
        callServiceByAysnc(window, style,new RData[]{data}, iRCallObj);
    }

	public void callServiceByAysnc(Activity window,DialogStyle style,RData[] data,IRemoteCallBack iRCallObj)
	{
		this.window=window;
		dialog=new ArtCircleDialog(window);
        dialog.show(style);
		callServiceByAysnc(data, iRCallObj);
	}
	
	public void callServiceByAysnc(RData data)
	{
		callServiceByAysnc(new RData[]{data},null);
	}

	public void callServiceByAysnc(RData data,IRemoteCallBack iRCallObj)
	{
		callServiceByAysnc(new RData[]{data},iRCallObj);
	}
	
	public void callServiceByAysnc(RData[] data)
	{
		callServiceByAysnc(data,null);
	}
	
	public void callServiceByAysnc(RData[] data,IRemoteCallBack iRCallObj)
	{
		this.arrData=data;
		this.iRCallObj=iRCallObj;
        try {
            if (NetworkProber.isNetworkAvailable(AppDefault.getAppContext()))
            {
                executor = Executors.newCachedThreadPool();
                this.startPool();
            }else{
				if(dialog!=null)
					dialog.close();
				if(this.iRCallObj!=null)
				{
					this.iRCallObj.remoteError(ERROR.NO_INTERENT);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, ERROR.NO_INTERENT_PERMISSION.getError());
			if(dialog!=null)
				dialog.close();
            if(this.iRCallObj!=null)
            {
                this.iRCallObj.remoteError(ERROR.NO_INTERENT_PERMISSION);
            }
        }
    }
	
	@Override
	public void run() {
		this.listService=new ArrayList<IService>();
		this.listLatch=new ArrayList<CountDownLatch>();
		CountDownLatch latch = new CountDownLatch(this.arrData.length); 
		this.listLatch.add(latch);
		for(int i=0;i<this.arrData.length;i++)
		{
			IService service=buildService();
			executor.submit(new RemoteRunner(this.arrData[i],service,latch));
			this.listService.add(service);
//			Log.e(tag, "RemoteRunner="+this.arrData[i].getSn());
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{}
//		Log.e(tag, "RemoteRunner finish");
		this.stopPool();
	}
	
	class RemoteRunner implements Runnable
	{
		private RData remoteData;
		private IService remoteService;
		private CountDownLatch remoteLatch;
		public RemoteRunner(RData remoteData,IService remoteService,CountDownLatch remoteLatch)
		{
			this.remoteData=remoteData;
			this.remoteService=remoteService;
			this.remoteLatch=remoteLatch;
		}
		
		@Override
		public void run() {
			doRmote(this.remoteData,this.remoteService);
			synchronized (listLatch) {
				this.remoteLatch.countDown();
			}
			
		}
		
		public void doRmote(RData rdata,IService remoteService)
		{
			byte[] result = null;
			if(rdata.isCache())
			{
                String cacheStr= null;
                try {
                    result= Base64.decode2Byte(AODBCmd.queryCmdResult(rdata.getAllCommand()));
					Log.w(tag,"使用缓存数据:"+rdata.getAllCommand());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {

                }
			}


			if(result==null)
			try {
                result=remoteService.invoke(rdata);
				Log.w(tag,"请求数据:"+rdata.getAllCommand());
			} catch (ServiceException e) {
                rdata.getResponse().setReqError(e);
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}finally{}
			if(isRun())
			{
				if(result!=null)
                    rdata.getResponse().setSource(result);
				if(rdata.getCallObj()!=null)
					try {
                        if(rdata.isCache()&&rdata.getResponse().succeed())
                            AODBCmd.saveCmdResult(rdata.getAllCommand(), Base64.encode2Str(result));
						rdata.getCallObj().sendMessage(rdata);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{}
			}
			else
			{
				Log.e(tag, "服务已经停止");
			}
			if(rdata.getChildren()!=null&&rdata.getResponse().succeed())
			{
				RData[] arrChild=rdata.getChildren();
				CountDownLatch childrenLautch=new CountDownLatch(arrChild.length);
				listLatch.add(childrenLautch);
				for(int i=0;i<arrChild.length;i++)
				{
					IService service=buildService();
					executor.submit(new RemoteRunner(arrChild[i],service,childrenLautch));
					listService.add(service);
				}
				try {
					childrenLautch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{}
			}
			remoteService.closeConn();
		}
	}
	
	
	
	private OnCancelListener cancel = new OnCancelListener() 
	{
		@Override
		public void onCancel(DialogInterface dialog) 
		{
			stopPool();
		}
	};
	
	
	@Override
	public synchronized void stopPool() {
        if(dialog!=null)
		    dialog.close();
		if(iRCallObj!=null)
			iRCallObj.remoteFinish();
        iRCallObj=null;
		if(executor!=null)
		{
			if(listService!=null)
				for(int i=0;i<listService.size();i++)
					listService.get(i).closeConn();
			listService=null;
			for(int i=listLatch.size()-1;i>=0;i--)
			{
				try {
					listLatch.get(i).notifyAll();
				} catch (Exception e) {
					Log.e(tag, e.getMessage());
				}finally{}
			}
			listLatch=null;
			executor.shutdownNow();
			executor=null;
		}
		super.stopPool();
	}


	
	public interface IRemoteCallBack
	{
		public void remoteFinish();
        public void remoteError(ERROR err);
	}
	
}
