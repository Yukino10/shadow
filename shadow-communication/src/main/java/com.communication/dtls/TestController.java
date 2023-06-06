//package com.communication.coap;
//
//import com.communication.dtls.dtls.CoAPMessage;
//import com.communication.reflect.CoAPController;
//import com.communication.reflect.CoAPService;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * @Author: bin
// * @Date: 2019/10/18 11:32
// * @Description:
// */
//@CoAPController
//public class TestController {
//
//
//
//    @CoAPService("/return_a_file")
//    public CoAPMessage returnAFile(CoAPMessage inMessage) {
//        System.out.println("====================return-a-file======================");
//        File file = new File(CoAPUtil.TEST_IN_MP4_PATH);
//        byte[] body = new byte[(int)file.length()];
//        try {
//            InputStream in = new FileInputStream(file);
//            in.read(body);
//            CoAPMessage outMessage = new CoAPMessage(body, inMessage.getSenderAddr(), CoAPUtil.DONT_RETURN);
//            return outMessage;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @CoAPService("/return_ab_file")
//    public String asldflasd() {
//        return "asdflasd";
//    }
//
//
//
//}
