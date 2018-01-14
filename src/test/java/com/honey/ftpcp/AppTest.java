package com.honey.ftpcp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	@Test
	public void testApp() {
		assertTrue(true);
	}
	
	@Test
	public void test1() {
		FTPManager ftpManager = new MockFTPManager();
		FTPConnection ftpConnection = null;
		try {
			ftpConnection = ftpManager.getFTPConnection();
		} catch (FTPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(ftpConnection != null) {
				ftpConnection.close();
			}
			
			ftpManager.close();
		} catch (Exception e) {
			//swallow exception
		}
	}
	
	class MockFTPManager implements FTPManager {

		public void close() throws Exception {
			// TODO Auto-generated method stub
			
		}

		public FTPConnection getFTPConnection() {
			return new MockFTPConnection();
		}
		
	}
	
	class MockFTPConnection implements FTPConnection {
		private volatile boolean _closed = false;
		public <T> T unwrap(Class<T> iface) throws FTPException {
			throw new FTPException("not supported",-1);
		}

		public boolean isWrapperFor(Class<?> iface) throws FTPException {
			throw new FTPException("not supported",-1);
		}

		public void close() throws FTPException {
			if(_closed) {
				return ;
			}
			synchronized (this) {
				if(!_closed) {
					_closed = true;
				}
			}
			
		}

		public boolean isClosed() throws FTPException {
			return _closed;
		}

		public boolean retrieveFile(String remote, OutputStream local) throws IOException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
