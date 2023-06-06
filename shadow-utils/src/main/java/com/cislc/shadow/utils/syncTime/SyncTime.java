package com.cislc.shadow.utils.syncTime;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncTime {
    private static String timeHost = "47.92.105.44";
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public TimeCallback timeCallback;

    public SyncTime(TimeCallback timeCallback) {
        this.timeCallback = timeCallback;
    }

    public String getDateTime() {
        try {
            NTPUDPClient timeClent = new NTPUDPClient();
            InetAddress timeServerAddress = InetAddress.getByName(timeHost);
            TimeInfo timeInfo = timeClent.getTime(timeServerAddress);
            TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
            String time = dateFormat.format(timeStamp.getDate());
//            System.out.println("SyncTime-->"+time);
            timeCallback.syncComplete(time);
            return time;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("与ntp服务器同步时间错误！" + e);
            return dateFormat.format(new Date());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("与ntp服务器同步时间错误！" + e);
            return dateFormat.format(new Date());
        }
    }
}
