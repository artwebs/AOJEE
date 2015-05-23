package cn.artobj.android.view;

import android.app.Activity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.object.AOList;

public class ArtListControlPage implements OnScrollListener {
	private final static String tag="ArtListViewPage";
	private AOList list=new AOList();
	private  Activity window;
	private ListAdapter adapter;
	private int page=1;
	private int pageSize=10;
	private int visibleLastIndex = 0;  
	private int visibleItemCount=0;  
	private int dataSize=0;
	private OnControlPageListener listener;
	
	public AOList getList() {
		return list;
	}

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
		visibleLastIndex = 0;
		visibleItemCount=0;
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
		this.visibleItemCount = visibleItemCount;  
        visibleLastIndex = firstVisibleItem + visibleItemCount;    
        if(totalItemCount == list.size()+1){  
//	            listView.removeFooterView(loadMoreLayout);  
            Toast.makeText(window, "加载完成", Toast.LENGTH_LONG).show();  
        }  
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(tag, "onScrollStateChanged");
		int itemsLastIndex = adapter.getCount()-1;    
        int lastIndex = itemsLastIndex + 1;  
        Log.d(tag, (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL )+"");
        Log.d(tag, (visibleLastIndex == lastIndex )+"");
        Log.d(tag, (adapter.getCount()<dataSize-1)+"");
        Log.d(tag, "------------------------");
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL  
                && visibleLastIndex == lastIndex &&adapter.getCount()<dataSize-1) { 
        	Log.d(tag, "loadMoreData");
        	loadData();
        }  
	}
	
	private void loadData()
	{
		if(listener==null)
        {
			setDataSize(0);
        	Log.d(tag, "listener 没有设置");
        	return;
        }
		listener.loadMoreData(page,pageSize);
	}
	
	public void notifyDataChanged(AOList tmpList)
	{
		if(tmpList.size()>0)
    	{
    		list.addend(tmpList);
    		window.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
    		dataSize+=tmpList.size();
    		page++;
    	}
	}

	public interface OnControlPageListener
	{
		public void loadMoreData(int page, int pageSize);
	}
	
	
}
