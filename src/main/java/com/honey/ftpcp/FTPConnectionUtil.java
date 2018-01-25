package com.honey.ftpcp;

import com.honey.ftpcp.FTPConnection;
import com.honey.ftpcp.FTPException;
import com.honey.ftpcp.FTPManager;

public class FTPConnectionUtil {

	public static FTPConnection getConnection(FTPManager ftpManager) throws FTPException {
		if(ftpManager != null) {
			return ftpManager.getFTPConnection();
		}
		throw new CannotGetFTPConnectionException("",-1);
	}
	public static void releaseConnection(FTPConnection conn) {
		if(conn == null) {
			return;
		}
		try {
			conn.close();
		} catch (FTPException e) {
			//swallow
		}
	}
}
