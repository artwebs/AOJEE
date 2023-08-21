package cn.artobj.android.app;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by rsmac on 15/9/16.
 */
public class AOLog {
    private static Boolean isDebug =null;
    private static Pattern pattern = null ;
    private static String debugReg = null ;
    public static void log(Object tag,String obj){
        if(isDebug == null){
            isDebug = (Boolean)AppDefault.meta("debug",true);
        }
        if(debugReg == null){
            debugReg = (String)AppDefault.meta("debugReg",".+");
            pattern = Pattern.compile(debugReg);
        }
        if(Boolean.valueOf(isDebug)){
            if(pattern.matcher(tag.getClass().toString()).matches()){
                Log.w(tag.getClass().toString(), obj);
            }
        }
    }
}
