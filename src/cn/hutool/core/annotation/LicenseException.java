package cn.hutool.core.annotation;


public class LicenseException extends Exception {
	private static final long serialVersionUID = 1L;

    private String msg;

    public LicenseException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public LicenseException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
