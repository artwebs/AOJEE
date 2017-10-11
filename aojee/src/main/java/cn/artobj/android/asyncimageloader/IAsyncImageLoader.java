package cn.artobj.android.asyncimageloader;


import android.graphics.drawable.Drawable;
import cn.artobj.android.transmit.ITransmit;


public interface IAsyncImageLoader {
	public void setRootPath(String path);
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback, ITransmit trans);
}
