package com.honey.ftpcp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class FTPClientWrapperConnection implements FTPConnection {

	private final FTPClient ftpClient;
	private final GenericObjectPool<FTPConnection> pool;
	private volatile boolean _closed = false;
	
	FTPClientWrapperConnection(FTPClient ftpClient, GenericObjectPool<FTPConnection> pool) {
		this.ftpClient = ftpClient;
		this.pool = pool;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws FTPException {
		if(iface.isInstance(this)) {
			return (T)this;
		} else if(iface.isInstance(ftpClient)) {
			return (T)ftpClient;
		}
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws FTPException {
		if(iface.isInstance(this)) {
			return true;
		} else if(iface.isInstance(ftpClient)) {
			return true;
		}
		return false;
	}

	public void close() throws FTPException {
		try {
			if(pool != null && !pool.isClosed()) {
				pool.returnObject(this);
			} else {
				if(ftpClient!=null) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			}
		} catch (Exception e) {
			//swallow everything
		} finally {
			_closed = true;
		}
	}

	public boolean isClosed() throws FTPException {
		return _closed;
	}


}
