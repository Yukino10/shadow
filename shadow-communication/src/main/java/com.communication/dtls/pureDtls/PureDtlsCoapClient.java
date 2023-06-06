package com.communication.dtls.pureDtls;


import com.communication.coap.OutCoAPMessage;
import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: bin
 * @Date: 2019/10/23 19:57
 * @Description:
 */
public class PureDtlsCoapClient {

    // 当前线程是否正在运行
    AtomicBoolean isRunning = new AtomicBoolean(false);
//    // 当前线程是否应该在完成当前任务后关闭。
//    AtomicBoolean shouldShutDown = new AtomicBoolean(true);

    // 当前client是否空闲（可以发消息）
    volatile AtomicBoolean isUseAble = new AtomicBoolean(true);




    private static final int DEFAULT_PORT = 5684;
    private static final String RECEIVER_IP_ADDR = "localhost";


    private static final long DEFAULT_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(10000);

    private static final Logger LOG = LoggerFactory.getLogger(PureDtlsCoapClient.class.getName());

    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();

    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";

    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();

    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";



    private static CountDownLatch messageCounter;





    private DTLSConnector dtlsConnector;

    private AtomicInteger clientMessageCounter = new AtomicInteger();



    public PureDtlsCoapClient() {

        try {
            // load key store

            SslContextUtil.Credentials clientCredentials = SslContextUtil.loadCredentials(

                    SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "client", KEY_STORE_PASSWORD,

                    KEY_STORE_PASSWORD);

            Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(

                    SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);



            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder();

            builder.setPskStore(new StaticPskStore("Client_identity", "secretPSK".getBytes()));

            builder.setIdentity(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(),

                    CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509);

            builder.setTrustStore(trustedCertificates);

            builder.setRpkTrustAll();

            builder.setConnectionThreadCount(1);

            dtlsConnector = new DTLSConnector(builder.build());

            dtlsConnector.setRawDataReceiver(new RawDataChannel() {



                @Override

                public void receiveData(RawData raw) {

                    if (dtlsConnector.isRunning()) {

                        receive(raw);

                    }

                }

            });



        } catch (GeneralSecurityException | IOException e) {

            LOG.error("Could not load the keystore", e);

        }

    }



    private void receive(RawData raw) {
//        System.out.println("client received a message : " + new String(raw.getBytes()));



        messageCounter.countDown();

        long c = messageCounter.getCount();

        if (LOG.isInfoEnabled()) {

            LOG.info("Received response: {} {}", new Object[] { new String(raw.getBytes()), c });

        }

        if (0 < c) {

            clientMessageCounter.incrementAndGet();

            try {

                RawData data = RawData.outbound(("client response " + c + ".").getBytes(), raw.getEndpointContext(), null, false);

//                dtlsConnector.send(data);

            } catch (IllegalStateException e) {

                LOG.debug("send failed after {} messages", (c - 1), e);

            }

        } else {

//            dtlsConnector.destroy();

        }



    }



    void start() {
        // 此线程不该关闭
//        shouldShutDown.set(false);
        try {
            dtlsConnector.start();

        } catch (IOException e) {

            LOG.error("Cannot start connector", e);

        }

    }



    void startSendMessage() {
        // 线程安全问题。会导致异常，但没事
        isRunning.set(true);
        OutCoAPMessage message = null;

        while (true) {
            if (isUseAble.get()) {
                if ((message = DTLSMessageList.getDTLSMessage()) != null) {
                    RawData data = RawData.outbound(message.getData(), new AddressEndpointContext(new InetSocketAddress(message.getReceiverAddr().getCoAPAddress().getIp(), message.getReceiverAddr().getCoAPAddress().getPort())),
                            new CoAPMessageCallback(dtlsConnector, this), false);
                    System.out.println(Thread.currentThread().getName() + " client send message -- " + new String(message.getData()) + " -- current message amount " + DTLSMessageList.getDTLSMessageAmount());
                    dtlsConnector.send(data);
                } else {
                    System.out.println(Thread.currentThread().getName() + " .get message is null. ");
                    isRunning.set(false);
                    break;
                }
            }
        }

    }



    int stop() {

        if (dtlsConnector.isRunning()) {

            dtlsConnector.destroy();

        }

        return clientMessageCounter.get();

    }




}
