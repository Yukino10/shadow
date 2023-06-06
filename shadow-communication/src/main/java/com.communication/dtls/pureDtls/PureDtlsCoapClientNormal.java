//package com.communication.dtls.pureDtls;
//
//
//import org.eclipse.californium.elements.AddressEndpointContext;
//import org.eclipse.californium.elements.RawData;
//import org.eclipse.californium.elements.RawDataChannel;
//import org.eclipse.californium.elements.util.DaemonThreadFactory;
//import org.eclipse.californium.elements.util.SslContextUtil;
//import org.eclipse.californium.scandium.DTLSConnector;
//import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
//import org.eclipse.californium.scandium.dtls.CertificateType;
//import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.security.cert.Certificate;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @Author: bin
// * @Date: 2019/10/23 19:57
// * @Description:
// */
//public class PureDtlsCoapClientNormal {
//    private static final int DEFAULT_PORT = 5684;
//    private static final String RECEIVER_IP_ADDR = "localhost";
//
//
//    private static final long DEFAULT_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(10000);
//
//    private static final Logger LOG = LoggerFactory.getLogger(PureDtlsCoapClientNormal.class.getName());
//
//    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
//
//    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
//
//    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
//
//    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";
//
//
//
//    private static CountDownLatch messageCounter;
//
//
//
//
//
//    private DTLSConnector dtlsConnector;
//
//    private AtomicInteger clientMessageCounter = new AtomicInteger();
//
//
//
//    public PureDtlsCoapClientNormal() {
//
//        try {
//            // load key store
//
//            SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(
//
//                    SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "client", KEY_STORE_PASSWORD,
//
//                    KEY_STORE_PASSWORD);
//
//            Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
//
//                    SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);
//
//
//
//            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();
//
//            builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));
//
//            builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),
//
//                    CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);
//
//            builder.setTrustStore(trustedCertificates);
//
//            builder.setRpkTrustAll();
//
//            builder.setConnectionThreadCount(1);
//
//            dtlsConnector = new DTLSConnector(builder.build());
//
//            dtlsConnector.setRawDataReceiver(new RawDataChannel() {
//
//
//
//                @Override
//
//                public void receiveData(RawData raw) {
//
//                    if (dtlsConnector.isRunning()) {
//
//                        receive(raw);
//
//                    }
//
//                }
//
//            });
//
//
//
//        } catch (GeneralSecurityException | IOException e) {
//
//            LOG.error("Could not load the keystore", e);
//
//        }
//
//    }
//
//
//
//    private void receive(RawData raw) {
////        System.out.println("client received a message : " + new String(raw.getBytes()));
//
//
//
//        messageCounter.countDown();
//
//        long c = messageCounter.getCount();
//
//        if (LOG.isInfoEnabled()) {
//
//            LOG.info("Received response: {} {}", new Object[] { new String(raw.getBytes()), c });
//
//        }
//
//        if (0 < c) {
//
//            clientMessageCounter.incrementAndGet();
//
//            try {
//
//                RawData data = RawData.outbound(("client response " + c + ".").getBytes(), raw.getEndpointContext(), null, false);
//
////                dtlsConnector.send(data);
//
//            } catch (IllegalStateException e) {
//
//                LOG.debug("send failed after {} messages", (c - 1), e);
//
//            }
//
//        } else {
//
////            dtlsConnector.destroy();
//
//        }
//
//
//
//    }
//
//
//
//    void start() {
//        try {
//            dtlsConnector.start();
//
//        } catch (IOException e) {
//
//            LOG.error("Cannot start connector", e);
//
//        }
//
//    }
//
//
//
//    void startSendMessage() {
//        Message message = null;
//        while (true) {
//            if ((message = CoapMessageList.getDTLSMessage()) != null) {
//                RawData data = RawData.outbound(message.getData(), new AddressEndpointContext(message.getSocketAddress()),
//                        new CoAPMessageCallback(dtlsConnector), false);
//                System.out.println(Thread.currentThread().getName() + " client send message -- " + new String(message.getData()) + " -- current message amount " + CoapMessageList.getDTLSMessageAmount());
//                dtlsConnector.send(data);
//            } else {
//                System.out.println(Thread.currentThread().getName() + " .get message is null. ");
////                break;
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//
//    }
//
//
//
//    int stop() {
//
//        if (dtlsConnector.isRunning()) {
//
//            dtlsConnector.destroy();
//
//        }
//
//        return clientMessageCounter.get();
//
//    }
//
//    public void startInThread() {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
//
//                new DaemonThreadFactory("Aux2#"));
//        executor.execute(new Runnable() {
//
//            @Override
//
//            public void run() {
//                start();
//                startSendMessage();
//
//            }
//
//        });
//    }
//
//
//}
