package cn.artobj.android.app;

public class Version {
	private double version;
	private String updateUrl;
	private String appName;
	private int apkSize;
	
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	public String getUpdateUrl() {
		return updateUrl;
	}
	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getApkSize() {
		return apkSize;
	}
	public void setApkSize(int apkSize) {
		this.apkSize = apkSize;
	}

}
