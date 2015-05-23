package cn.artobj.android.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import cn.artobj.aoandroid.R;

public class CustomProgressDialog extends ProgressDialog {
	  public CustomProgressDialog(Context context) {
		  super(context);
		 }
	  public CustomProgressDialog(Context context,int theme) { 
	           super(context,theme); 
	  }
		  
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.layout_progress);
	  
	//  setContentView(android.R.layout.alert_dialog_progress);
	  
	 }
	 public static CustomProgressDialog show (Context context) { 
	   CustomProgressDialog dialog = new CustomProgressDialog(context);
	   dialog.show();
	   return dialog;
	 }
}
