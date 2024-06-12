package com.tsukihi.myrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.date.DateTime;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本机注册的节点key的集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 心跳检测(服务端)
     * 使用 Hutool 工具类的 CronUtil 实现定时任务，对所有集合中的节点执行 重新注册 操作，这是一个小 trick，就相当于续签了。
     * 采用这种实现方案的好处是，即时 Etcd 注册中心的数据出现了丢失，通过心跳检测机制也会重新注册节点信息。
     */
    @Override
    public void heartbeat() {
        // 10s续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本地节点
                for(String key : localRegisterNodeKeySet){
                    try{
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点过期，需要重启节点才能注册
                        if(CollUtil.isEmpty(keyValues)){
                            log.info("节点过期，重新注册节点：{}", key);
                            continue;
                        }
                        // 节点未过期，重新注册相当于续签
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    }catch (Exception e){
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });


        // 支持秒级定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void init(RegistryConfig registryConfig){
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        // 心跳检测
        heartbeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 生成时间属性
        DateTime dateTime = DateTime.now();
        if(serviceMetaInfo.getRegisterTime() == null)
            serviceMetaInfo.setRegisterTime(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
        serviceMetaInfo.setLatestRenewalTime(dateTime.toString("yyyy-MM-dd HH:mm:ss"));

        // 创建Lease 和KV客户端
        Lease leaseClient  = client.getLeaseClient();

        // 创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        // 设置存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        // KV 键值对，通过 put 方法将键值对存储到 Etcd 中
        kvClient.put(key, value, putOption).get();

        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo){
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        // 从本地缓存中移除节点信息
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList != null){
            log.info("从缓存中获取服务列表");
            return cachedServiceMetaInfoList;
        }

        log.info("从注册中心获取服务列表");
        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听key的变化
                        watch(key);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());

            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy(){
        log.info("当前节点下线");
        // 下线节点
        // 被动下线：服务提供者项目异常推出时，利用 Etcd 的 key 过期机制自动移除。
        // JVM 的 ShutdownHook 是 Java 虚拟机提供的一种机制，允许开发者在 JVM 即将关闭之前执行一些清理工作或其他必要的操作，例如关闭数据库连接、释放资源、保存临时数据等。
        // 遍历本节点的所有key
        for(String key : localRegisterNodeKeySet){
            try{
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            }catch (Exception e){
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }

        // 释放资源
        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();
        }
    }

    /**
     * 监听（消费端）
     * 通过调用 Etcd 的 WatchClient 实现监听，
     * 如果出现了 DELETE key 删除事件，则清理服务注册缓存。
     *
     * 注意，即使 key 在注册中心被删除后再重新设置，之前的监听依旧生效。
     * 所以我们只监听首次加入到监听集合的 key，防止重复。
     *
     * @param serviceNodeKey 服务节点key
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
