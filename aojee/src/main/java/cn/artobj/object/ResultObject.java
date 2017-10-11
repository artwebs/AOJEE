package cn.artobj.object;

public class ResultObject extends IObject {
	private String code;
	private String message;
	private AOMap args;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public AOMap getArgs() {
		return args;
	}
	public void setArgs(AOMap args) {
		this.args = args;
	}
	
	
}
