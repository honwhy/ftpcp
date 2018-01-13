package com.honey.ftpcp;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class URLTest {

	@Test
	public void testParse1() throws MalformedURLException {
		URL url = new URL("ftp://127.0.0.1");
		assertTrue("fail parse protocol", "ftp".equals(url.getProtocol()));
		assertTrue("fail parse host", "127.0.0.1".equals(url.getHost()));
	}
	
	@Test
	public void testParse2() throws MalformedURLException {
		URL url = new URL("ftp://username:password@127.0.0.1");
		assertTrue("fail parse protocol", "ftp".equals(url.getProtocol()));
		System.out.println("url " + url.getHost());
		System.out.println(url.getAuthority());
		System.out.println(url.getUserInfo());
		
		//assertTrue("fail parse host", "127.0.0.1".equals(url.getHost()));
		
	}
	@Test
	public void testParse3() throws MalformedURLException {
		URL url = new URL("http://username:password@127.0.0.1");
		assertTrue("fail parse protocol", "ftp".equals(url.getProtocol()));
		System.out.println("url " + url.getHost());
		System.out.println(url.getAuthority());
		System.out.println(url.getUserInfo());
		
		//assertTrue("fail parse host", "127.0.0.1".equals(url.getHost()));
		
	}
	public static void main(String[] args) throws Exception {

        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
                           + "/index.html?name=networking#DOWNLOADING");

        System.out.println("protocol = " + aURL.getProtocol());
        System.out.println("authority = " + aURL.getAuthority());
        System.out.println("host = " + aURL.getHost());
        System.out.println("port = " + aURL.getPort());
        System.out.println("path = " + aURL.getPath());
        System.out.println("query = " + aURL.getQuery());
        System.out.println("filename = " + aURL.getFile());
        System.out.println("ref = " + aURL.getRef());
    }	
}
