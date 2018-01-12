package com.honey.ftpcp;

public class FTPException extends java.lang.Exception {


	public FTPException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public FTPException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
	
	public FTPException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
	
	private int errorCode;
	
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	private static final long serialVersionUID = 6461659829471226431L;
}
