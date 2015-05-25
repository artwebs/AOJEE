package cn.artobj.android.remote;

import android.app.Activity;
import android.util.Log;
import cn.artobj.android.utils.AndroidUtils;

/**
 * Created by artwebs on 14/11/5.
 */
public class DefaultRemoteCallBack implements RemoteFactory.IRemoteCallBack  {
    private final String tag="DefaultRemoteCallBack";
    protected Activity window;

    public Activity getWindow() {
        return window;
    }

    public DefaultRemoteCallBack toWindow(Activity window) {
        this.window = window;
        return this;
    }

    public DefaultRemoteCallBack(){}

    public DefaultRemoteCallBack(Activity window){
        this.window=window;
    }

    @Override
    public void remoteFinish() {

    }

    @Override
    public void remoteError(RemoteFactory.ERROR err) {
        if(this.window!=null){
            AndroidUtils.toastShow(window, err.getError());
        }else{
            Log.w(tag,err.getError());
        }
    }

}

