package cn.artobj.android.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.artobj.android.app.AppDefault;
import cn.artobj.android.transmit.ITransmit;
import cn.artobj.utils.Base64;
import cn.artobj.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AndroidUtils {
	public static void setEditTextReadOnly(TextView view){  
	      view.setTextColor(Color.GRAY);   //设置只读时的文字颜色  
	      if (view instanceof EditText){
	          view.setCursorVisible(false);      //设置输入框中的光标不可见  
	          view.setFocusable(false);           //无焦点  
	          view.setFocusableInTouchMode(false);     //触摸时也得不到焦点  
	      }  
	}  
	
	public static void commDialog(Context context,String title,String message)
	{
		new AlertDialog.Builder(context)
		.setTitle(title)
        .setMessage(message)   
        .setPositiveButton("确定",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {  
                    }  
                }).show();
	}
	
	public static void toastShow(final Activity activity,final String msg)
	{
		toastShow(activity,msg,Toast.LENGTH_LONG);
	}
	
	public static void toastShow(final Activity activity,final String msg, final int duration)
	{
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, msg, duration).show();
				
			}
		});
	}

    public static String stringForPhone(String str){
        return stringInsertSpace(str,new int[]{3,7});
    }

    public  static String stringInsertSpace(String str,int[] pos){
        int count=0;
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<pos.length&&count<str.length();i++){
            if(pos[i]>=str.length())break;
            sb.append(str.substring(count,pos[i]));
            count=pos[i];
            if(count<str.length())sb.append(" ");
        }
        if(count<str.length()){
            sb.append(str.substring(count,str.length()));
        }

        return sb.toString();

    }

    public static void editTextForMobileNumber(final EditText editText){
        editTextForMobileNumber(0, editText);
    }
    public static void editTextForMobileNumber(final int sstart, final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Integer> indexs=new ArrayList<Integer>();
                indexs.add(4+sstart);
                indexs.add(9+sstart);
                if (count == 1) {
                    if (indexs.contains((s.length() + 1))) {
                        editText.setText(s + " ");
                        editText.setSelection(s.length() + 1);
                    }
                }else if (count == 0) {
                    if (s.length() > 0 &&indexs.contains((s.length() + 1))) {
                        editText.setText(s.subSequence(0, s.length() - 1));
                        editText.setSelection(s.length() - 1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public static Drawable loadImageFromUrl(String imageUrl) {
        return loadImageFromUrl(null,imageUrl);
    }

    //该方法用于根据图片的URL，从网络上下载图片
    public static Drawable loadImageFromUrl(ITransmit trans, String imageUrl) {
        return loadImageFromUrl(trans,imageUrl,-1);
    }

    public static Drawable loadImageFromUrl(String imageUrl,int quality)
    {
        return loadImageFromUrl(null,imageUrl, quality);
    }

    //该方法用于根据图片的URL，从网络上下载图片
    public static Drawable loadImageFromUrl(ITransmit trans, String imageUrl,int quality) {
        InputStream inputStream;
        try {

            if(trans!=null)
                inputStream=trans.downStream(imageUrl);
            else{
                URL url = new URL(imageUrl);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                inputStream = urlConn.getInputStream();
            }

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap bm;
            FileUtils fileUtils=new FileUtils(AppDefault.getAppName());
            String fileName= Base64.encode(imageUrl);
            File file = fileUtils.creatSDFile(fileName);
            FileOutputStream output = new FileOutputStream(file);
            Drawable drawable;
            if(quality==-1){
                bm=BitmapFactory.decodeStream(inputStream,null,bitmapOptions);
                bm.compress(Bitmap.CompressFormat.JPEG, 100,output);
                inputStream.close();
            }else{
                bitmapOptions.inSampleSize = 4;
                bm=BitmapFactory.decodeStream(inputStream,null,bitmapOptions);
                bm.compress(Bitmap.CompressFormat.JPEG, 50,output);
                inputStream.close();
            }

            drawable=Drawable.createFromStream(new FileInputStream(fileUtils.getFile(fileName)), fileName);
            output.close();
            output.flush();
            inputStream=null;
            //根据图片的URL，下载图片，并生成一个Drawable对象
            return drawable;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{}
    }

    public static void buttonTimeDisabled(final Button btn,int timelen)
    {
        final String title=btn.getText().toString();
        final Handler handler=new Handler();
        final int[] second = {timelen};
        btn.setEnabled(false);
        Runnable task = new Runnable() {
            public void run() {
                if (second[0]>0) {
                    handler.postDelayed(this, 1000);
                    second[0] -= 1;
                    btn.setText(Utils.format(second[0])+"秒");
                }else{
                    btn.setEnabled(true);
                    btn.setText(title);
                }
            }
        };
        handler.postDelayed(task, 1000);
    }



}
