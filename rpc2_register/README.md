# 1. rpc2_register : 实现注册表，服务端注册多个服务

## 1.1 实现注册表 - ServiceRegistry

- 通过ConcurrentMap存储key服务接口-value服务实现类, 使得一个接口对应一个服务实现类



- Question： 多接口多实现类交叉实现如何完善？
- Question： 为什么选用ConcurrentHashMap?
- Question：  class.getCanonicalName()？

## 1.2 RpcServer修改

- 传入提前注册完毕的注册表ServiceRegistry, 原registry方法改为start方法，将传入的rpcRequest与服务表一同传入处理线程池中

## 1.3 WorkerThread -> RequestHandlerThread,

- 由原来的单一服务处理修改为根据注册表中注册的服务进行对应处理， 具体处理调用RequestHandler （根据获取的待处理服务service与rpcRequest）

## 1.4 RequestHandler

- 根据传入的待处理service与rpcRequest中的参数类型，名称确定调用service中的具体方法 - 反射调用