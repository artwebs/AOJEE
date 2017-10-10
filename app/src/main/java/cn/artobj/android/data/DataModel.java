package cn.artobj.android.data;

import android.content.ContentValues;
import cn.artobj.android.app.AppDefault;
import cn.artobj.object.AOList;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rsmac on 15/9/16.
 */
public abstract class DataModel {
    private Object[] values;
    private String table="";
    private String field="";
    private String where="";
    private String group="";
    private String order="";
    private String limit ="";


    private DataBase db;

    public DataModel(){
        this.db= AppDefault.getInstance().buildDataBase();
    }

    public abstract String[] info();

    public String create(){
        String sql="CREATE TABLE IF NOT EXISTS "+this.getTableName()+"(";
        String[] info=info();
        for(int i=0;i<info.length;i++){
            if(i!=0){
                sql+=",";
            }
            sql+=info[i];
        }
        sql+=");";
        return sql;
    }

    public String getTableName(){
        String rs="";
        String cStr= this.getClass().toString();
        cStr=cStr.substring(cStr.lastIndexOf(".")+1);
        cStr=cStr.substring(0,cStr.indexOf("Model"));
        Pattern p = Pattern.compile("([A-Z])");
        Matcher m = p.matcher(cStr);
        while (m.find()) {
            cStr=cStr.replace(m.group(1),"_"+m.group(1).toLowerCase());
        }
        rs = cStr.substring(1);
        return AppDefault.getAppName()+"_"+rs;
    }

    public DataModel field(String... args){
        this.field = "";
        for (String arg : args) {
            if(this.field !=""){
                this.field+=",";
            }
            this.field += arg;
        }
        return this;
    }

    public DataModel values(Object... args){
        this.values=args;
        return this;
    }

    public DataModel table(String table){
        this.table=table;
        return this;
    }

    public DataModel where(String where){
        this.where=where;
        return this;
    }

    public DataModel where(int id){
        this.where="id = "+id;
        return this;
    }

    public DataModel order(String order){
        this.order=order;
        return this;
    }

    public DataModel group(String group){
        this.group=group;
        return this;
    }

    public DataModel limit(String limit){
        this.limit=limit;
        return this;
    }

    public DataModel page(int page,int pageSize){
        this.limit = pageSize+" offset "+(page-1)*pageSize;
        return this;
    }

    public boolean insert(){
        ContentValues vObj=new ContentValues();
        if(values!=null){
            if(values.length%2!=0)
                return false;
            for(int i=0;i<values.length;i=i+2){
                vObj.put(values[i].toString(),values[i+1].toString());
            }
        }
        return this.db.insert(table==""?getTableName():table,null,vObj);
    }

    public AOList select(){
        return select(new String[]{});
    }

    public AOList select(String[] args){
        String sql="select ";
        sql += field ==""?"*":field;
        sql += " from ";
        sql += table ==""?getTableName():table;
        if(where!=""){
            sql += " where ";
            sql += where;
        }
        if(group !=""){
            sql += " group by ";
            sql += group;
        }
        if(order!=""){
            sql += " order by ";
            sql += order;
            if (!((order.contains("desc")||order.contains("asc")))){
                sql += " asc ";
            }
        }
        if(limit !=""){
            sql += " limit ";
            sql += limit;
        }
        return this.db.query(sql,args);
    }

    public boolean update(){
        return this.update(new String[]{});
    }

    public boolean update(String[] whereArgs){
        ContentValues vObj=new ContentValues();
        if(values==null){
            if(values.length%2!=0)
                return false;
            for(int i=0;i<values.length;i=i+2){
                vObj.put(values[i].toString(),values[i+1].toString());
            }
        }
        return this.db.update(table==""?getTableName():table,vObj,where,whereArgs);
    }

    public boolean delete(){
        return delete(new String[]{});
    }

    public boolean delete(String[] args){
        return this.db.delete(table == "" ? getTableName() : table, where, args);
    }


}
