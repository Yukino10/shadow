package com.communication.utils;

import com.communication.common.DeviceAddr;
import com.communication.common.Protocol;

import java.io.*;

/**
 * @Author: bin
 * @Date: 2019/11/19 13:26
 * @Description:
 */
public class ConfigUtil {
//    public static CommunicationConfig config;

    // 通讯服务启动过程中赋值
    public static DeviceAddr currentDeviceAddr;
    // 从配置文件中读取本设备的配置
    public static void readConfigFromConfigFile() {

    }


    public static String tryReadDeviceIdFromFile(File file) {
        if (!file.exists()) {
            System.out.println("communicate config file" + file.getAbsolutePath() + " is not exists.");
        } else if (!file.isFile()) {
            System.out.println("communicate config file " + file.getAbsolutePath() + " is not a file, make sure it's not a dictionary.");
        } else {
            if (!file.canRead()) {
                file.setReadable(true);
                System.out.println("communicate config file " + file.getAbsolutePath() + " is not readable.");
            }
            // read config
            byte[] configBytes = new byte[(int) file.length()];
            try {
                InputStream in = new FileInputStream(file);
                in.read(configBytes);
                in.close();
                return new String(configBytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将配置信息写入配置文件，并保存
     * // TODO 保存uuid
     */
    public static void writeDeviceIdToFile(String uuid, File file) {
        System.out.println("configStr: " + uuid);
        try {
            OutputStream out = new FileOutputStream(file);
            out.write(uuid.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
