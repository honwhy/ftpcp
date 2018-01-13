package com.honey.ftpcp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class PoolableConnectionFactory implements PooledObjectFactory<FTPConnection> {
	
	private final IFTPClientFactory factory;
	private GenericObjectPool<FTPConnection> pool; //反向引用
	
	public PoolableConnectionFactory(IFTPClientFactory factory) {
		this.factory = factory;
	}

	public PooledObject<FTPConnection> makeObject() throws Exception {
		FTPClient ftpClient = factory.getFTPClient();
		FTPClientWrapperConnection wrapperConnection = new FTPClientWrapperConnection(ftpClient,pool);
		return null;
	}

	public void destroyObject(PooledObject<FTPConnection> p) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean validateObject(PooledObject<FTPConnection> p) {
		// TODO Auto-generated method stub
		return false;
	}

	public void activateObject(PooledObject<FTPConnection> p) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void passivateObject(PooledObject<FTPConnection> p) throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void setPool(GenericObjectPool<FTPConnection> pool) {
		this.pool = pool;
	}
	


}
