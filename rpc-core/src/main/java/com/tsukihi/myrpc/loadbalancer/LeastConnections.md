
如何实现这个最少连接的负载均衡算法呢



看一下在ServiceProxy中的逻辑

首先是获取到注册中心实例之后，调用```registry.serviceDiscovery```方法返回```list<ServiceMetaInfo>```，获取到这个```list```和```requestParam```作为参数传入到```loadBalancer.select()```方法中.


对于该负载均衡算法 一个简单容易理解的方法是为ServiceMetaInfo添加一个```connections```属性，每次调用```select()```方法时，使用```Collections.sort```方法，重写```Comparator```接口，找到```connections```最小的那个ServiceMetaInfo返回即可。
然后对这个ServiceMetaInfo的```connections```属性进行+1操作，表示这个服务的连接数+1

但是这样的实现有一个问题，就是在多线程的情况下，可能会出现并发问题，因为```connections```属性是共享的，可能会出现多个线程同时对这个属性进行+1操作，导致数据不一致，也就是说可能```select```的可能并不是当前连接数最少的服务元。
这个问题是不是可以设置这个属性为```AtomicInteger```类型，这样就可以保证线程安全了呢？

还有一个问题就是，这样违反了**单一职责**的原则，ServiceMetaInfo应该只是一个元数据的载体，不应该包含这个连接数的属性，这个属性应该是在负载均衡算法中维护的，而不是在ServiceMetaInfo中维护的。


另一种方法是，装饰器模式，对```loadBalancer```进行装饰，添加一个```LeastConnectionsDecorator```类，维护一个```Map<ServiceMetaInfo, Integer>```，每次调用```select()```方法时，遍历这个```Map```，找到连接数最少的那个ServiceMetaInfo返回即可。

```java
public class LeastConnectionsDecorator implements LoadBalancer {
    private LoadBalancer loadBalancer;
    private Map<ServiceMetaInfo, Integer> connectionsMap = new HashMap<>();

    public LeastConnectionsDecorator(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfos, Object requestParam) {
        ServiceMetaInfo selectedService = null;
        int min = Integer.MAX_VALUE;
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfos) {
            Integer connections = connectionsMap.get(serviceMetaInfo);
            if (connections == null) {
                connections = 0;
            }
            if (connections < min) {
                min = connections;
                selectedService = serviceMetaInfo;
            }
        }
        connectionsMap.put(selectedService, connectionsMap.get(selectedService) + 1);
        return selectedService;
    }
}
```

同样需要考虑线程安全问题，可以使用```ConcurrentHashMap```来代替```HashMap```，这样就可以保证线程安全了。
但是map中的value是Integer类型，这样也会有线程安全问题，可以使用```AtomicInteger```来代替```Integer```，这样就可以保证线程安全了。

此外就是考虑服务注册、宕机等情况下的这个Map的维护问题，这个可以通过注册中心的监听器来实现，当服务注册、宕机等情况发生时，更新这个Map即可。但是但是这里可能又是属于冗余的操作，因为这个Map的维护应该是在负载均衡算法中维护的


还是认为实现起来比较复杂
最小连接数更多的是站在服务提供者的角度
作为一个框架来说，不希望在服务生产者端额外的维护自己的连接数

一个完整的逻辑应该是这样的

# 好难实现 所以暂且搁置
假设有一个`ConcurrentHashMap<ServiceMetaInfo, AtomicInteger> connectionsMap`
1. 服务提供者将自己的服务注册到注册中心，同时在`connectionsMap`中添加一个`<ServiceMetaInfo, AtomicInteger>`的映射，`value`也就是连接数初始化为0
2. 服务消费者调用时在jdk动态代理的`invoke`方法中，是先根据`invoke`传入参数的`method `的`getDeclaringClass()`作为key
3. 通过调用注册中心**实例**的服务发现获取到服务提供者的列表（显然这里的列表是没有连接数信息的，假设我们通过某种方式可以线程安全的获取和操作这个connectionsMap）
4. 创建一个`LeastConnectionsBalancer`**实例对象**，通过调用它的`select`方法实现负载均衡，具体就是
5. 遍历`list` , 在`map`中找到连接数最小的那个服务，返回。
6. 需要使用`connectionsMap`的地方: 注册中心注册服务时; 服务下线时; 服务消费者负载均衡选择服务增加连接数, 调用完成服务减少连接数; 
7. 所以说这个`connectionsMap`应该是一个全局的变量，应该是一个单例对象，所以可以是`static` 的`connectionsMap`
8. 它应该放在哪个类中呢? 



