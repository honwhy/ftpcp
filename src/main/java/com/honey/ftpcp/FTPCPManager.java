package com.honey.ftpcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class FTPCPManager extends PoolProperties implements FTPManager {


	public FTPCPManager() {
		super();
	}
	
	protected FTPManager ftpManager = null;
    /**
     * 链接FTP相关的配置
     */
    private Properties connectionProperties = new Properties();

	public synchronized void close() throws Exception {
		if(ftpManager!=null) {
			ftpManager.close();
		}
		
	}

	protected synchronized FTPManager createFTPManager() {
		if(ftpManager != null) {
			return ftpManager;
		}
		
		//create connection factory
		IFTPClientFactory ftpClientFactory = createFTPClientFactory();
		if(username != null) {
			connectionProperties.put("username", username);
		}
		if(password != null) {
			connectionProperties.put("password", password);
		}
		return new PoolingFTPManager(ftpClientFactory);
	}
	
	//TODO 用简单工厂重构
	private IFTPClientFactory createFTPClientFactory() {
		if(url == null || url.trim().length() == 0) {
			throw new IllegalArgumentException("url can not be null or empty");
		}
		//parse url
		try {
			URL aurl = new URL(url);
			if("ftp".equals(aurl.getProtocol())) {
				if(aurl.getHost()!=null) {
					return new FTPClientFactory();
				} else {
					// ftp:http://
					url = url.substring(4);
					aurl = new URL(url);
					if("http".equals(aurl.getProtocol())) {
						return new FTPHTTPClientFactory();
					}
					throw new IllegalArgumentException("invalid subprotocol");
				}
			} else if("ftps".equals(aurl.getProtocol())) {
				return new FTPSClientFactory();
			} else {
				throw new IllegalArgumentException("invalid protocol");
			}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("url invalid", e);
		}
	}

	public FTPConnection getFTPConnection() throws FTPException {
		return createFTPManager().getFTPConnection();
	}
	
	protected String url;
	
	public synchronized void setUrl(final String url) {
		this.url = url;
	}
	protected String username;
	protected String password;
}
