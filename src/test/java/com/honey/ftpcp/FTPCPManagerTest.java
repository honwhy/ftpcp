package com.honey.ftpcp;

import static org.junit.Assert.*;

import org.junit.Test;

public class FTPCPManagerTest {

	@Test
	public void test1() throws Exception {
		FTPCPManager manager = new FTPCPManager();
		manager.setUrl("ftp://127.0.0.1");
		manager.setUsername("sa");
		manager.setPassword("sa");
		manager.setInitialSize(2);
		manager.setKeepAliveTimeout(1 * 60);
		
		FTPConnection conn = manager.getFTPConnection();
		assertTrue(manager.getNumActive() == 1);
		assertTrue(manager.getNumIdle() == 1);
		conn.close();
		assertTrue(manager.getNumActive() == 0);
		assertTrue(manager.getNumIdle() == 2);
		manager.close();
	}
}
