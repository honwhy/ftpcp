package com.honey.ftpcp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
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
		return new DefaultPooledObject<FTPConnection>(wrapperConnection);
	}

	public void destroyObject(PooledObject<FTPConnection> p) throws Exception {
		FTPConnection conn = p.getObject();
		if(conn != null) {
			FTPClient client = conn.unwrap(FTPClient.class);
			if(client != null) {
				client.logout();
				client.disconnect();
			}
		}
		
	}

	public boolean validateObject(PooledObject<FTPConnection> p) {
		FTPConnection conn = p.getObject();
		try {
			if(conn != null) {
				FTPClient client = conn.unwrap(FTPClient.class);
				if(client != null) {
					int reply = client.noop();
					return FTPReply.isPositiveCompletion(reply);
				}
			}
		} catch (FTPException e) {
			//swallow
		} catch (IOException e) {
			//swallow
		}
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
