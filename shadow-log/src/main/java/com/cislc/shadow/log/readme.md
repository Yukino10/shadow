参考文档
https://blog.csdn.net/wateryouyo/article/details/82079209

搭建教程
https://www.cnblogs.com/hollowcabbage/p/9503107.html
1、wget http://mirrors.tuna.tsinghua.edu.cn/apache/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz

source ~/.bashrc


修改java_home  vim /etc/profile



官方文档 http://flume.apache.org/releases/content/1.9.0/FlumeUserGuide.html

1、安装 telnet工具
https://www.jianshu.com/p/9ff2cda44584
2、注意如下配置
a1.sources.r1.bind = 0.0.0.0
3、参考文档
sdk Log 配置
https://www.liangzl.com/get-article-detail-123634.html
4、这两个配置也很重要， 
使用第一个时，可以使用 telnet localhost 44444 的形式；
使用第二个时，可以使用FlumeTest3的形式
a1.sources.r1.type = netcat  
a1.sources.r1.type = avro



-----------------------------教程---------------------------  
flume官方文档 http://flume.apache.org/releases/content/1.9.0/FlumeUserGuide.html

===================日志服务器搭建===================  
1、下载安装包  
wget http://mirrors.tuna.tsinghua.edu.cn/apache/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz  
2、解压安装包  
tar zxf apache-flume-1.9.0-bin.tar.gz  
3、到conf 目录  
cd apache-flume-1.9.0-bin/conf/  
4、新建配置文件  
vim example.conf  
如下：
```properties
# example.conf: A single-node Flume configuration

# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = avro
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 44444

# Describe the sink
#a1.sinks.k1.type = logger
a1.sinks.k1.type = file_roll
a1.sinks.k1.sink.directory = /usr/software/flume/apache-flume-1.9.0-bin/logs
a1.sinks.k1.sink.rollInterval = 0

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```
5、创建配置文件中 a1.sinks.k1.sink.directory 属性对应的文件夹  
6、开放配置文件中 a1.sources.r1.port 属性对应的端口  
firewall-cmd --zone=public --add-port=44444/tcp --permanent  
systemctl restart firewalld  
firewall-cmd --list-ports  
7、配置JAVA_HOME FLUME_HOME 等环境变量  
vim ~/.bashrc  
```javascript
export FLUME_HOME=/usr/software/flume/apache-flume-1.9.0-bin
export FLUME_CONF_DIR=$FLUME_HOME/con
export PATH=$PATH:$FLUME_HOME/bin
```
source ~/.bashrc  
vim /etc/profile  
```javascript
JAVA_HOME=/opt/jdk1.8.0_141
CLASSPATH=$JAVA_HOME/lib/
PATH=$PATH:$JAVA_HOME/bin
export PATH JAVA_HOME CLASSPATH
```
source /etc/profile  
8、在 conf文件夹下 启动 bin 目录下的 flume-ng  
../bin/flume-ng agent --conf conf --conf-file example.conf --name a1 -Dflume.root.logger=INFO,console  
在后台运行执行以下命令  
nohup ../bin/flume-ng agent --conf conf --conf-file example.conf --name a1 -Dflume.root.logger=INFO,console &  
ps -aux|grep java  
暂停命令  
kill -9 进程号  

===================java sdk 使用===================  
1、引入pom依赖  
```xml
<dependency>
    <groupId>org.apache.flume.flume-ng-clients</groupId>
    <artifactId>flume-ng-log4jappender</artifactId>
    <version>1.8.0</version>
</dependency>
```
以上配置默认将log4j定向至flume，如果需要将slf4j定向至flume，请加入以下配置
```xml
<dependency>
    <groupId>org.apache.flume</groupId>
    <artifactId>flume-ng-embedded-agent</artifactId>
    <version>1.8.0</version>
</dependency>
```
2、配置 log4j.properties 存放于项目 resource目录

```properties
log4j.rootLogger=debug,stdout,flume
log4j.appender.flume=org.apache.flume.clients.log4jappender.Log4jAppender
log4j.appender.flume.Hostname=211.87.235.167
log4j.appender.flume.Port=44444
log4j.appender.flume.layout=org.apache.log4j.PatternLayout
log4j.appender.flume.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %p [%c:%L] - %m

### 输出信息到控制台 ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{hh:mm:ss,SSS} [%t] %-5p [%c:%L] %x - %m%n
```
注：其中log4j.appender.flume.Hostname 和 log4j.appender.flume.Port 两个属性可能需要修改  
3、编写代码  
```java
public class Test {
    private static Logger log = Logger.getLogger(Test.class);
    public static void main(String[] args) {
        for (int i = 0; i < 10; i ++ ) {
            log.info("this is a log. 这是一个log。");
        }
    }
}
```
4、运行测试用例，即可发现服务器配置的 a1.sinks.k1.sink.directory 文件夹中的日志文件中被写入了日志
