在项目准备阶段，我们已经预留了一段调用服务的代码，只要能够获取到 UserService 对象（实现类），就能跑通整个流程。

但 UserService 的实现类从哪来呢？

总不能把服务提供者的 UserServiceImpl 复制粘贴到消费者模块吧？要能那样做还需要 RPC 框架干什么？分布式系统中，我们调用其他项目或团队提供的接口时，一般只关注请求参数和响应结果，而不关注具体实现。

在之前的架构中讲过，我们可以通过生成代理对象来简化消费方的调用。

(为了简化消费者发请求的代码，实现类似本地调用的体验。可以基于代理模式，为消费者要调用的接口生成一个代理对象，由代理对象完成请求和响应的过程。

所谓代理，就是有人帮你做一些事情，不用自己操心。)

代理的实现方式大致分为 2 类：静态代理和动态代理，下面依次实现。