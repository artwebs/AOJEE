package cn.artobj.android.view.submititem;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import cn.artobj.android.utils.AndroidUtils;
import cn.artobj.aoandroid.R;
import cn.artobj.object.AOMap;

public class SubmitItem2TextBox extends SubmitItem {
	private EditText nameETxt;
	public TableRow buildItem(Context context,AOMap paraRow)
	{
		TableRow rowLayout=super.buildItem(context, paraRow);
		nameETxt=(EditText) rowLayout.findViewById(R.id.nameETxt);
		if(paraRow.containsKey("value"))
		{
			nameETxt.setHint(paraRow.getValue("value").toString());
		}
		if(paraRow.containsKey("readonly"))
		{
			if("true".equals(paraRow.getValue("readonly").toString())) AndroidUtils.setEditTextReadOnly(nameETxt);
		}
		if(paraRow.containsKey("unit"))
		{
			TextView unitTView=(TextView) rowLayout.findViewById(R.id.unitTView);
			unitTView.setVisibility(View.VISIBLE);
			unitTView.setText(paraRow.getValue("unit").toString());
		}
		return rowLayout;
	}

	@Override
	public boolean isChanged() {
		// TODO Auto-generated method stub
		if(nameETxt.getText()!=null)
			if(!"".equals(nameETxt.getText().toString()))
				return !getOriginValue().equals(nameETxt.getText().toString());
		return false;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		
		return this.isChanged()?(nameETxt.getText()==null?"":nameETxt.getText().toString()):getOriginValue();
	}

}
