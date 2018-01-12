package com.honey.ftpcp;

public interface Wrapper {

	<T> T unwrap(java.lang.Class<T> iface) throws FTPException;
	
	boolean isWrapperFor(java.lang.Class<?> iface) throws FTPException;
}
