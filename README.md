# RPCImpl

RPC: Remote Procedure Call - 远程过程调用，对应客户端只有通用接口却想要去执行服务端提供的实现类函数/方法，需通过网络来传达调用的语义与数据。

![RpcStructure2](D:\java_web\RPC\static\RpcStructure2.jpg)

**服务寻址** -- 基于Nacos实现服务的注册与发现，通过一定的负载均衡策略寻找服务地址

**传输过程** -- 原生Socket基于Java序列化流(ObjectStream) Netty版本实现Json&Kryo序列化方式并通过应用层自定义传输协议避免粘/拆包

**客户端获取结果** -- 动态代理

**服务器执行方法** -- 反射调用

## 架构-final

![rpc_structure](D:\java_web\RPC\static\rpc_structure.png)

- 客户端向服务器传递RpcRequest来传递自己想要实现的服务，通过动态代理获取实现类方法返回对象。
- 客户端通过Nacos远程注册中心可查询能处理自己请求的服务端列表，根据Nacos实现的负载均衡策略提供具体服务端。
- 服务器接收到RpcRequest后根据接口、方法名、参数、参数类型确定自己要执行的方法，反射调用执行。将结果封装到RpcResponse中回传给客户端。

## 项目版本迭代

### rpc1_native

1. 实现了基于Socket方式与Java原生序列化ObjectStream流的网络传输，服务端未实现注册功能，在RpcServer中register方法手动绑定了HelloService服务（一个服务地址仅绑定了一个服务实现类）。
2. 客户端动态代理RpcClientProxy类向服务器发送RpcRequest请求，返回结果为获取的RpcResponse中的Data数据。
3. 服务器根据获取的RpcRequest反射调用本地实现方法，将结果封装到RpcResponse中回传给客户端。

### rpc2_register

1. 实现注册表(ConcurrentHashMap: key-interface, value-serviceImpl)
2. 将服务端RpcServer原register方法重构为start方法，RpcServer需传入register，解耦RpcServer与实现服务。 
3. 线程处理类WorkerThread->RequestHandlerThread, 由初始的绑定单一服务处理升级为从注册表中获取服务实现。

### rpc3_netty

1. 实现基于Netty方式的网络传输
2. 实现自定义网络传输协议 (见传输协议)，提供封装&拆包服务
3. 实现Json与Kryo两种序列化方法应用于Netty传输方式
4. 抽象化RpcC/S，实现基于Netty与Socket的两种CS架构

### rpc4_nacos

1. 实现基于Nacos的服务注册与发现，注册服务的同时注册提供该服务的服务端地址(host, port)。客户端不再固定远程服务器的IP地址与端口号而是根据Nacos注册中心根据一定的负载均衡策略来选取对应的服务端。
2. 基于1，实现服务端服务的自动注销（避免服务器下线时远程注册表仍保存有该服务端地址造成客户端无法获取服务实现） - *Runtimme.getRuntime().addShutdownHook*调用新线程来实现服务的注销与线程池的关闭

### rpc5_final

1. 实现服务端服务实现类自动注册 - 通过注解
2. 优化项目模块结构

### 项目模块概览 (final)

- **roc-api**	——	通用接口
- **rpc-common**	——	实体对象、工具类等公用类
- **rpc-core**	——	框架的核心实现
- **rpc-test**	——	部署实现类&C\S启动类

## 特性

- 实现了基于 Java 原生 Socket - BIO传输与 Netty - NIO传输两种网络传输方式
- 实现了两种序列化算法，Json 方式、Kryo 方式
- 实现了两种负载均衡算法：随机算法与轮转算法
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 消费端如采用 Netty 方式，会复用 Channel 避免多次连接
- 项目抽象化良好，模块耦合度低，网络传输、序列化器、负载均衡算法可配置
- 实现自定义的顶层传输协议
- 基于注解的服务提供侧自动注册服务

## 自定义传输协议

自定义传输协议结构如下 - 防止拆/粘包：

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

| 字段            |                             解释                             |
| :-------------- | :----------------------------------------------------------: |
| Magic Number    |              魔数，标识自定义协议包，0xCAFEBABE              |
| Package Type    | 包类型，标明这是一个调用请求RpcRequest还是调用响应RpcResponse |
| Serializer Type |     序列化器类型，标明这个包的数据的序列化方式Json/Kryo      |
| Data Length     |                        数据字节的长度                        |
| Data Bytes      |    字节流数据，根据DataLength确定当前反序列化的字节流长度    |

## 启动注意

在此之前请确保 Nacos 运行在本地 `8848` 端口(默认)。

1. 可将nacos-develop文件置于本项目static目录，（需根据官网要求配置好数据库以及生成对应jar包）
2. windows系统可执行nacos-start.bak脚本文件以单机模式standalone启动Nacos
   - or: 进入nacos-develop文件找到bin文件，命令行对其中的startup.cmd执行 `startup.cmd -m standalone`

- 注：本项目rpc5-final中的nacos-start脚本文件为基于个人本地目录结构实现的，不具有通用性。

## ToDo

1. 尝试实现Zookeeper服务端注册，学习Zookeeper机制
2. 尝试多实现序列化机制
3. Netty机制还需学习了解