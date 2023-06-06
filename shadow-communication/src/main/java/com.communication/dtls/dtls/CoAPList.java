//package com.communication.dtls.dtls;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/10/22 14:12
// * @Description:
// */
//public class CoAPList<E> extends ArrayList<E> {
//
////    private static List<E> coAPList = new ArrayList<E>();
//
//    /**
//     * 确保两个线程不能同时add 或 add 时 remove
//     * @param e
//     * @return
//     */
//    public boolean add(E e) {
//        synchronized (this) {
//            return super.add(e);
//        }
//    }
//
//    public boolean addAll(Collection<? extends E> c) {
//        synchronized (this) {
//            return super.addAll(c);
//        }
//    }
//
//
//    public E remove(int index) {
//        synchronized (this) {
//            return super.remove(index);
//        }
//    }
//
//    public boolean remove(Object o) {
//        synchronized (this) {
//            return super.remove(o);
//        }
//    }
//
//    public static void main(String[] args) {
////        new ArrayList<>()
//    }
//}
