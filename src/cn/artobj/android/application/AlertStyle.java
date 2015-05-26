package cn.artobj.android.application;

public enum AlertStyle {
	info("info", "提示"), check("check", "校验"), warn("warn", "警告"), error(
			"error", "错误");

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

	private AlertStyle(String code, String title)
	{
		this.code = code;
		this.title = title;
		this.ico =  AppDefault.getRID(AppDefault.RTYPE.Drawable, title);
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
