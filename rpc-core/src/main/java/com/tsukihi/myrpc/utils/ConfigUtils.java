package com.tsukihi.myrpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 加载配置对象
     *
     * @param tClass 配置类
     * @param prefix 配置前缀
     * @param <T>    配置类型
     * @return 配置对象
     */

    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }

        // 配置文件读取优先级：properties > yaml > yml
        String[] extensions = {".properties", ".yaml",".yml"};

        for (String extension : extensions) {
            String fileName = configFileBuilder.toString() + extension;
            if (resourceExists(fileName)) {
                if (extension.equals(".properties")) {
                    Props props = new Props(fileName);
                    return props.toBean(tClass, prefix);
                } else {
                    T res = loadYamlConfig(tClass, fileName, prefix);
                    return res;
                }
            }
        }

        throw new RuntimeException("配置文件不存在");
    }

    public static boolean resourceExists(String fileName) {
        return ConfigUtils.class.getClassLoader().getResource(fileName) != null;
    }

    public static <T> T loadYamlConfig(Class<T> tClass, String fileName, String prefix) {
        Yaml yaml = new Yaml();
        InputStream inputStream = ConfigUtils.class
                .getClassLoader()
                .getResourceAsStream(fileName);
        Map<String, Object> obj = yaml.load(inputStream);
        Map<String, Object> subMap = (Map<String, Object>) obj.get(prefix);
        if (subMap == null) {
            throw new RuntimeException("Prefix " + prefix + " not found in map");
        }
        // 将Map转换为你的配置类
        // 这里需要你的配置类有对应的setter方法
        return mapToBean(subMap, tClass);
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> tclass){
        try{
            T instance = tclass.newInstance();

            for(Map.Entry<String, Object> entry : map.entrySet()){
                String key = entry.getKey();
                Object value = entry.getValue();

                Field field = tclass.getDeclaredField(key);
                field.setAccessible(true);

                field.set(instance, value);
            }

            return instance;
        }catch (Exception e){
            throw new RuntimeException("map to bean failed", e);
        }
    }
}
