package com.honey.ftpcp;


import org.apache.commons.net.ftp.FTPClient;

public interface IFTPClientFactory {
	
	FTPClient getFTPClient() throws FTPException ;
	
}
