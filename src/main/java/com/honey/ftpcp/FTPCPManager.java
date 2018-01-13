package com.honey.ftpcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.pool2.ObjectPool;

public class FTPCPManager extends PoolProperties implements FTPManager {


	public FTPCPManager() {
		super();
	}
	
	protected FTPManager ftpManager = null;
	private ObjectPool connectionPool;
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
		PoolingFTPManager newManager = new PoolingFTPManager(ftpClientFactory, this);
		connectionPool = newManager.getPool();
		return newManager;
	}
	
	//TODO 用简单工厂重构
	private IFTPClientFactory createFTPClientFactory() {
		if(url == null || url.trim().length() == 0) {
			throw new IllegalArgumentException("url can not be null or empty");
		}
		
		if(proxyHost != null)
		connectionProperties.put("proxyHost", proxyHost);
		connectionProperties.put("proxyPort", proxyPort);
		if(proxyUser != null)
		connectionProperties.put("proxyUser", proxyUser);
		connectionProperties.put("encoding", encoding);
		connectionProperties.put("keepAliveTimeout", keepAliveTimeout);
		connectionProperties.put("controlKeepAliveReplyTimeout", controlKeepAliveReplyTimeout);
		if(serverTimeZoneId != null)
		connectionProperties.put("serverTimeZoneId", serverTimeZoneId);
		connectionProperties.put("bufferSize", bufferSize);
		connectionProperties.put("connectTimeout", connectTimeout);
		//parse url
		try {
			URL aurl = new URL(url);
			if("ftp".equals(aurl.getProtocol())) {
				if(aurl.getHost()!=null) {
					FTPClientFactory factory = null;
					if(username != null && password != null) {
						factory = new FTPClientFactory(aurl.getHost(),aurl.getPort(),username, password,connectionProperties);
					} else {
						factory = new FTPClientFactory(aurl.getHost(),aurl.getPort(),connectionProperties);
					}
					return factory; 
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
    public synchronized int getNumActive() {
        if (connectionPool != null) {
            return connectionPool.getNumActive();
        } else {
            return 0;
        }
    }
    
    public synchronized int getNumIdle() {
        if (connectionPool != null) {
            return connectionPool.getNumIdle();
        } else {
            return 0;
        }
    }
    
	protected String url;
	
	public synchronized void setUrl(final String url) {
		this.url = url;
	}
	protected String username;
	protected String password;
	protected String proxyHost = null;
    protected int proxyPort = 80;
    protected String proxyUser = null;
    protected String proxyPassword = null;
    protected String encoding = StandardCharsets.UTF_8.name();
	protected long keepAliveTimeout = -1;
    protected int controlKeepAliveReplyTimeout = -1;
    protected String serverTimeZoneId = null;
    protected int bufferSize = -1;
    protected int connectTimeout = -1;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public long getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(long keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	public int getControlKeepAliveReplyTimeout() {
		return controlKeepAliveReplyTimeout;
	}

	public void setControlKeepAliveReplyTimeout(int controlKeepAliveReplyTimeout) {
		this.controlKeepAliveReplyTimeout = controlKeepAliveReplyTimeout;
	}

	public String getServerTimeZoneId() {
		return serverTimeZoneId;
	}

	public void setServerTimeZoneId(String serverTimeZoneId) {
		this.serverTimeZoneId = serverTimeZoneId;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getUrl() {
		return url;
	}
    
}
