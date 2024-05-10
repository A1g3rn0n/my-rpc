package com.tsukihi.myrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.tsukihi.myrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器（支持键值对映射）
 *
 * 相当于一个工具类，提供了读取配置并加载实现类的方法。
 * 关键实现如下：
 * 用 Map 来存储已加载的配置信息 键名 => 实现类。
 * 扫描指定路径，读取每个配置文件，获取到 键名 => 实现类 信息并存储在 Map 中。
 * 定义获取实例方法，根据用户传入的接口和键名，从 Map 中找到对应的实现类，然后通过反射获取到实现类对象。
 * 可以维护一个对象实例缓存，创建过一次的对象从缓存中读取即可。
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类： 接口名 => (key => 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复创建）, 类路径 => 对象实例， 单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有SPI");
        for (Class<?> clazz : LOAD_CLASS_LIST) {
                load(clazz);
        }
    }

    /**
     * 获取某个接口的实例
     *
     * @param tClass 接口类
     * @param key 实现类的key
     * @param <T> 接口类型
     * @return 接口实例
     */
    public static <T> T getInstance(Class<T> tClass, String key){
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if(keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if(!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型（实现类）", tClassName, key));
        }

        // 获取到要加载的实现类
        Class<?> implClass = keyClassMap.get(key);

        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if(!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errMsg = String.format(" %s 类实例化失败", implClassName);
                throw new RuntimeException(errMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型
     */
    public static Map<String, Class<?>> load(Class<?> loadClass){
        log.info("加载类型为 {} 的SPI", loadClass.getName());
        // 扫描路径，用户自定义的SPI优先级高于系统SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for(String dir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            // 读取每个文件
            for(URL resource : resources){
                try{
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        String[] split = line.split("=");
                        if(split.length > 1){
                            String key = split[0];
                            String className = split[1];
                            Class<?> implClass = Class.forName(className);
                            keyClassMap.put(key, implClass);
                        }
                    }
                }catch (Exception e){
                    log.error("spi resource load error", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "kryo");
        System.out.println(serializer);
    }

}
