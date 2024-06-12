package com.tsukihi.myrpc;
import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.constant.RpcConstant;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.utils.ConfigUtils;
import com.tsukihi.myrpc.config.RpcConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Rpc框架应用
 * 相当于holder，存放了项目全局用到的变量。双检索单例模式实现
 *
 * 支持在获取配置时才调用init方法实现懒加载
 * 为了便于扩展，支持自己传入配置对象
 * 若不传入，则默认调用前面写好的 ConfigUtils.loadConfig方法加载配置
 *
 * 之后RPC框架内只需要写一行代码，就能正确加载到配置
 * RpcConfig rpcConfig = RpcApplication.getRpcConfig();
 */

@Slf4j
public class RpcApplication {

    /**
     * 全局配置实例
     */
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册Shutdown Hook, JVM关闭前释放资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown Hook was invoked. Shutting down...");
            registry.destroy();
        }));
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            // 加载配置失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);

    }

    /**
     * 获取配置
     *
     * 双重检查锁定 单例模式实现方式
     * 同步代码块，它使用RpcApplication.class对象作为锁。
     * 当多个线程同时调用getRpcConfig()方法时，只有一个线程能够进入同步代码块，
     * 执行init()方法。其他线程将在同步代码块外部等待，
     * 直到第一个线程完成init()方法的调用并退出同步代码块，释放锁。
     * 这样可以确保RpcApplication类的初始化过程只会被执行一次。
     *
     *
     * @return
     *
     */
    public static RpcConfig getRpcConfig(){
        // 如果rpcConfig不为null，则直接返回，避免了不必要的同步
        if(rpcConfig == null){
            synchronized (RpcApplication.class){
                // 如果rpcConfig仍然为null，则进行初始化。
                // 这样可以确保即使有多个线程同时调用getRpcConfig()方法，
                // RpcApplication类的初始化过程也只会被执行一次。
                if(rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
