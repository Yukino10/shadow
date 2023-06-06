package com.communication.coap;

import org.eclipse.californium.scandium.DTLSConnector;

import java.util.UUID;


/**
 * @Author: bin
 * @Date: 2019/9/26 16:22
 * @Description:
 */
public class CoAPUtil {
    // 检查coap配置文件的正确性
    public static boolean checkCoAPConfig() {
        // 检查是否启用了coap
        // 检查是否存在公网ip
        // 检查端口是否被占用
        return true;
    }

    public static final String TEST_IN_IMAGE_PATH = "D:\\test\\coap\\file.jpg";
    public static final String TEST_OUT_IMAGE_PATH = "D:\\test\\coap\\out.jpg";
    public static final String TEST_IN_MP4_PATH = "D:\\test\\coap\\test.mp4";
    public static final String TEST_OUT_MP4_PATH = "D:\\test\\coap\\out.mp4";


    public static final String TEST_IN_FILE_PATH = TEST_IN_IMAGE_PATH;
    public static final String TEST_OUT_FILE_PATH = TEST_OUT_IMAGE_PATH;

    // 告诉server从response中返回响应
    public static final byte RETURN_FROM_SERVER = 1;
    // 让server从client中返回
    public static final byte RETURN_FROM_CLIENT = 2;
    // 不需要返回 意味着，这是对方请求的内容
    public static final byte DONT_RETURN = 3;
    // 消息体中的起始位置
    public static final int RETURN_OPTION_START = 0;
    // 在消息体中占用的长度
    public static final int RETURN_OPTION_LENGTH = 1;

    // 文件传输块的大小 默认4M
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;
    // 一块 传输的默认最大时间 ms
    public static final int BLOCK_SEND_OVER_TIME = 3600;
    // TODO 一帧传输的默认超时重发时间 ms
    public static final int FRAME_SEND_OVER_TIME = 600;


    /** #{@link DTLSConnector} 中此属性为私有。*/
    public static final int MAX_PLAINTEXT_FRAGMENT_LENGTH = 16384; // max. DTLSPlaintext.length (2^14 bytes)
//    public static final int MAX_PLAINTEXT_FRAGMENT_LENGTH = 15000; // max. DTLSPlaintext.length (2^14 bytes)

//    public static final int PROTOCOL_LENGTH = 1;
//    public static int CALLBACK_STATE_LENGTH = 1;
//    public static int DEVICE_ID_LENGTH = 12;
//    public static int SEND_TIME_LENGTH = 8;
    // uuid 占用的 byte数组长度
    public static final int UUID_LENGTH = 32;
    // 当前帧编号 占用的 byte数组长度
    public static final int CURRENT_FRAME_LENGTH = 4;
    // 总帧数 占用的 byte数组长度
    public static final int TOTAL_FRAME_LENGTH = 4;
    // 消息体长度信息 占用的 byte数组长度
    public static final int BODY_LENGTH_LENGTH = 4;


    public static final byte IS_RECEIVED = 1;

    // 数据数组的第一个位置用于存放 该消息是否已经接收
    public static final int STATE_BYTE_START = RETURN_OPTION_START + RETURN_OPTION_LENGTH;
    public static final int STATE_BYTE_LENGTH = 1;
    // uuid 在数组中的起始位置
    public static final int UUID_START = STATE_BYTE_START  + STATE_BYTE_LENGTH;
    // 当前帧 在数组中的起始位置
    public static final int CURRENT_FRAME_START = UUID_START + UUID_LENGTH;
    // 总帧数 在数组中的起始位置
    public static final int TOTAL_FRAME_START = CURRENT_FRAME_START + CURRENT_FRAME_LENGTH;
    // 消息长度信息 在数组中的起始位置
    public static final int BODY_LENGTH_START = TOTAL_FRAME_START + TOTAL_FRAME_LENGTH;
    // 消息体 在数组中的起始位置
    public static final int BODY_START = BODY_LENGTH_START + BODY_LENGTH_LENGTH;


    // 最大的消息体长度
    public static final int MAX_SEND_LENGTH = MAX_PLAINTEXT_FRAGMENT_LENGTH  - STATE_BYTE_LENGTH -
            UUID_LENGTH - RETURN_OPTION_LENGTH
            - CURRENT_FRAME_LENGTH - TOTAL_FRAME_LENGTH - BODY_LENGTH_LENGTH;

    // server 收到消息后的确认 格式 MESSAGE_RECEIVED + UUID + CURRENT_FRAME
    public static final String MESSAGE_RECEIVED = "ACK";

    public static final int ACK_START = 0;
    public static final int ACK_UUID_START = MESSAGE_RECEIVED.length();
    public static final int ACK_CURRENT_FRAME_START = ACK_UUID_START + UUID_LENGTH;


    /**
     * 将long保存为8byte
     * @param values
     * @return
     */
    public static byte[] transLongToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * 将8byte转换为long
     * @param buffer
     * @return
     */
    public static long transBytesToLong(byte[] buffer) {
        long  values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8; values|= (buffer[i] & 0xff);
        }
        return values;
    }

    /**
     * 将int转为4byte
     * @param num
     * @return
     */
    public static byte[] transIntToBytes(int num){
        byte[]bytes=new byte[4];
        bytes[0]=(byte) ((num>>24)&0xff);
        bytes[1]=(byte) ((num>>16)&0xff);
        bytes[2]=(byte) ((num>>8)&0xff);
        bytes[3]=(byte) (num&0xff);
        return bytes;
    }

    /**
     * 将4byte转换为int
     * @param bytes
     * @return
     */
    public static int transBytesToInt(byte[] bytes) {
        return (bytes[0]&0xff)<<24
                | (bytes[1]&0xff)<<16
                | (bytes[2]&0xff)<<8
                | (bytes[3]&0xff);
    }

    public static String createUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

