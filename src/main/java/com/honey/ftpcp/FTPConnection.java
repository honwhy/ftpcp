package com.honey.ftpcp;

/**
 * 
 * Description: a connection to interact with server
 *  
 * @date: 2018年1月12日 下午6:10:33
 * @author honwhy.wang
 */
public interface FTPConnection extends Wrapper, AutoCloseable {

	void close() throws FTPException;
	
	boolean isClosed() throws FTPException;
	
	//ftp|ftps|ftp:http -- subprotocol
	//String getSchema() throws FTPException;
}
