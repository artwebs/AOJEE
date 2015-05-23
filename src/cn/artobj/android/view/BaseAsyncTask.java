package cn.artobj.android.view;

import android.content.Context;
import android.os.AsyncTask;

import cn.artobj.android.utils.AndroidUtils;
import cn.artobj.object.AOMap;


public abstract class BaseAsyncTask {
	protected Context context;
	protected AsyncTask<Void,Void,AOMap> task=new AsyncTask<Void,Void,AOMap>(){

		@Override
		protected AOMap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return doRun();
		}
		@Override
		protected void onPostExecute(AOMap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);	
			doUpdate(result);
		}
	};
	
	public abstract AOMap  doRun();
	
	public void  doUpdate(AOMap result)
	{
		AndroidUtils.commDialog(context, "", result.getValue("message").toString());
		this.hide();
	}
	
	public abstract void start();
	
	public abstract void show();
	
	public abstract void hide();
	
	public void stop()
	{
		this.task.cancel(true);
	}
}