//    public static void addRawDataToCombiner(byte[] data, InetSocketAddress senderAddr, byte returnOption) {
//        byte[] uuidBytes = new byte[UUID_LENGTH];
//        System.arraycopy(data, UUID_START, uuidBytes, 0, UUID_LENGTH);
//        MessageCombiner combiner = MessageList.getReceivingCombinerByUuid(new String(uuidBytes));
//        if (combiner == null) {
//            byte[] totalFrameNum = new byte[TOTAL_FRAME_LENGTH];
//            System.arraycopy(data, TOTAL_FRAME_START, totalFrameNum, 0, TOTAL_FRAME_LENGTH);
//            combiner = new MessageCombiner(new String(uuidBytes), transBytesToInt(totalFrameNum), senderAddr);
//            MessageList.getReceivingList().add(combiner);
//        }
//        combiner.receiveData(data);
//        System.out.println("combiner.getReceivedFragmentNum()" + combiner.getReceivedFragmentNum());
//        System.out.println("combiner.getTotalFragmentNum()" + combiner.getTotalFragmentNum());
//
//        if (combiner.getReceivedFragmentNum() == combiner.getTotalFragmentNum()) {
//            System.out.println("==============combiner.getReceivedFragmentNum() == combiner.getTotalFragmentNum()=");
//        }
//        System.out.println("combiner.getUnReceivedNum().size()" + combiner.getUnReceivedNum().size());
//        if (combiner.getUnReceivedNum().size() == 0) {
//            System.out.println("--------combiner.getUnReceivedNum().size() == 0-");
//        }
//        // 判断是否接收完成，以便执行下一步操作
//        if (combiner.getReceivedFragmentNum() == combiner.getTotalFragmentNum()
//                && combiner.getUnReceivedNum().size() == 0) {
//            CoAPMessage message = transCombinerToMessage(combiner, returnOption);
//            // 尝试获取一下回调函数
//            CoAPMessage callBackMessage = MessageList.getCallBackByUuid(message.getUuid());
//            if (callBackMessage != null) {
//                // TODO 执行回调函数。
////                callBackMessage.
////                MessageList.getPreCallBackList();
//                return;
//            }
//            // 是请求，不是回调
//            MessageList.getPreHandList().add(message);
//        }
//
//    }


//    /**
//     * 将 combiner 转为 message 其中 receiver 是发送方。
//     * @param combiner
//     * @return
//     */
//    public static CoAPMessage transCombinerToMessage(MessageCombiner combiner, byte returnOption) {
//        return new CoAPMessage(combiner.getAllData(), combiner.getSenderAddr(), returnOption);
//    }


//    static MessageDivider transMessageToDivider(CoAPMessage message) {
//
//    }


    public static byte[] generateFrame(String uuid, int currentFrame, int totalFrame, byte[] body, byte returnOption) {
        byte[] content = new byte[MAX_PLAINTEXT_FRAGMENT_LENGTH];
        // combiner 组装时，认为已接收到此分组
        content[RETURN_OPTION_LENGTH] = returnOption;
        content[STATE_BYTE_START] = IS_RECEIVED;
        // 封装uuid
        System.arraycopy(uuid.getBytes(), 0,content, UUID_START, UUID_LENGTH);
        // 封装 当前帧号
        System.arraycopy(transIntToBytes(currentFrame), 0, content, CURRENT_FRAME_START, CURRENT_FRAME_LENGTH);
        // 封装 总帧数
        System.arraycopy(transIntToBytes(totalFrame), 0, content, TOTAL_FRAME_START, TOTAL_FRAME_LENGTH);
        // 封装 消息体的长度信息
        System.arraycopy(transIntToBytes(body.length), 0, content, BODY_LENGTH_START, BODY_LENGTH_LENGTH);
        // 封装 消息体的内容
        System.arraycopy(body, 0, content, BODY_START, body.length);
        return content;
    }



    public static void addHeadForFrame(String uuid, int currentFrame, int totalFrame, int bodyLength, byte[] result, byte returnOption) {

        // combiner 组装时，认为已接收到此分组
        result[RETURN_OPTION_LENGTH] = returnOption;
        result[STATE_BYTE_START] = IS_RECEIVED;
        // 封装uuid
        System.arraycopy(uuid.getBytes(), 0,result, UUID_START, UUID_LENGTH);
        // 封装 当前帧号
        System.arraycopy(transIntToBytes(currentFrame), 0, result, CURRENT_FRAME_START, CURRENT_FRAME_LENGTH);
        // 封装 总帧数
        System.arraycopy(transIntToBytes(totalFrame), 0, result, TOTAL_FRAME_START, TOTAL_FRAME_LENGTH);
        // 封装 消息体的长度信息
        System.arraycopy(transIntToBytes(bodyLength), 0, result, BODY_LENGTH_START, BODY_LENGTH_LENGTH);
    }
}
