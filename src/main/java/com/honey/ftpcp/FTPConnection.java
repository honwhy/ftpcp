package com.honey.ftpcp;

/**
 * 
 * Description: a connection to interact with server
 *  
 * @date: 2018年1月12日 下午6:10:33
 * @author honwhy.wang
 */
public interface FTPConnection<T> extends Wrapper, AutoCloseable {

	T getConnection();
	
	void close() throws FTPException;
	
	boolean isClosed() throws FTPException;
	
	String getSchema() throws FTPException;
}
