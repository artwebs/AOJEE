package cn.artobj.android.adapter;

import java.util.HashMap;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.artobj.android.asyncimage.AsyncImageLoader;
import cn.artobj.android.asyncimage.IAsyncImageLoader;
import cn.artobj.aoandroid.R;


public class ImgListAdapter extends ListAdapter {

	protected IAsyncImageLoader loader = new AsyncImageLoader();
	public ImgListAdapter(Activity activity) {
		super(activity);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView=rowViews.get(position);
		rowView=(LinearLayout)this.activity.getLayoutInflater().inflate(R.layout.imgbinlistitem, null);
		
		TextView firstView=(TextView)rowView.findViewById(R.id.first);
		firstView.setMaxLines(2);
		HashMap<Object, Object> row=(HashMap<Object, Object>)this.getItem(position);
//			firstView.setText(row.get("text").toString());
//			Log.i("img",activity.getApplicationContext().getFilesDir().toString());
//			loader.setRootPath(activity.getApplicationContext().getFilesDir().toString());
//			ImageView imageView = (ImageView)rowView.findViewById(R.id.imageView);
//	    	CallbackImpl callbackImpl = new CallbackImpl(imageView);
//	    	Drawable cacheImage = 
//	    		loader.loadDrawable(row.get("img").toString(), callbackImpl,C.transmit.transObj);
//			if (cacheImage != null) {
//				imageView.setImageDrawable(cacheImage);
//			}
		
		rowViews.put(position, rowView);
		return rowView;
	}
	
	

}
