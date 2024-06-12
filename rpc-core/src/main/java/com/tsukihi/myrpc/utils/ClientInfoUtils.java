package com.tsukihi.myrpc.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 获取客户端信息 工具类
 * 目的：1.多线程下保证每个消费者拥有自己的ip地址
 * 2.ip地址获取一次后，不会再次获取
 */
@Getter
@Slf4j
public class ClientInfoUtils {

    /**
     * 保存客户端ip地址
     * ThreadLocal 保证每个线程拥有自己的ip地址
     */

    // 获取公网IP地址
    private static String getPublicIPAddress() {
        String[] urls = {
                "http://api.ipify.org",
                "http://checkip.amazonaws.com",
                "http://icanhazip.com",
                "http://bot.whatismyipaddress.com",
                "http://ipinfo.io/ip"
        };
        for (String urlStr : urls) {
            try {
                URL url = new URL(urlStr);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String ip = reader.readLine();
                if (ip != null && !ip.isEmpty()) {
                    return ip;
                }
            } catch (Exception e) {
                // Ignore and try next URL
            }
        }
//        throw new RuntimeException("Failed to get public IP address");
        log.info("Failed to get public IP address");
        return "0.0.0.0";
    }

    private static final ClientInfoUtils instance = new ClientInfoUtils();
    private final String ipAddress;

    private ClientInfoUtils() {
        // 初始化IP地址
        this.ipAddress = initIpAddress();
    }

    // 获取单例实例
    public static ClientInfoUtils getInstance() {
        return instance;
    }

    // 获取IP地址
    public String getIpAddress() {
        return ipAddress;
    }

    // 初始化IP地址，实际项目中可能需要根据具体情况获取真实的IP地址
    private String initIpAddress() {
        String ip = getPublicIPAddress(); // 默认获取公网IP地址
        return ip;
    }

    /** 通过使用 ThreadLocal，每个线程都有自己的独立变量副本。
     * 在这个例子中，每个线程在第一次访问 threadLocalIpAddress 时会调用 getPublicIPAddress() 获取一个 IP 地址，
     * 并存储在自己的 ThreadLocal 变量中。后续的访问将直接返回这个存储的值，
     * 而不会再调用 getPublicIPAddress()，确保了 IP 地址只获取一次。
     *
     * e.g. 有一个装有不同颜色粉笔的盒子，每个粉笔盒只属于一个特定的老师（线程），
     * 每个老师拿到自己的粉笔盒后，都可以随意使用里面的粉笔（变量），但不会影响其他老师的粉笔盒。
     *
     *
     * *  按理说是单例模式
     *  你不需要为每个线程分配一个唯一的IP地址
     *  IP地址的需求是不同的。IP地址在你的场景中是一个全局唯一且不变的值，适用于所有线程共享。因此，使用 ThreadLocal 来管理 IP 地址并不合适，也不必要。
     */
//    private static final ThreadLocal<String> threadLocalIpAddress = ThreadLocal.withInitial(() -> {
//        String ipAddress = getPublicIPAddress();
//        log.info("Thread {} has IP address: {}", Thread.currentThread().getId(), ipAddress);
//        return ipAddress;
//    });
//
//    private ClientInfoUtils() {
//        // 私有构造函数，防止实例化
//    }
//
//    public static String getIpAddress() {
//        return threadLocalIpAddress.get();
//    }
//
//
//    public static void clearIpAddress() {
//        threadLocalIpAddress.remove();
//    }


//    public static ClientInfoUtils getInstance() {
//        if (instance == null) {
//            log.info("Load ip from new instance");
//            synchronized (ClientInfoUtils.class) {
//                if (instance == null) {
//                    instance = new ClientInfoUtils();
//                }
//            }
//        }else{
//            log.info("Load ip from existed instance");
//        }
//        return instance;
//    }

}
