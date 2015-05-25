package cn.artobj.object;


import cn.artobj.json.JSONArray;
import cn.artobj.json.JSONException;
import cn.artobj.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AOList extends IObject {
	private ArrayList item=new ArrayList();
	public void setItem(ArrayList item) {
		this.item = item;
	}

	public ArrayList getItem() {
		return item;
	}
	public HashMap getItem(int i) {
		return (HashMap)item.get(i);
	}
	
	public AOMap getMapItem(int i)
	{
		AOMap para=new AOMap();
		para.setItemByHashMap((HashMap)item.get(i));
		return para;
	}

    public AOList appendFromJSONArray(JSONArray array)
    {
        try {
            for (int i = 0; i <array.length() ; i++) {
                boolean isStart=false;
                JSONObject json=array.getJSONObject(i);
                Iterator<?> it = json.keys();
                while(it.hasNext()){
                    String key= (String) it.next();
                    put(isStart,key,json.getString(key));
                    if(!isStart)isStart=true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return this;

    }

	public JSONArray getArray2JSONArray()
	{
		JSONArray array=new JSONArray();
		for(int i=0;i<size();i++)
		{
			array.put(getMapItem2JSONObject(i));
		}
		return array;
	}
	
	public JSONObject getMapItem2JSONObject(int i)
	{
		JSONObject obj=new JSONObject();
		AOMap map=getMapItem(i);
		for(int ni=0;ni<map.size();ni++)
			try {
				obj.put(map.getKey(ni).toString(), map.getValue(ni).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return obj;
		
	}
	
	@SuppressWarnings("unchecked")
	public Object getValue(int i,Object key) {
		Object value=new Object();
		HashMap hm=(HashMap)item.get(i);
		Iterator it= hm.keySet().iterator();
		while(it.hasNext())
		{			
			  Object tempkey=(Object)it.next();
			  if(key.equals(tempkey))value=hm.get(key);		
		 
		}
		return value;
	}
	
	public String getValueString(int i,Object key)
	{
		Object value=getValue(i, key);
		return value==null?"":value.toString();
	}
	
	public void setValue(int i,Object key,Object value){
		HashMap hm=(HashMap)item.get(i);
		Iterator it= hm.keySet().iterator();
		while(it.hasNext())
		{			
			  Object tempkey=(Object)it.next();
			  if(key.equals(tempkey)){
				  hm.remove(tempkey);
				  hm.put(tempkey, value);
			  }
		 
		}
	}

	public ArrayList getItemKeys() {
		ArrayList rs=new ArrayList();
		HashMap hm=(HashMap)item.get(0);
		Iterator it= hm.keySet().iterator();
		while(it.hasNext())
		{			
			Object key=(Object)it.next();
			rs.add(key);
			
		}
		return rs;
	}
	public ArrayList getItemValues(int index) {
		ArrayList rs=new ArrayList();
		HashMap hm=(HashMap)item.get(index);
		Iterator it= hm.keySet().iterator();
		while(it.hasNext())
		{
			
			String key=(String)it.next();
			rs.add(hm.get(key));
			
		}
		return rs;
	}
	
	public void put(HashMap hm)
	{		
		item.add(hm);
	}
	
	@SuppressWarnings("unchecked")
	public void put(int index,Object key,Object value)
	{
		if(index<item.size()){
			HashMap hm=(HashMap)item.get(index);
		    hm.put(key, value);
		}else{
			HashMap hm=new HashMap();
		    hm.put(key, value);
		    item.add(index,hm);
		}

	}
	public void put(Boolean flag,Object key,Object value)
	{
		if(flag&&item.size()>0){
			HashMap hm=(HashMap)item.get(item.size()-1);
		    hm.put(key, value);
		}else{
			HashMap hm=new HashMap();
		    hm.put(key, value);
		    item.add(hm);
		}

	}
	public void put(int index,ArrayList keys,ArrayList values)
	{
		HashMap hm=(HashMap)item.get(index);
		if(keys.size()==values.size())
		{
		    for(int i=0;i<keys.size();i++)
		    {
		    	hm.put(keys.get(i), values.get(i));			
		    }
		    item.add(hm);
		}		
	}

	public int size()
	{ 
		int size=item.size();
		return size;
	}
	public void remove(int index){
		item.remove(index);
	}
	public void remove(int index,Object key){
		HashMap hm=(HashMap)item.get(index);
		hm.remove(key);
	}
	
	public void clear()
	{
		if(item!=null)
		item.clear();		
	}
	public boolean containsKey(Object key)
	{
		boolean Flage=false;
		for(int i=0;i<item.size();i++)
		{
			HashMap hm=(HashMap)item.get(i);
			if(hm.containsKey(key))
			{
				Flage=true;
				break;				
			}	
		}
		return Flage;
	}
	public boolean containsValue(Object value)
	{
		boolean Flage=false;
		for(int i=0;i<item.size();i++)
		{
			HashMap hm=(HashMap)item.get(i);
			if(hm.containsValue(value))
			{
				Flage=true;
				break;				
			}	
		}
		return Flage;
	}

	public int getContainsValueItem(Object value)
	{
		int rsInt=-1;
		for(int i=0;i<item.size();i++)
		{
			HashMap hm=(HashMap)item.get(i);
			if(hm.containsValue(value))
			{
				rsInt=i;
				break;				
			}	
		}
		return rsInt;
	}
	
	public void addend(AOList adder)
	{
		this.item.addAll(adder.getItem());
	}
	
	public void addendTop(AOList adder)
	{
		this.item.addAll(0,adder.getItem());
	}
	
	public AOList clone(){
		AOList rsList=new AOList();
		rsList.setItem((ArrayList)item.clone());
		return rsList;
		
	}
	public static void main(String[] args) {
		
		AOList atp=new AOList();
		atp.put(0,"a", "1");
		atp.put(0,"b", "2");
		atp.put(0,"c", "3");
		atp.put(1,"e", "4");
//		System.out.println(atp.getItem());
//		atp.put(1,"e", "5");
//		System.out.println(atp.getItem().size());
//		System.out.println(atp.getValue(1, "e"));
		System.out.println(atp.clone().getItem());
		

	}

}
