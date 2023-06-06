package com.cislc.shadow.queue.utils;

import java.util.Random;

/**
 * @ClassName RandomUtils
 * @Description 随机数工具
 * @Date 2019/10/7 11:31
 * @author szh
 **/
public class RandomUtils {

    /**
     * @Description 获取int型随机数
     * @param startNum 起始数字
     * @param range 随机数范围
     * @return 随机数
     * @author szh
     * @Date 2019/10/7 11:33
     */
    public static int getIntRandom(int startNum, int range) {
        Random random = new Random();
        return startNum + random.nextInt(range);
    }

    /**
     * @Description 获取概率
     * @param numerator 分子
     * @param denominator 分母
     * @return 是否在概率中
     * @author szh
     * @Date 2019/10/8 15:55
     */
    public static boolean getProbability(int numerator, int denominator) {
        int random = getIntRandom(1, denominator);
        return random <= numerator;
    }

}
