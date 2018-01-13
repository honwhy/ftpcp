package com.honey.ftpcp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;

public class DelegatingFTPClient extends FTPClient implements FTPConnection {

	protected FTPClient _client = null;
	protected ObjectPool<DelegatingFTPClient> _pool = null;
	protected boolean _closed = false;
	
	DelegatingFTPClient(FTPClient client) {
		if(client == null) {
		}
		this._client = client;
	}
	
	public void close() {
		if(_closed) {
			return;
		}
		synchronized(this) {
			if(_closed || _client == null) {
				return ;
			}
			try {
				_client.disconnect();
				_client.logout();
			} catch(Exception e) {
				//swallow everything
			} finally {
				_closed = true;
			}
		}
		
	}
	public FTPClient getDelegate() {
		return _client;
	}
	public FTPClient getInnermostDelegate() {
		FTPClient c = _client;
        while(c != null && c instanceof DelegatingFTPClient) {
            c = ((DelegatingFTPClient)c).getDelegate();
            if(this == c) {
                return null;
            }
        }
        return c;
    }

	public <T> T unwrap(Class<T> iface) throws FTPException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws FTPException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClosed() throws FTPException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getSchema() throws FTPException {
		// TODO Auto-generated method stub
		return null;
	}
}
