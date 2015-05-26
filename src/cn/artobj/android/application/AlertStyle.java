package cn.artobj.android.application;

import cn.artobj.aoandroid.R;

public enum AlertStyle {
	info("info", "提示", R.drawable.info), check("check", "校验",
			R.drawable.check), warn("warn", "警告", R.drawable.warn), error(
			"error", "错误", R.drawable.error);

	String code;
	String title;
	int ico;

	public static AlertStyle fromCode(String code)
	{
		for (AlertStyle config : AlertStyle.values())
		{
			if (config.code.equals(code))
			{
				return config;
			}
		}
		throw new IllegalArgumentException(code);
	}

	private AlertStyle(String code, String title, int ico)
	{
		this.code = code;
		this.title = title;
		this.ico = ico;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getTitle()
	{
		return title;
	}

	public int getIco()
	{
		return ico;
	}
}
