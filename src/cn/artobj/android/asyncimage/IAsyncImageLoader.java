package cn.artobj.android.asyncimage;


import android.graphics.drawable.Drawable;
import cn.artobj.android.transmit.ITransmit;


public interface IAsyncImageLoader {
	public void setRootPath(String path);
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback, ITransmit trans);
}
