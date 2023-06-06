package com.cislc.shadow.utils.heartbeats;

import com.cislc.shadow.utils.heartbeats.common.PacketProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import static com.cislc.shadow.utils.heartbeats.common.PacketProto.Packet.newBuilder;


/**
 * Created by Yohann on 2016/11/9.
 */
public class HeartBeatClient {

    private static Channel ch;
    private static ChannelFuture future;
    private static Bootstrap bootstrap;
    public String client_id = "";
    public HeartBeatsCallback heartBeatsCallback;

//    public static String host = "211.87.235.127";
//    public static String host = "127.0.0.1";
    public static String host = "192.168.3.158";
    public static int port = 20000;
    public static int interval = 5;

    public HeartBeatClient(String client_id, HeartBeatsCallback heartBeatsCallback) {
        this.client_id = client_id;
        this.heartBeatsCallback = heartBeatsCallback;
    }

    public void start() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,2000)
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new IdleStateHandler(0, interval+1, 0));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ClientHeartbeatHandler(client_id,heartBeatsCallback));
                        }
                    });
            // 连接服务器
            doConnect();
            while (ch.isActive()) {
                System.out.println("发送心跳");
                sendHeartbeatPacket(client_id);
                Thread.sleep(interval * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    private void sendHeartbeatPacket(String id) {
        PacketProto.Packet.Builder builder = PacketProto.Packet.newBuilder();
        builder.setPacketType(PacketProto.Packet.PacketType.HEARTBEAT);
        builder.setData(id);
        PacketProto.Packet packet = builder.build();
        ch.writeAndFlush(packet);
    }

    /**
     * 抽取出该方法 (断线重连时使用)
     *
     * @throws InterruptedException
     */
    public static void doConnect() throws InterruptedException {
        ch = bootstrap.connect(host, port).sync().channel();
    }

    public static void disConnect() {
        ch.close();
    }
}
