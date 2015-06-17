package cn.artobj.android.view;

import android.app.Activity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.object.AOList;
import android.widget.Toast;

public class ArtListControlPage implements OnScrollListener {
	private final static String tag="ArtListViewPage";
	protected Activity window;
	protected AOList list=new AOList();
	protected ListAdapter adapter;
	protected int page=1;
	protected int pageSize=10;
	protected int dataSize=-1;

	protected boolean isLastRow = false;
	protected int lastPage =0;

	protected OnControlPageListener listener;


	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}



	public ArtListControlPage(Activity window,ListAdapter adapter)
	{
		this.window=window;
		this.adapter=adapter;
		this.adapter.setList(list);
	}
	
	public void load()
	{
		this.adapter.clearItem();
		page=1;
		setDataSize(pageSize);
		list.clear();
		loadData();
	}
	
	public void setListener(OnControlPageListener listener)
	{
		this.listener=listener;
	}
	

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {


		if (!isLastRow&&firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 ) {
			isLastRow = true;
		}

		if(dataSize!=-1&&dataSize<=totalItemCount){
			finishLoadData();
			isLastRow=false;
		}

	}

	protected void finishLoadData(){
		Toast.makeText(window, "加载完成", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		Log.e(tag, "onScrollStateChanged");
		if (lastPage !=page&&isLastRow && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			//加载元素
			loadData();
			isLastRow = false;
			lastPage =page;
		}
	}
	
	protected void loadData()
	{
		if(listener==null)
        {
			setDataSize(-1);
        	Log.d(tag, "listener 没有设置");
        	return;
        }
		listener.loadMoreData(page,pageSize);
	}
	
	public synchronized void notifyDataChanged(final AOList tmpList)
	{
		if(tmpList.size()>0)
    	{
    		window.runOnUiThread(new Runnable() {
				@Override
				public synchronized void run() {
					list.addend(tmpList);
					adapter.notifyDataSetChanged();
					if(dataSize!=-1&&dataSize<=adapter.getCount()){
						finishLoadData();
						isLastRow=false;
					}
				}
			});
    		page++;
    	}


	}

	public interface OnControlPageListener
	{
		public void loadMoreData(int page, int pageSize);
	}
	
	
}
