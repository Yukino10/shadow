//package com.communication.dtls.dtls;
//
//import com.communication.coap.CoAPUtil;
//import org.eclipse.californium.elements.Connector;
//import org.eclipse.californium.elements.RawData;
//import org.eclipse.californium.elements.RawDataChannel;
//import org.eclipse.californium.elements.util.DaemonThreadFactory;
//import org.eclipse.californium.elements.util.SslContextUtil;
//import org.eclipse.californium.scandium.DTLSConnector;
//import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
//import org.eclipse.californium.scandium.dtls.CertificateType;
//import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.security.GeneralSecurityException;
//import java.security.cert.Certificate;
//import java.util.Date;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static com.communication.coap.CoAPUtil.*;
//
///**
// * @Author: bin
// * @Date: 2019/9/27 17:44
// * @Description:
// */
//public class CoAPServer {
//    private static org.apache.log4j.Logger log = com.cislc.shadow.log.Logger.getLogger(CoAPServer.class);
//
//    private static final int DEFAULT_PORT = 5684;
//    private static final Logger LOG = LoggerFactory
//            .getLogger(CoAPServer.class.getName());
//    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
//    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
//    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
//    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
//
//    private DTLSConnector dtlsConnector;
//
//    public CoAPServer() {
//        InMemoryPskStore pskStore = new InMemoryPskStore();
//        // put in the PSK store the default identity/psk for tinydtls tests
//        pskStore.setKey("Client_identity", "secretPSK".getBytes());
//        try {
//            // load the key store
//            SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
//                    SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "server", KEY_STORE_PASSWORD,
//                    KEY_STORE_PASSWORD);
//            Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
//                    SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);
//
//            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
////            builder.setRecommendedCipherSuitesOnly(false);
//            builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
//            builder.setPskStore(pskStore);
//            builder.setIdentity(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(),
//                    CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
//            builder.setTrustStore(trustedCertificates);
//            builder.setRpkTrustAll();
//            dtlsConnector = new DTLSConnector(builder.build());
//            dtlsConnector
//                    .setRawDataReceiver(new CoAPServer.RawDataChannelImpl(dtlsConnector));
//
//        } catch (GeneralSecurityException | IOException e) {
//            LOG.error("Could not load the keystore", e);
//            System.out.println("Could not load the keystore");
//        }
//    }
//
//    public void start() {
//        try {
//            dtlsConnector.start();
//            System.out.println("DTLS example server started");
//        } catch (IOException e) {
//            throw new IllegalStateException(
//                    "Unexpected error starting the DTLS UDP server", e);
//        }
//    }
//
//    private class RawDataChannelImpl implements RawDataChannel {
//
//        private Connector connector;
//
//        public RawDataChannelImpl(Connector con) {
//            this.connector = con;
//        }
//        private int num = 0;
//
//        @Override
//        public void receiveData(final RawData raw) {
//            for(int i = 0; i < 10; i ++) {
//                System.out.println(("server 返回的 response " + i));
//                RawData response = RawData.outbound(("server 返回的 response " + i).getBytes(), raw.getEndpointContext(), null, false);
//                connector.send(response);
//            }
//            num ++;
//            log.info("server received a message " + num);
////            System.out.println(new String(raw.getBytes()));
//
//            // TODO 这里还是要判断下，是否需要从server返回。
//            if (raw.getBytes()[RETURN_OPTION_START] == RETURN_FROM_SERVER) {
//                // 从此处返回 （请求体只能有一个, 返回体可以有多个）
//                CoAPMessage message = returnMessageFromServer(raw);
//                if (message != null) {
//
//                }
//
//                // 3、返回
//            } else if (raw.getBytes()[RETURN_OPTION_START] == RETURN_FROM_CLIENT) {
//                // 从新的client返回 （请求体可能分包了）
//            } else if (raw.getBytes()[RETURN_OPTION_START] == DONT_RETURN) {
//                // 不需要返回
//                // 返回 ack
//            }
//            /** server 处理接收到数据的逻辑 ， 有时会调用回调函数， 有时会 返回请求 。 不管怎样都会再压入 list*/
//
//            // 根据 raw获取 message  TODO 这个 RETURN_FROM_CLIENT 待定
//            CoAPUtil.addRawDataToCombiner(raw.getBytes(), raw.getInetSocketAddress(), RETURN_FROM_CLIENT);
//            byte[] ackData = new byte[UUID_LENGTH + CURRENT_FRAME_LENGTH];
//            // 拷贝 ACK 的头
//            System.arraycopy(MESSAGE_RECEIVED.getBytes(), 0, ackData, ACK_START, MESSAGE_RECEIVED.length());
//            // 拷贝 ACK 的uuid
//            System.arraycopy(raw.getBytes(), CoAPUtil.UUID_START, ackData, CoAPUtil.ACK_START, UUID_LENGTH);
//            // 拷贝 ACK 的current frame
//            System.arraycopy(raw.getBytes(), CoAPUtil.CURRENT_FRAME_START, ackData, ACK_CURRENT_FRAME_START, CURRENT_FRAME_LENGTH);
//
//            RawData response = RawData.outbound(ackData, raw.getEndpointContext(), null, false);
//
//
////            System.out.println("server receive a message, 当前 PreHandList 的长度为： " + MessageList.getPreHandList().size());
//
//        }
//
//        /**
//         * 根据客户需求，直接从server返回结果。
//         * @param raw
//         * @return
//         */
//        private CoAPMessage returnMessageFromServer(RawData raw) {
//            // 1、封装成 CoAPMessage
//            byte[] uuidBytes = new byte[UUID_LENGTH];
//            System.arraycopy(raw.getBytes(), UUID_START, uuidBytes, 0, UUID_LENGTH);
//
//            byte[] totalFrameBytes = new byte[TOTAL_FRAME_LENGTH];
//            System.arraycopy(raw.getBytes(), TOTAL_FRAME_START, totalFrameBytes, 0, TOTAL_FRAME_LENGTH);
//            int totalFrame = CoAPUtil.transBytesToInt(totalFrameBytes);
//
//            byte[] currentFrameBytes = new byte[CURRENT_FRAME_LENGTH];
//            System.arraycopy(raw.getBytes(), CURRENT_FRAME_START, currentFrameBytes, 0, CURRENT_FRAME_LENGTH);
//            int currentFrame = CoAPUtil.transBytesToInt(currentFrameBytes);
//
//            if (totalFrame != currentFrame) {
//                log.error("require body shouldn't have two or more fragment, if you need get response from this server !!!");
//                return null;
//            }
//
//            byte[] bodyLengthBytes = new byte[BODY_LENGTH_LENGTH];
//            System.arraycopy(raw.getBytes(), BODY_LENGTH_START, bodyLengthBytes, 0, BODY_LENGTH_LENGTH);
//            int bodyLength = CoAPUtil.transBytesToInt(bodyLengthBytes);
//
//            byte[] body = new byte[bodyLength];
//            System.arraycopy(raw.getBytes(), BODY_START, body, 0, bodyLength);
//
//            CoAPMessage inMessage = new CoAPMessage(body, raw.getInetSocketAddress(), RETURN_FROM_SERVER);
//            inMessage.setUuid(new String(uuidBytes));
//            // TODO handlerUrl  暂时随便生成一个
//            String handlerUrl = "get_a_file";
//
//            // 使用反射调用相应的 方法，以获得结果。
//
//            CoAPMessage outMessage =  new CoAPMessage("sadf".getBytes(), new InetSocketAddress("192.168.10.12", 11), RETURN_FROM_SERVER);
//
//            return outMessage;
//        }
//    }
//
//    public void startAServer() {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
//                new DaemonThreadFactory("Aux" + new Date().getTime() + "#"));
//        executor.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                CoAPServer server = new CoAPServer();
//                server.start();
//                System.out.println("a server is started ...");
//                try {
//                    for (;;) {
//                        Thread.sleep(5000);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//
//
//}
