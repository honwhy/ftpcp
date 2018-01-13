package com.honey.ftpcp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;

public class PoolableFTPClient extends FTPClient implements AutoCloseable {
	
	/** The pool to which I should return. */
    private final ObjectPool<PoolableFTPClient> _pool;
    
    private FTPClient _ftpClient;
    PoolableFTPClient(final FTPClient ftpClient, final ObjectPool<PoolableFTPClient> pool) {
    	this._ftpClient = ftpClient;
    	this._pool = pool;
    }
    
    
    public FTPClient getFtpClient() {
    	return _ftpClient;
    }
    
    public void close() {
    	
    }
}
