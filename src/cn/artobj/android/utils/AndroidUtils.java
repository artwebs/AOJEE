package cn.artobj.android.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
	
	

}
