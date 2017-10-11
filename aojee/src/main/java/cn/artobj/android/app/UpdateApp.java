package cn.artobj.android.app;

import cn.artobj.android.utils.FileUtils;
import cn.artobj.utils.HttpDownloader;
import cn.artobj.utils.Utils;
import cn.artobj.json.JSONObject;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class UpdateApp {
	private final static String tag="UpdateApp";
	private ProgressDialog downPd;
	private FileUtils fileUtils;
	private static UpdateApp obj;
	private Version version;
	private ContextWrapper activity;
	private static String path="artintall";
	public static enum DATAType
	{
		JSON,
		XML
	};
	
	private UpdateApp()
	{
		
	}
	
	public static void install(ContextWrapper activity)
	{
		Log.d(tag,"UpdateApp start");
		install(activity,getCtlContent());
	}
	
	public static void install(final ContextWrapper activity,final String url)
	{
		install(activity, url,DATAType.JSON);
	}
	
	public static void install(final ContextWrapper activity,final String url,final DATAType type)
	{
		obj=new UpdateApp();
		new Thread(new Runnable(){

			@Override
			public void run() {
				Version localVersion=getLocalVersion();
				String content="";
				content=getCtlContent(url);
				if(content=="")return;
				Version ctlVersion=null;
				switch (type) {
				case JSON:
					ctlVersion=getControlVersionWithJson(content);
					break;
				case XML:
					ctlVersion=getControlVersionWithXml(content);
					break;
				default:
					
					return;
				}
				if(ctlVersion==null)return;
				
				obj.version=ctlVersion;
				obj.activity=activity;
				obj.fileUtils=new FileUtils(path);
				if(obj.fileUtils.isFileExist(obj.version.getAppName()+".apk"))obj.fileUtils.deleteSDFile(obj.version.getAppName()+".apk");
				Log.d(tag, "localVersion="+localVersion.getVersion());
				Log.d(tag, "ctlVersion="+ctlVersion.getVersion());
				if(localVersion.getVersion()<ctlVersion.getVersion())
				{
					obj.mHandler.sendEmptyMessage(0);
				}
			}}).start();
	}
	
	public static void installWithString(final ContextWrapper activity,final String content)
	{
//		Log.d(tag,"haveInternet="+NetworkProber.haveInternet());
//		if(!NetworkProber.haveInternet())return;
		obj=new UpdateApp();
		new Thread(new Runnable(){

			@Override
			public void run() {
				Version localVersion=getLocalVersion();
				Version ctlVersion=getControlVersionWithXml(content);
				if(ctlVersion==null)return;
				
				obj.version=ctlVersion;
				obj.activity=activity;
				obj.fileUtils=new FileUtils(path);
				if(obj.fileUtils.isFileExist(obj.version.getAppName()+".apk"))obj.fileUtils.deleteSDFile(obj.version.getAppName()+".apk");
				Log.d(tag, "localVersion="+localVersion.getVersion());
				Log.d(tag, "ctlVersion="+ctlVersion.getVersion());
				if(localVersion.getVersion()<ctlVersion.getVersion())
				{
					obj.mHandler.sendEmptyMessage(0);
				}
			}}).start();
		
		
	}
	
	public static void installWithObject(final ContextWrapper activity,final Version ctlVersion)
	{
		obj=new UpdateApp();
		new Thread(new Runnable(){

			@Override
			public void run() {
				Version localVersion=getLocalVersion();
				if(ctlVersion==null)return;
				obj.version=ctlVersion;
				obj.activity=activity;
				obj.fileUtils=new FileUtils(path);
				if(obj.fileUtils.isFileExist(obj.version.getAppName()+".apk"))obj.fileUtils.deleteSDFile(obj.version.getAppName()+".apk");
				Log.d(tag, "localVersion="+localVersion.getVersion());
				Log.d(tag, "ctlVersion="+ctlVersion.getVersion());
				if(localVersion.getVersion()<ctlVersion.getVersion())
				{
					obj.mHandler.sendEmptyMessage(0);
				}
			}}).start();
	}
	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Dialog dialog=new AlertDialog.Builder(obj.activity)
			 .setIcon(R.drawable.ic_dialog_alert)
			 .setTitle("软件升级？")
			 .setMessage("您确定进行软件升级吗？")
			 .setPositiveButton("升级", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if(!android.os.Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED))
					{
						Toast.makeText(AppDefault.getAppContext(), "设备无SDCard，无法完成自动升级",Toast.LENGTH_LONG).show();
						return;
					}
					downApk();
				}
				 
			 })
			 .setNegativeButton("以后再说", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					
				}
				 
			 }).create();
			dialog.show();
			
		}
	};
	
	private void downApk()
	{
		downPd=new ProgressDialog(activity);
		downPd.setTitle("正在更新...");
		downPd.setMessage("下载更新文件可能需要几分钟，请稍后...");
		downPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		downPd.setMax(this.version.getApkSize());
		downPd.show();
		new Thread(new Runnable(){

			@Override
			public void run() {	
				HttpDownloader obj=new HttpDownloader();
				obj.downFile(version.getUpdateUrl(),path, version.getAppName()+".apk", downHandler);
			}}).start();
	}
	
	public static Version getLocalVersion()
    {
    	Version obj=new Version();
    	obj.setAppName(AppDefault.getAppName());
    	obj.setVersion(Long.valueOf(AppDefault.getPKG().versionCode));
    	obj.setUpdateUrl("");
    	return obj;
    }
	
	
	
	public static Version getControlVersionWithXml(String content)
	{
		Version ctlVersion=new Version();
		try{
			ctlVersion.setAppName(Utils.getMarkString(content, "<appName>", "</appName>"));
	    	ctlVersion.setUpdateUrl(Utils.getMarkString(content, "<updateUrl>", "</updateUrl>"));
	    	ctlVersion.setVersion(Long.valueOf(Utils.getMarkString(content, "<version>", "</version>")));
	    	ctlVersion.setApkSize(Integer.parseInt(Utils.getMarkString(content, "<apkSize>", "</apkSize>")));
		}catch(Exception e)
		{
			ctlVersion=null;
		} finally
		{}
    	return ctlVersion;
	}
	
	public static Version getControlVersionWithJson(String content)
	{
		Version ctlVersion=new Version();
		try {

			JSONObject rootObj=new JSONObject(content);
			JSONObject updateData=rootObj.getJSONObject("result");
			
			ctlVersion.setAppName(updateData.getString("appName"));
	    	ctlVersion.setUpdateUrl(updateData.getString("updateUrl"));
	    	ctlVersion.setVersion(Long.valueOf(updateData.getString("version")));
	    	ctlVersion.setApkSize(Integer.parseInt(updateData.getString("apkSize")));
		} catch (Exception e) {
			// TODO: handle exception
		}finally{}
		
		return ctlVersion;
	}
    
    public static String getCtlContent()
    {
    	Version localVersion=getLocalVersion();
    	String url="http://www.artwebs.com.cn/appserver.php?appName=%s";
    	return getCtlContent(String.format(url, Utils.UrlEncode(localVersion.getAppName(), "utf-8")));
    }
    
    public static String getCtlContent(String url)
    {
    	String rs="";
    	try{
	    	HttpDownloader httpobj=new HttpDownloader();
	    	rs=httpobj.download(url);
    	}catch(Exception e){
    		
    	}
    	finally{}
		return rs;
    }
	
	private Handler downHandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted())
		      {
		        switch (msg.what)
		        {
		          case 1:
		            downPd.incrementProgressBy(Integer.valueOf(msg.obj.toString()));
		            break;
		          case 2:
		        	downPd.dismiss();
		            Toast.makeText(activity, "文件下载完成", Toast.LENGTH_LONG).show();		            
					Log.i(tag,fileUtils.getSDPATH()+version.getAppName()+".apk");
					fileUtils.installApk(activity, fileUtils.getSDPATH()+version.getAppName()+".apk");
		            break;

			      case 3:
			    	  downPd.setMax(Integer.valueOf(msg.obj.toString()));
			    	  break;
		          case -1:
		            String error = msg.getData().getString("error");
		            Toast.makeText(AppDefault.getAppContext(), error,Toast.LENGTH_LONG).show();
		         
		            break;
		        }
		      }
		      super.handleMessage(msg);
		}
		
	};
}
