package com.honey.ftpcp;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public class DelegatingFTPClientTest {

	public void testAPI() throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		DelegatingFTPClient delegate = new DelegatingFTPClient(ftpClient);
		delegate.connect("");
	}
}
