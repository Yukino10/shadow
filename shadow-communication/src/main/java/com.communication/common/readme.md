好久没有理逻辑了，理下逻辑：
通信和其他模块的交互方式有以下几种可能：
1、其他模块让我的线程发送一条消息。
2、其他模块的线程自己发送一条消息并自己等待返回结果。
3、其他模块让我的线程发送一条消息，收到返回后，执行其他模块的回调。
2和3只需一个即可，当然都实现也可以。 2返回结果到主线程对MQTT很困难，考虑使用回调函数。
4、我收到一条消息，我要交给其他模块。（用我的线程执行其他模块的方法）

用户处理InMessage或 OutMessage -> 添加必要属性产生 例：CoAPOutMessage 
-> CoAPOutMessage 重组byte[] -> 发送 -> 收到响应 -> 转为 CoAPInMessage
-> 转为InMessage 交给用户。

OutMessage --(receiverAddr requestUrl data 回调函数)--> CoAPOutMessage

OutMessage --(receiverAddr senderDeviceId requestUrl data 回调函数)--> MQTTOutMessage

CoAP发送的byte[]包含 data
MQTT发送的byte[]包含 senderDeviceId requestUrl data

CoAPInMessage --(senderAddr=null protocol=coap data)--> InMessage 直接根据requestUrl发给对应的接口。

MQTTInMessage --(senderDeviceId protocol=mqtt data senderAddr)--> InMessage