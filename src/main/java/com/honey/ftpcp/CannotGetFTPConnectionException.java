package com.honey.ftpcp;

import com.honey.ftpcp.FTPException;

public class CannotGetFTPConnectionException extends FTPException {

	private static final long serialVersionUID = 8864994591970103842L;

	public CannotGetFTPConnectionException(String message, int errorCode) {
		super(message, errorCode);
	}

}
