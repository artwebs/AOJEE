package cn.artobj.android.asyncimageloader1;

import cn.aoandroid.transmit.ITransmit;

import android.graphics.drawable.Drawable;


public interface IAsyncImageLoader {
	public void setRootPath(String path);
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback, ITransmit trans);
}
