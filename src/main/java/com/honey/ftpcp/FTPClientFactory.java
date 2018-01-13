package com.honey.ftpcp;

import java.io.IOException;
import java.net.SocketException;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

public class FTPClientFactory implements IFTPClientFactory {

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final Properties connectionProperties;
	
	public FTPClientFactory(String host, int port, Properties connectionProperties) {
		this(host,port,null,null,connectionProperties);
	}
	
	public FTPClientFactory(String host, int port, String username, String password, Properties connectionProperties) {
		this.host = host;
		if(port != -1) {
			this.port = port;
		} else {
			this.port = FTP.DEFAULT_PORT;
		}
		this.username = username;
		this.password = password;
		this.connectionProperties = connectionProperties;
	}
	public FTPClient getFTPClient() throws FTPException {
		
		FTPClient client = configure();
		try {
			client.connect(host, port);
			int reply = client.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				throw new FTPException("failed to connect server" + host + ":" + port, reply);
			}
			if(username != null && password != null) {
				client.login(username, password);
				reply = client.getReplyCode();
				if(!FTPReply.isPositiveCompletion(reply)) {
					client.logout();
					client.disconnect();
					throw new FTPException("failed to login server" + host + ":" + port, reply);
				}
			}
			//TODO set keepAlive=true, fileType=FTP.BINARY_FILE_TYPE
		} catch (SocketException e) {
			throw new FTPException("socket exception: " + e.getMessage(), e, -1);
		} catch (IOException e) {
			throw new FTPException("io exception: " + e.getMessage(), e, -1);
		}
		return client;
	}
	
	private FTPClient configure() {
		FTPClientConfig config = new FTPClientConfig();
		if(connectionProperties.getProperty("serverTimeZoneId") != null) {
			config.setServerTimeZoneId(connectionProperties.getProperty("serverTimeZoneId"));
		}
		
		FTPClient client = new FTPClient();
		client.configure(config);
		String bufferSize = (String) connectionProperties.getProperty("bufferSize");
		if(bufferSize != null) {
			client.setBufferSize(Integer.parseInt(bufferSize));
		}
		String encoding = (String) connectionProperties.getProperty("encoding");
		if(encoding != null) {
			client.setControlEncoding(encoding);
		}
		
		String keepAliveTimeout = (String) connectionProperties.getProperty("keepAliveTimeout");
		if(keepAliveTimeout != null) {
			client.setControlKeepAliveTimeout(Long.parseLong(keepAliveTimeout));
		}
		String controlKeepAliveReplyTimeout = (String) connectionProperties.getProperty("controlKeepAliveReplyTimeout");
		if(controlKeepAliveReplyTimeout != null) {
			client.setControlKeepAliveReplyTimeout(Integer.parseInt(controlKeepAliveReplyTimeout));
		}
		
		String connectTimeout = (String) connectionProperties.getProperty("connectTimeout");
		if(connectTimeout != null) {
			client.setConnectTimeout(Integer.parseInt(connectTimeout));
		}
		return client;
	}
}
