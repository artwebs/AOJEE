package cn.artobj;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.artobj.android.app.AppDefault;

public class AbsSecondActivity extends AbsActivity {
	protected RelativeLayout root;
	protected LinearLayout content;
	protected LinearLayout footer;
	protected LinearLayout head;
	
	protected ImageButton headleftBtn;
	protected ImageButton headrightBtn;
	protected TextView headtitleTxt;

    public RelativeLayout getRoot() {
        return root;
    }

    public LinearLayout getContent() {
        return content;
    }

    public LinearLayout getFooter() {
        return footer;
    }

    public LinearLayout getHead() {
        return head;
    }

    public ImageButton getHeadleftBtn() {
        return headleftBtn;
    }

    public ImageButton getHeadrightBtn() {
        return headrightBtn;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        ((AppDefault)getApplication()).addActivity(this);
		root=(RelativeLayout)getLayoutInflater().inflate(R.layout.default_layout, null);
        setContentView(root);       
        content=(LinearLayout)findViewById(R.id.contentpart);
		head=(LinearLayout)findViewById(R.id.headpart);
		headleftBtn=(ImageButton)findViewById(R.id.headleftBtn);
        headrightBtn=(ImageButton)findViewById(R.id.headrightBtn);
		headtitleTxt=(TextView)findViewById(R.id.headtitleTxt);
		headleftBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}
	
	public void setHeadText(String title)
	{
		headtitleTxt.setText(title);
	}
	
	
}
