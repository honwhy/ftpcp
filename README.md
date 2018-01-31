# 使用commons-pool管理FTP连接

## 背景

在[封装一个FTP工具类](https://segmentfault.com/a/1190000007461457)文章，已经完成一版对FTP连接的管理，设计了模板方法，为工具类上传和下载文件方法的提供获取对象和释放对象支持。

此番重新造轮子更多地考虑功能复用的角度，支持更多可配置参数，不止是连接池相关的属性；只考虑维护同一个连接请求多个连接对象的情况，将多个不同请求的情况交给外部管理，由外部定制，类似多数据源数据库连接的方式；重新审视模板方法的使用，在不引入模板的方法，设计封装对象池管理功能，以更自然的方式获取对象和释放对象。

## 思路
整体的思路来自`BasicDataSource`，它是`javax.sql.DataSource`的具体实现，实现的是数据库连接池，使用上完全感觉不到对象池的存在，通过`dataSource`获取对象`connection`，释放对象则使用`connection.close()`即可。然而，与`javax.sql.DataSource`和`java.sql.Connection`不同的是，JDK中并没有支持FTP协议的类似的框架；另一个问题则是，项目中已经使用commons-net来建立FTP连接，使用FTPClient等API了，如何将具体实现整合到要新定义的接口中，似乎是本末倒置的。

## 实现
### 整体框架
首先定义整体框架，类似`DataSource`
```
public interface FTPManager extends AutoCloseable {

	FTPConnection getFTPConnection() throws FTPException ;
}
```
定义连接对象，
```
public interface FTPConnection extends Wrapper, AutoCloseable {

	void close() throws FTPException;
	
	boolean isClosed() throws FTPException;
	
	//ftp|ftps|ftp:http -- subprotocol
	//String getSchema() throws FTPException;
}
```
从这个框架出发，获取连接对象使用`ftpManager.getFTPConnection`，释放对象使用`ftpConnection.close`。
### 整理配置属性
引入主角`FTPCPManager`，在`FTPCPManager`定义和连接相关的属性，抽取一个父类`PoolProperties`专门用于配置对象池相关的配置。
```
public class FTPCPManager extends PoolProperties implements FTPManager {
    protected String url;    
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
    protected String localActive = "false";
}
```
类似地，若使用Spring的xml配置，配置`FTPCPManager`或许是这样的，
```
<bean id="ftpCPManager" class="com.honey.ftpcp.FTPCPManager" destroy-method="close">  
    <property name="url" value="ftp://127.0.0.1"/>  
    <property name="username" value="sa"/>  
    <property name="password" value="sa"/>
    <property name="maxTotal" value="100"/>
    <property name="maxIdle" value="8"/> 
    <property name="minIdle" value="0"/>
    <property name="maxWait" value="1000"/>
    <property name="initialSize" value="2"/>
    <property name="testOnBorrow" value="true"/>、
    <property name="testOnReturn" value="false"/>
    <property name="testWhileIdle" value="false"/>
</bean>
```
关于对象池的属性的说明请参考更多网络文章，或者官方文档。
### 获取对象
这是`FTPCPManager`最核心的部分了，入口是`getFTPConnection`方法，
```
public FTPConnection getFTPConnection() throws FTPException {
    return createFTPManager().getFTPConnection();
}
```
```
protected synchronized FTPManager createFTPManager() {
    if(ftpManager != null) {
        return ftpManager;
    }
    //create connection factory
    IFTPClientFactory ftpClientFactory = createFTPClientFactory();
    PoolingFTPManager newManager = new PoolingFTPManager(ftpClientFactory, this);
    connectionPool = newManager.getPool();
    this.ftpManager = newManager;
    return newManager;
}
```
`FTPCPManager`做了一个特殊处理，在内部维护了新的`FTPManager`类型变量，不同的是它带有对象池管理的功能，它存在的意义就是将对象池和对象工厂组合起来，这样的处理方式减轻了`FTPCPManager`的负担，职责更少，只提供重要接口，重要的实现还是交给被代理的成员。（当然，这里也可以有不同的看法）。`createFTPClientFactory`会根据url属性的协议分别创建不同的对象工厂，如`FTPClientFactory`，`FTPSClientFactory`等。

`PoolingFTPManager`的构造方法，需要对象工厂及连接池配置属性两个参数，`FTPCPManager`正好继承扩展了`PoolProperties`类，作为连接池配置参数很合适。所以构造被代理的成员，即`newManager = new PoolingFTPManager(ftpClientFactory, this)`。

构造好`PoolingFTPManager`的实例后，就可以获取`FTPConnection`连接对象了，接下来就是对象池的功能了。整体时序图如下，
![图片描述][1]
对象的获取最终还是对象池与对象工厂的事情。

### 释放对象
为了让`FTPConnection`执行`close`方法的时候能够释放自己，将自己return到对象池，必须对`FTPConnection`做一些封装，连接对象需要记住最初的对象池对象，而对象池需要通过对象工厂来构造，通过这些条件代码的实现思路如下，
在构造`PoolingFTPManager`的同时也针对FTP对象工厂进行了封装，把原来的`IFTPClientFactory`封装成`PoolableConnectionFactory`类型，并且`PoolableConnectionFactory`持有`GenericObjectPool`类型的的对象池变量。在构造完`GenericObjectPool`对象池后，将对象池引用设置到`PoolableConnectionFactory`中。
```
PoolingFTPManager(IFTPClientFactory clientFactory, PoolProperties poolProperties) {
    //create object factory
    _connectionFactory = new PoolableConnectionFactory(clientFactory);
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    // set config
    
    _pool = new GenericObjectPool<FTPConnection>(_connectionFactory, config);
    _connectionFactory.setPool(_pool);//反向引用
}
```
此外，在执行`PoolableConnectionFactory`的`makeObject`方法，对生成的对象做一次封装，传递`PoolableConnectionFactory`持有的对象池给新生成的的对象。
```
public PooledObject<FTPConnection> makeObject() throws Exception {
    FTPClient ftpClient = factory.getFTPClient();
    FTPClientWrapperConnection wrapperConnection = new FTPClientWrapperConnection(ftpClient,pool);
    return new DefaultPooledObject<FTPConnection>(wrapperConnection);
}
```
这个`FTPClientWrapperConnection`类就是关键了。`FTPConnection`执行`close`方法能将自己释放，return到对象池，就是由`FTPClientWrapperConnection`具体实现的。
```
public void close() throws FTPException {
    try {
        if(pool != null && !pool.isClosed()) {
            pool.returnObject(this);
        } else {
            if(ftpClient!=null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    } catch (Exception e) {
        //swallow everything
    } finally {
        _closed = true;
    }
}
```
## 简单测试
用一个测试来表现这个获取和释放对象的功能，
```
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
```
首先`initialSize`设置了对象池初始大小，在构造对象池的时候就调用了两次对象工厂的`makeObject`方法生成两个对象。然后是通过`manager`获取一次对象，此时检测对象池的被借出的对象`manager.getNumActive() == 1`是否成立，检测对象池保留的对象`manager.getNumIdle() == 1`是否成立。接下里是调用连接对象的`close`方法，再次检测比较对象池保留的对象是否`manager.getNumIdle() == 2`。如果以上断言都成立，证明对象的获取和释放使用到了对象池管理而且能够正常运行。

## 总结
至此，使用commons-pool管理FTP连接的功能算基本完成了。与[封装一个FTP工具类](https://segmentfault.com/a/1190000007461457)文章中的FTP工具相比还缺少上传下载等功能的封装，而这些功能将会交给另外的工程来完成。
项目地址：https://github.com/Honwhy/ftpcp


  [1]: https://sfault-image.b0.upaiyun.com/345/349/3453496482-5a71da7265296_articlex