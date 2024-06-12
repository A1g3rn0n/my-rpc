### 已解决
😅😅😅😅😅😅😅😅😅😅😅😅
又是依赖问题，明明在consumer的pom中引入了core的依赖，但是更改core的依赖clean install后，consumer的依赖还是没有更新
解决就是将新加的依赖手动添加到consumer的pom中，然后再删除就可以了😅😅😅😅😅😅😅😅😅😅😅😅


在使用FixedIntervalRetryStrategy时
运行消费者时，会出现如下错误
```java
//Exception in thread "main" java.lang.NoClassDefFoundError: com/github/rholder/retry/RetryListener
//	at java.base/java.lang.Class.forName0(Native Method)
//	at java.base/java.lang.Class.forName(Class.java:375)
//	at com.tsukihi.myrpc.spi.SpiLoader.load(SpiLoader.java:131)
//	at com.tsukihi.myrpc.fault.retry.RetryStrategyFactory.<clinit>(RetryStrategyFactory.java:11)
//	at com.tsukihi.myrpc.proxy.ServiceProxy.invoke(ServiceProxy.java:108)
//	at jdk.proxy1/jdk.proxy1.$Proxy0.getUser(Unknown Source)
//	at com.tsukihi.example.consumer.ConsumerExample.main(ConsumerExample.java:30)
//Caused by: java.lang.ClassNotFoundException: com.github.rholder.retry.RetryListener
//	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
//	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
//	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
//	... 7 more
```
没找到原因
我把RetryListener相关的代码注释掉了，又会出现
```java
//Exception in thread "main" java.lang.NoClassDefFoundError: com/github/rholder/retry/RetryerBuilder
//at com.tsukihi.myrpc.fault.retry.FixedIntervalRetryStrategy.doRetry(FixedIntervalRetryStrategy.java:21)
//at com.tsukihi.myrpc.proxy.ServiceProxy.invoke(ServiceProxy.java:111)
//at jdk.proxy1/jdk.proxy1.$Proxy0.getUser(Unknown Source)
//at com.tsukihi.example.consumer.ConsumerExample.main(ConsumerExample.java:30)
//Caused by: java.lang.ClassNotFoundException: com.github.rholder.retry.RetryerBuilder
//at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
//at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
//at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
//	... 4 more
```