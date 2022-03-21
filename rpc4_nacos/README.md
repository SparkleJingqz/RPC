# RPC4_nacos

- Nacos：Alibaba开发的服务注册中心，依赖于数据库，建库表*<u>nacos-mysql.sql</u>*



- 通过nacos实现远程注册RemoteRegistry, Nacos通过NamingFactory创建NamingService连接Nacos
  - NamingService - registerInstance : 向Nacos注册服务
  - NamingService - getAllInstances : 获取某个服务的所有提供者列表(基于此后续可在所有提供者中选取最优策略实现负载均衡)
- 优化NettyClient: 通过RemoteRegistry获取服务提供方地址而非初始写死固定。
- 优化NettyServer: 可手动传入Serializer序列化器，传参Host，Port实现灵活注册服务器地址。



- <u>踩坑: Nacos找到bin目录的start.cmd运行之前需要先执行MAVEN安装命令，将nacos-server.jar目录复制到报错目标目录。</u>



Q1：AtomicReference result = new AtomicReference(null);  AtomicReference类原子引用类的作用？

Q2：Netty心跳机制? Netty本身多线程为什么要服务端Handler要使用线程池?

- 异步业务线程池，避免长时间耗时业务阻塞netty本身worker工作线程？
- https://www.cnblogs.com/falcon-fei/p/11422376.html 配合理解

Q3：Nacos注册中心？ Zookeeper/Eureka 分别的原理与实现？

