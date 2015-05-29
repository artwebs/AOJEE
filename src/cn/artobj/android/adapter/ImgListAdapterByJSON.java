package cn.artobj.android.adapter;

import java.util.HashMap;

import cn.artobj.android.asyncimageloader.AsyncImageLoader;
import cn.artobj.android.asyncimageloader.IAsyncImageLoader;
import cn.artobj.json.JSONArray;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.artobj.R;



public class ImgListAdapterByJSON extends ListAdapterByJSON {
	protected IAsyncImageLoader loader = new AsyncImageLoader();
	public ImgListAdapterByJSON(JSONArray list, Activity activity) {
		super(list, activity);
		// TODO Auto-generated constructor stub
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
