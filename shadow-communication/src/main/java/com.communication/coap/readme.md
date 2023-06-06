#coap 使用方法
##发送消息
```java
public class TestClient {
    public static void main(String[] args) {
        // 创建一个消息("消息体", "消息要进入的接口", "消息接收方ip+端口")
        OutCoAPMessage message = new OutCoAPMessage("this is a payload ,".getBytes(), "hello", new InetSocketAddress(server_host, 5684));
        // 添加message时，会自动发送
        CoAPMessageList.addCoAPMessage(message);
    }
}
```
##接收消息
```java
// 项目启动时，将扫描此注解
@CommunicationController
public class TestController {
    // 通过此注解定义 "消息要进入的接口" 与发送消息时对应
    // 约定平台开发过程中使用的接口均以 sys_开头，以防与用户自定义接口冲突
    @CommunicationService("hello") // 这是一个错误的示范 应为 "sys_hello"
    // 可用通过inMessage拿到对方发送的信息，进行相应的运算，然后返回 OutMessage或void
    public OutCoAPMessage hello(InCoAPMessage inMessage) {
        System.out.println(Thread.currentThread().getName() + " server handle a request. ");
        // 这里返回OutMessage后，消息会按照请求的路径进行返回。
        return new OutCoAPMessage("hello server result.".getBytes(), inMessage.getSenderAddr());
    }
}
```
##启动项目
```java
public class TestStart {
    public static void main(String[] args) {
        // scanner负责扫描注解
        CommunicationScanner scanner = new CommunicationScanner();
        // 添加需要被扫描的包，注意不要出现重复的包含关系，重复的扫描会导致报错
        scanner.addPackage("com.communication");
        try {
            scanner.getMapping();
        } catch (Exception e) {
            System.out.println("a fault happened when scanning package to find class marked with CommunicationController .");
            e.printStackTrace();
        }
    }
}
```