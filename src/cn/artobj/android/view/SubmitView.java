package cn.artobj.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import cn.artobj.android.view.submititem.SubmitItem;
import cn.artobj.object.AOList;
import cn.artobj.object.AOMap;

import java.lang.reflect.Constructor;

public class SubmitView extends ScrollView {
	private Context context;
	private AOList source;
	private AOMap ctlMap;
	private TableLayout tblLayout;
    private SubmitItem.SubmitItemDelegate itemDelegate=null;
	
	
	public void setSource(AOList source) {
		this.source = source;
	}

    public void setItemDelegate(SubmitItem.SubmitItemDelegate itemDelegate) {
        this.itemDelegate = itemDelegate;
    }

    public SubmitView(Context context) {
		super(context,null);
	}

	
	public SubmitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		
		ctlMap=new AOMap();
		tblLayout=new TableLayout(this.context);
		tblLayout.setColumnStretchable(1, true);
		this.addView(tblLayout);
	}
	
	public void reLoad()
	{
		if(source!=null)
		{
			ctlMap.clear();
			tblLayout.removeAllViews();
			for(int i=0;i<source.size();i++)
			{
				AOMap map=new AOMap();
				map.setItemByHashMap(source.getItem(i));
				
				
				SubmitItem itemObj=getItemObj(map);
				TableRow rowLayout=itemObj.buildItem(context, map);
				ctlMap.put(map.getValue(SubmitItem.ItemKey.name.toString()).toString(), itemObj);
				if(map.containsKey(SubmitItem.ItemKey.display.toString()))
					if("false".equals(map.getValue(SubmitItem.ItemKey.display.toString())))continue;
				tblLayout.addView(rowLayout);
			}
			
		}
		
	}
	
	private SubmitItem getItemObj(AOMap map)
	{
		String type= SubmitItem.ItemValueType.textBox.toString();
		if(map.containsKey(SubmitItem.ItemKey.type.toString()))
		{
			type=map.getValue(SubmitItem.ItemKey.type.toString()).toString();
		}
		
		Class<SubmitItem> objclass=null;
		SubmitItem itemobj=null;
		try{
			objclass=(Class<SubmitItem>)Class.forName("cn.aoandroid.control.submititem.SubmitItem2"+type.substring(0,1).toUpperCase()+type.substring(1));
			Constructor constructor = objclass.getConstructor(); 
			itemobj=(SubmitItem)constructor.newInstance();
		}catch(Exception e)
		{
			try {
				objclass=(Class<SubmitItem>)Class.forName("cn.aoandroid.control.submititem.SubmitItem2TextBox");
				Constructor constructor = objclass.getConstructor(); 
				itemobj=(SubmitItem)constructor.newInstance();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}	
		return itemobj;
	}
	
	
	public static void buildData(AOList para,AOList data ,int index)
	{
		if(data==null)return;
		if(index>data.size())return;
		AOMap mapData=new AOMap();
		mapData.setItemByHashMap(data.getItem(index));
		for(int i=0;i<para.size();i++)
		{
			for(int j=0;j<mapData.size();j++)
			{
				if(para.getValue(i, SubmitItem.ItemKey.name.toString()).equals(mapData.getKey(j)))
				{
					para.put(i, SubmitItem.ItemKey.value.toString(), mapData.getValue(j));
					mapData.remove(j);
					continue;
				}
			}
		}
		
	}
	
	public boolean isChanged() {
		AOMap rsMap=new AOMap();
		boolean isChanged=false;
		for(int i=0;i<ctlMap.size();i++)
		{
			SubmitItem item=(SubmitItem) ctlMap.getValue(i);
			if(item.isChanged())isChanged=true;
		}
		return isChanged;
	}
	
	
	public AOMap getResult()
	{
		AOMap rsMap=new AOMap();
		for(int i=0;i<ctlMap.size();i++)
		{
			SubmitItem item=(SubmitItem) ctlMap.getValue(i);
			rsMap.put(item.getName(), item.getValue());
		}
		return rsMap;
	}
	
	public AOMap getResult(boolean isChanged)
	{
		AOMap rsMap=new AOMap();
		for(int i=0;i<ctlMap.size();i++)
		{
			SubmitItem item=(SubmitItem) ctlMap.getValue(i);
			if(item.isChanged()!=isChanged)continue;
			rsMap.put(item.getName(), item.getValue());
		}
		return rsMap;
	}
	
	
}
