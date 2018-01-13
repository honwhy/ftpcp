package com.honey.ftpcp;

import java.util.NoSuchElementException;

import org.apache.commons.pool2.impl.GenericObjectPool;

public class PoolingFTPManager implements FTPManager {

	private GenericObjectPool<FTPConnection> _pool;
	private PoolableConnectionFactory _connectionFactory;
	
	PoolingFTPManager(IFTPClientFactory clientFactory) {
		//create object factory
		_connectionFactory = new PoolableConnectionFactory(clientFactory);
		_pool = new GenericObjectPool<FTPConnection>(_connectionFactory);
		
	}
	public synchronized void close() throws Exception {
		if(_pool == null && _pool.isClosed()) {
			return ;
		}
		if(_pool != null) {
			_pool.close();
		}
		
	}

    public FTPConnection getFTPConnection() throws FTPException {
        try {
        	FTPConnection conn = _pool.borrowObject();
            return conn;
        } catch(NoSuchElementException e) {
            throw new FTPException("Cannot get a connection, pool error " + e.getMessage(), e,-1);
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new FTPException("Cannot get a connection, general error", e,-1);
        }
    }


}
