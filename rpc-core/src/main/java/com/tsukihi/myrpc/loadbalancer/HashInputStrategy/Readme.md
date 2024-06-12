### 需要思考
对于一致性哈希的负载均衡算法，目前默认使用的用于计算请求的hash参数是消费者的ip地址
这样做的好处是
* 在需要保持会话一致性的场景中，一致性哈希也是一个很好的选择。
* 例如，如果你有一个在线游戏的服务器集群，你可能希望同一个玩家的所有请求都被路由到同一个服务器，
* 这样可以保持玩家的会话状态。在这种情况下，你可以使用玩家的ID或者IP地址作为一致性哈希的输入，
* 这样同一个玩家的所有请求都会被路由到同一个服务器。
* 公网ip的获取方式
是通过http访问一个ip查询的网站，获取到自己的公网ip地址。然后放入缓存中，当ip地址非空时，返回缓存中的地址
* 这样也存在一个问题，公网ip地址的获取依赖于获取ip地址的网站与本机的网络连接，如果所有网站都连不通，那么会有异常抛出。
* 考虑过扩充tcp协议消息头，将ip地址放在消息头中，这样无疑会增加消息头的大小，增加网络传输的开销。尤其当不使用一致性hash算法时，客户端ip往往是不必要的。

但并不意味着一致性哈希只能用于需要保持会话一致性的场景，或者一定需要使用客户端IP地址作为参数。 
一致性哈希的输入可以是任何可以唯一标识请求的数据，例如请求的URL、请求的参数等。
选择哪种数据作为一致性哈希的输入，取决于你的具体需求和场景。

#### 如何满足不同场景的一致性哈希的需求呢？已经实现的是
* LoaderBalancer作为RpcConfig的一个属性，可以通过配置文件进行配置，通过Spi机制进行加载。
* 比较具体的解释就是，将key和接口的实现类的路径写入到resources目录下的META-INF文件夹下的com.tsukihi.myrpc.{包名}.{接口类}文件中
* 在XXXFactory中调用SpiLoader的getInstance方法通过传入的key和XXX.class，获取到对应的实现类实例。
* 这个key就是写在配置文件中，被ConfigUtils读取到的RpcConfig的属性值。


1. 一种看似可行的方式是，粗暴地实现多个一致性哈希的实现类，通过配置文件进行配置，通过Spi机制进行加载。
   1. 但是这样的问题是。多个一致性hash类的唯一区别是，传入的请求的hash参数不同（客户端ip或者请求的URL、请求的参数等）。属于冗余的部分。
   2. 同时，也需要在代理方法中去if-else或者switch判断RpcConfig中配置的具体是哪一种一致性hash实现类。
      1. 这样才可以做到ip作为请求hash计算的唯一参数，而不受到请求参数或者Url之类的其他参数的影响。 
      2. 也可以避免一些资源浪费，虽然method.getName()的开销不大可以忽略，但是获取消费者公网ip地址是通过http请求的方式，即使使用了缓存，但是对于非ip作为hash参数的一致性hash实现类来说，这个开销是不必要的。 
      
      这样实现并不是很优雅，作为比较上层的ServiceProxy应该是不需要关心这些细节的。
   3. 理想的方式是，只有一个一致性hash实现类，同时代理方法中不需要if-else或者switch判断传入哪种参数。
2. 但是第二点有点难以实现。不加判断，就得愣头青传入所有参数到一致性hash实现类中，必然有资源浪费（如ip作为参数时的http请求）
   1. 在RpcConfig中增加一个属性，用于指定一致性hash的hash参数。
   2. 但在代理方法种写if-else或者switch判断，依然是不可避免的。还是需要根据RpcConfig中的配置，来决定给一致性hash实现类的参数是{ipAddress : xxx.xxx.xxx.xxx}还是{method : method.getName()}。
3. 最终采用了工厂模式+策略模式
   1. 优点
      1. 开放/封闭原则：可以在不修改上下文类的情况下引入新的策略。
      2. 避免使用条件语句：策略模式消除了代码中大量的条件语句，使得代码更为简洁。
      3. 可扩展性强：可以随时添加新的策略，扩展性非常好。
   2. 缺点
      1. 增加对象数量：需要维护许多策略类和上下文类，增加了系统的复杂性。
      2. 客户端必须知道所有策略：客户端需要了解所有可用的策略，并自行选择适合的策略。
   3. 策略模式的适用场景
      1. 行为经常改变的场景：如果一个类的行为经常变化，可以使用策略模式。
      2. 需要使用不同算法：需要在不同的时间点使用不同的算法或规则时。
      3. 避免多重条件判断：避免在代码中使用复杂的条件分支结构。

   策略模式通过将具体的行为实现和使用这些行为的代码分离开来，使得行为实现可以独立于上下文类进行修改和扩展，提高了代码的灵活性和可维护性。
```java
// 希望避免在ServiceProxy中使用if-else语句来判断使用哪种哈希输入。一个可能的解决方案是使用策略模式，将哈希输入的选择逻辑封装到一个单独的类中。 
// 首先，我们可以创建一个HashInputStrategy接口，定义获取哈希输入的方法：
public interface HashInputStrategy {
    String getHashInput(RpcRequest rpcRequest);
}

// 然后，我们可以为每种哈希输入创建一个策略类：
public class HashInputWithIPStrategy implements HashInputStrategy {
    @Override
    public String getHashInput(RpcRequest rpcRequest) {
        return ClientInfoUtils.getInstance().getIpAddress();
    }
}

public class HashInputWithMethodNameStrategy implements HashInputStrategy {
    @Override
    public String getHashInput(RpcRequest rpcRequest) {
        return rpcRequest.getMethodName();
    }
}

// 接下来，我们可以创建一个HashInputStrategyFactory类，根据配置来创建对应的策略实例：
// 并不利于扩展，如果有新的参数作为一致性hash计算请求hash值的参数，需要修改这个类。
// Spi机制的实现方式，可以将这个类的实现类写入到resources目录下的META-INF文件夹下的com.tsukihi.myrpc.loadbalancer.HashInputStrategy文件中
// 然后通过SpiLoader的getInstance方法获取到实现类的实例。
public class HashInputStrategyFactory {
    public static HashInputStrategy getStrategy(String strategyType) {
        switch (strategyType) {
            case "consistentHashWithIP":
                return new HashInputWithIPStrategy();
            case "consistentHashWithMethodName":
                return new HashInputWithMethodNameStrategy();
            default:
                throw new IllegalArgumentException("Unknown hash input strategy type: " + strategyType);
        }
    }
}

// 最后，我们可以在ServiceProxy类中使用这个工厂类来获取策略实例，并设置到RpcRequest对象中：
public class ServiceProxy implements InvocationHandler {
    // ...
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // ...
        RpcRequest rpcRequest = RpcRequest.builder()
                // ...
                .build();
        HashInputStrategy strategy = HashInputStrategyFactory.getStrategy(rpcConfig.getLoadBalancer());
        rpcRequest.setHashInput(strategy.getHashInput(rpcRequest));
        // ...
    }
}

```
 ### 所以主要的问题在于 暂且搁置
在何处获取、存储请求端的ip

