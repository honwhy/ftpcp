package com.honey.ftpcp;

public interface FTPManager extends AutoCloseable {

	FTPConnection getFTPConnection() throws FTPException ;
}
