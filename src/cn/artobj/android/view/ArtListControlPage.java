package cn.artobj.android.view;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import cn.artobj.android.adapter.ListAdapter;
import cn.artobj.object.AOList;
import android.widget.Toast;
import cn.artobj.object.AOMap;

import java.util.HashMap;

public class ArtListControlPage implements OnScrollListener,AdapterView.OnItemClickListener {
	private final static String tag="ArtListViewPage";
	protected Activity window;
	protected AOList list=new AOList();
	protected ListAdapter adapter;
	protected int page=1;
	protected int pageSize=10;
	protected int dataSize=-1;

	protected boolean isLastRow = false;
	protected int lastPage =0;
	protected Class itemToClass=null;

	protected OnControlPageListener listener;

	public void setItemToClass(Class itemToClass) {
		this.itemToClass = itemToClass;
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
		page=1;
		setDataSize(pageSize);
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
		if(page==1){
			this.adapter.clearItem();
			list.clear();
		}
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(itemToClass==null)return;
		AOMap map=new AOMap();
		map.setItemByHashMap((HashMap)(parent.getAdapter().getItem(position)));
		Intent intent=new Intent(window,itemToClass);
		intent.putExtra("data",map.toJSONObject().toString());
		window.startActivity(intent);
	}


	public interface OnControlPageListener
	{
		public void loadMoreData(int page, int pageSize);
	}
	
	
}
