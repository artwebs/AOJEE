package cn.artobj.android.adapter;

import java.util.HashMap;

import cn.artobj.R;
import cn.artobj.android.asyncimageloader.AsyncImageLoader;
import cn.artobj.android.asyncimageloader.IAsyncImageLoader;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ImgListAdapter extends ListAdapterDefault {

	protected IAsyncImageLoader loader = new AsyncImageLoader();
	public ImgListAdapter(Activity activity) {
		super(activity);
	}




	@Override
	public int initLayoutID() {
		return R.layout.imgbinlistitem;
	}

	@Override
	public ViewHolder initViewHolder() {
		return new ViewHolder();
	}



}
