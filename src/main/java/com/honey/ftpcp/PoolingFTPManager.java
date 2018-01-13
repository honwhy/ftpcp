package com.honey.ftpcp;

import java.util.NoSuchElementException;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class PoolingFTPManager implements FTPManager {

	private GenericObjectPool<FTPConnection> _pool;
	private PoolableConnectionFactory _connectionFactory;
	
	PoolingFTPManager(IFTPClientFactory clientFactory, PoolProperties poolProperties) {
		//create object factory
		_connectionFactory = new PoolableConnectionFactory(clientFactory);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(poolProperties.getMaxTotal());
		config.setMaxIdle(poolProperties.getMaxIdle());
		config.setMinIdle(poolProperties.getMinIdle());
		config.setMaxWaitMillis(poolProperties.getMaxWait());
		config.setTestOnBorrow(poolProperties.getTestOnBorrow());
		config.setTestOnReturn(poolProperties.getTestOnReturn());
		config.setTestWhileIdle(poolProperties.getTestWhileIdle());
		//eviction
		config.setTimeBetweenEvictionRunsMillis(poolProperties.getTimeBetweenEvictionRunsMillis());
		config.setNumTestsPerEvictionRun(poolProperties.getNumTestsPerEvictionRun());
		config.setMinEvictableIdleTimeMillis(poolProperties.getMinEvictableIdleTimeMillis());
		config.setSoftMinEvictableIdleTimeMillis(poolProperties.getSoftMinEvictableIdleTimeMillis());
		
		_pool = new GenericObjectPool<FTPConnection>(_connectionFactory, config);
		_connectionFactory.setPool(_pool);//反向引用
		if(poolProperties.getInitialSize() > 0) {
			int count = poolProperties.getInitialSize();
			while(count > 0) {
				try {
					_pool.addObject();
				} catch (Exception e) {
					//
				}
				count--;
			}
		}
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
	public GenericObjectPool<FTPConnection> getPool() {
		return _pool;
	}
	public void set_pool(GenericObjectPool<FTPConnection> pool) {
		this._pool = pool;
	}

    

}
