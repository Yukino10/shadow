测试思路
模拟一个服务器 用于设备注册。
两个设备
假定：设备知道服务器的通信地址
1、设备首先请求服务器地址将自身通信信息发送给服务器。如果设备已有id也要携带id。
2、服务器判断此通信地址是否有绑定id，如果没有生成新id
3、服务器将信息返回。

例如设备初次使用coap注册、再使用mqtt注册
1、设备将自己coap ip + 端口 发给服务器 告知服务器要注册
2、服务器注册成功，将id返回给设备
3、设备将注册信息保存至本地文件
4、设备将mqtt ip + queue + id发送给服务器请求注册
5、服务器将此id的mqtt信息与coap信息对应。 返回注册结果

