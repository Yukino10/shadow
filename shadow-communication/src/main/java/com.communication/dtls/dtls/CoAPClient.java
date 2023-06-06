//package com.communication.dtls.dtls;
//
//import com.communication.utils.ConstUtil;
//import org.eclipse.californium.elements.AddressEndpointContext;
//import org.eclipse.californium.elements.RawData;
//import org.eclipse.californium.elements.RawDataChannel;
//import org.eclipse.californium.elements.util.DaemonThreadFactory;
//import org.eclipse.californium.elements.util.SslContextUtil;
//import org.eclipse.californium.scandium.DTLSConnector;
//import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
//import org.eclipse.californium.scandium.dtls.CertificateType;
//import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.security.GeneralSecurityException;
//import java.security.cert.Certificate;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @Author: bin
// * @Date: 2019/9/27 16:53
// * @Description:
// */
//public class CoAPClient {
//    private static final int DEFAULT_PORT = 5684;
//    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
//    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
//    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
//    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
//    private DTLSConnector dtlsConnector;
//
//    public CoAPClient() {
//        try {
//            // load key store
//            SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
//                    SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "client", KEY_STORE_PASSWORD,
//                    KEY_STORE_PASSWORD);
//            Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
//                    SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);
//
//            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
//            builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));
//            builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),
//                    CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
//            builder.setTrustStore(trustedCertificates);
//            builder.setRpkTrustAll();
//            builder.setConnectionThreadCount(1);
//            dtlsConnector = new DTLSConnector(builder.build());
//            dtlsConnector.setRawDataReceiver(new RawDataChannel() {
//
//                @Override
//                public void receiveData(RawData raw) {
//                    System.out.println("client receive data ");
//                    if (dtlsConnector.isRunning()) {
//                        System.out.println("client received a data -- " + new String(raw.getBytes()));
//                        // TODO 发送后从这里收到确认消息。。
//                        // server 接收完所有的块，响应一次。 这里删掉等待重传的块
////                        receive(raw);
////                        dtlsConnector.send();
//
//                    }
//                }
//            });
//
//        } catch (GeneralSecurityException | IOException e) {
//            System.out.println("Could not load the keystore" + e);
//        }
//    }
//
//    private void start() {
//        try {
//            dtlsConnector.start();
//
//        } catch (IOException e) {
//            System.out.println("Cannot start connector" + e);
//        }
//    }
//
//    private void startTest() {
//
//        while (true) {
////            System.out.println("当前 getSendingList 的长度为" + MessageList.getSendingList().size());
//            List<MessageFragment> fragmentList = MessageList.getSendingList();
//            List<CoAPMessage> messageList = MessageList.getPreSendList();
//            if (fragmentList.size() > 0) {
//                System.out.println("fragmentList.size() > 0");
////                System.out.println("client send a message, 当前 list 的长度为： " + fragmentList.size());
//                MessageFragment message = fragmentList.remove(0);
//                RawData data = RawData.outbound(message.getContent(), new AddressEndpointContext(message.getReceiverAddr()), null, false);
////                RawData data = RawData.outbound(message.getContent(), new AddressEndpointContext(peer), null, false);
//                dtlsConnector.send(data);
////                System.out.println(" a fragment is sended ..============== 长度为： " + message.getContent().length);
//            } else if (messageList.size() > 0) {
//                System.out.println("messageList.size() > 0");
////                System.out.println("当前 persendlist 的长度为 " + messageList.size());
//                CoAPMessage message = messageList.remove(0);
//                System.out.println("================message.getBody().length "  + message.getBody().length);
//                message.send();
//            } else {
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//            }
////            try {
////                Thread.sleep(10);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//
//
//    public void startAClient() {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
//                new DaemonThreadFactory("Auxadfaf#"));
//        final CoAPClient client = new CoAPClient();
//        executor.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                client.start();
//                System.out.println("one client is started ...");
//                client.startTest();
//            }
//        });
//
//    }
//
//
//}
