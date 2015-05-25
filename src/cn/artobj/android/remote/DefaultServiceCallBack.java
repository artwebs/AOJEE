package cn.artobj.android.remote;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import cn.artobj.android.utils.AndroidUtils;
import cn.artobj.aoandroid.R;

import java.util.concurrent.CountDownLatch;

/**
 * Created by artwebs on 14/11/5.
 */
public class DefaultServiceCallBack implements IServiceCallBack{
    private final String tag="DefaultServiceCallBack";
    private Activity window;


    public DefaultServiceCallBack toWindow(Activity window) {
        this.window = window;
        return this;
    }

    public DefaultServiceCallBack(){}
    public DefaultServiceCallBack(Activity window){
        this.window=window;
    }

    @Override
    public void sendMessage(final RData data) {
        final CountDownLatch latch = new CountDownLatch(1);
        if (!data.getResponse().succeed()&&!validation(data.getResponse()
                .getMessage())) {
            if (this.window!=null&&data.getSn() != R.string.Update)
                AndroidUtils.toastShow(window, data.getResponse()
                        .getMessage(), Toast.LENGTH_LONG);
            Log.w(tag,data.getResponse().getMessage());
            return;
        }
        try {
            if (this.window!=null){
                this.window.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            resposne(data.getResponse());
                        } catch (Exception e) {
                            e.printStackTrace();
                            AndroidUtils.toastShow(window,e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                        }
                    }

                    );
                }else {

                this.resposne(data.getResponse());
                latch.countDown();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.w(tag,e.getMessage());
        }finally {

        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            
        }

    }

    public boolean validation(String err){
        return false;
    }


    public void resposne(RemoteResponse res) throws Exception{
        Log.w(tag,res.getResultStr());
    }
}
