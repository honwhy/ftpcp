package com.honey.ftpcp.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.honey.ftpcp.FTPConnection;
import com.honey.ftpcp.FTPException;

public class FTPConnectionImpl implements FTPConnection {

	private final FTPClient _client;
	private volatile boolean _closed = false;
	
	public FTPConnectionImpl(FTPClient client) {
		super();
		this._client = client;
	}
	
	public <T> T unwrap(Class<T> iface) throws FTPException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws FTPException {
		// TODO Auto-generated method stub
		return false;
	}

	public void close() throws FTPException {
		// TODO Auto-generated method stub
		
	}

	public boolean isClosed() throws FTPException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retrieveFile(String remote, OutputStream local) throws IOException {
		return _client.retrieveFile(remote, local);
	}

}
