package cn.artobj.pool;

import android.util.Log;
import cn.artobj.object.AOMap;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class ArtPoolQueque {
	private final static String tag="ArtPoolQueque";
	private BlockingQueue<String> queue;
	private HashMap<String,AOMap> runMap=new HashMap<String,AOMap>();
	private ExecutorService exec;
	public ArtPoolQueque(int maxCount)
	{
		queue=new LinkedBlockingQueue<String>(maxCount);
		exec=Executors.newCachedThreadPool();
	}
	
	public void reLoad()
	{
		exec.submit(new RunnableObject());
	}
	
	
	public void add(String key,AOMap map)
	{
		if(!runMap.containsKey(key))
		{
			queue.add(key);
			runMap.put(key, map);
		}
		else
		{
			Log.d(tag, key + "=》正在处理");
		}
		
	}
	
	protected void remove(String key)
	{
		runMap.remove(key);
	}
	
	
	private class RunnableObject implements Runnable {
		
		public void run() {
			String key="";
			try {
				key=queue.take();
				doRun(key, runMap.get(key));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				remove(key);
			}
			
			
		}
	}
	
	public abstract void doRun(String key,AOMap map);

}
