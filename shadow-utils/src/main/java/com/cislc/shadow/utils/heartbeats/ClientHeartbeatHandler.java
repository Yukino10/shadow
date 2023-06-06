package com.cislc.shadow.utils.heartbeats;

import com.cislc.shadow.utils.heartbeats.common.PacketProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

import static com.cislc.shadow.utils.heartbeats.common.PacketProto.Packet.newBuilder;

public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private String id;
    private HeartBeatsCallback heartBeatsCallback;
    int i = 0;

    public ClientHeartbeatHandler(String id, HeartBeatsCallback heartBeatsCallback) {
        this.id = id;
        this.heartBeatsCallback = heartBeatsCallback;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        System.out.println("--connect--");
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
        System.out.println("---disconnect---");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- Server is active ---");
        heartBeatsCallback.heartActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- Server is inactive ---");
        // 10s 之后尝试重新连接服务器
        System.out.println("10s 之后尝试重新连接服务器...");
        Thread.sleep(10 * 1000);
        HeartBeatClient.doConnect();
        heartBeatsCallback.heartInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("Client-->userEventTriggered--" + i++);

//        if (evt instanceof IdleStateEvent) {
//            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
//            sendHeartbeatPacket(ctx,id);
//            System.out.println("Client-->userEventTriggered--发送心跳");
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("连接出现异常:" + cause);
        heartBeatsCallback.heartException(cause);
    }

    /**
     * 发送心跳包
     *
     * @param ctx
     */
    private void sendHeartbeatPacket(ChannelHandlerContext ctx, String id) {
        PacketProto.Packet.Builder builder = newBuilder();
        builder.setPacketType(PacketProto.Packet.PacketType.HEARTBEAT);
        builder.setData(id);
        PacketProto.Packet packet = builder.build();
        ctx.writeAndFlush(packet);
    }
}
