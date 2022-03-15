

# 1. rpc1_native

- 初步实现了rpc远程调用(客户端调用服务器service实现类完成远程调用)



## 1.1 RpcRequest

- 客户端想要调用接口函数需至少知道以下四点信息，RpcRequest封装该类信息传递给
  1. 接口名
  2. 函数名
  3. 函数参数
  4. 参数类型



## 1.2 RpcClient

- 客户端rpc
  1. 向指定host， port发送rpcRequest通知服务端根据rpcRequest调用接口函数<u>oos.writeObject(rpcRequest);</u>
  2. 接受服务端函数执行结果。<u>ois.readObject();</u>



### 补充: Java-I/O流 - Java对数据操作方式-流传输

- flush()方法: **刷新输出流并强制写出任何缓冲的输出字节。flush 的一般约定是，调用它表示，如果先前写入的任何字节已被输出流的实现缓冲，则应立即将这些字节写入其预期目的地。**  (缓冲 - 提高I/O性能)





## 1.3 RpcClientProxy

- 由IP地址与端口号根据JDK动态代理获取服务器端实现类

- getProxy方法根据传入的接口信息实现动态调用
- invoke方法基于传入的接口信息clazz以及传入的方法信息(方法名, 参数)决定具体方法的执行

### 补充: 动态代理

- 静态代理: 创建代理类类实现对应接口, 代理类持有被代理对象的引用, 代理类基于被代理类加上自身修缮调用被代理类的方法.  *(一个代理类只能未一个被代理类服务, 若需多种代理则要创建多个代理类)*

- JDK动态代理

  1. 实现InvocationHandler方法, 将被代理类传入作为参数, 实现invoke方法根据传参method调用invoke实现被代理类操作 (可与前后修饰)
  2. 根据**Proxy**的newProxyInstance(类加载器, 实现接口组, 代理类对象)创建代理类实例对象 (接口引用)

  - 运行时动态根据接口以及被代理类创建代理对象
  - InvocationHandler: <u>每个代理实例都会有的一个关联调用处理程序.</u> 对被代理实例进行调用时, 将对方法的调用进行编码并指派到其invoke方法, 根据传入代理对象,方法名称,参数决定具体方法的调用.
  - newProxyInstance(类加载器, 被代理类实现接口组, handler实现类) 创建代理类实例





## 2.1 RpcResponse

- 将输出结果封装, 补充响应码与响应信息



## 2.2 RpcServer

- 初始化线程池参数, 每由一个请求到达时(**accept()返回q队列中建立连接的socket**) 将对应套接口与注册服务传递给线程池去执行



### 补充: 线程池 (Lock & AQS同步队列)

- 存在的意义: 减少创建与销毁线程的时间 - 控制线程数量, 提升响应速度, 线程复用, 实现对线程统一管理



1. corePoolSize : 核心线程数 (若当前线程数<核心线程数,则新建线程执行任务)
2. maximumPoolSize: 最大线程数, 若任务队列已满则新建线程执行任务, 若任务队列已满且当前线程数=maximumPoolSize则拒绝策略
3. BlockingQueue\<Runnable> workQueue: 任务队列, 若当前线程数>=corePoolSize且任务队列未满则将任务存储与任务队列中
   - Array/Linked 先进先出
   - SynchronousQueue 不存储元素, 想插入必须有移除
   - PriorityBlockingQueue 优先级无线阻塞队列 按权重出队
4. long keepAliveTime : 空闲线程的存活时间
5. TimeUnit unit : keepAliveTime的单位
6. ThreadFactory tf : 线程工厂 - 指定如何创建线程
7. RejectedExecutionHandler handler : 拒绝策略 maximumPoolSIze且阻塞队列已满
   - AbortPolicy : 新任务直接拒绝并抛出异常
   - DiscardPolicy : 新任务忽略丢弃
   - DiscardOldestPolicy : 抛弃任务队列队头任务,插入
   - CallerRunPolicy: 新任务使用调用者所在线程执行任务



- 向线程池提交任务的方法
  1. execute : 提交不需要返回值的任务,传入Runnable实例 (socket)
  2. submit: 提交需要返回值的任务, 返回Future对象指示任务是否成功, 可通过其get方法获取返回结果 **(执行get方法会阻塞当前线程直至线程执行完毕 || 带时间参数的get方法只会阻塞一段时间但可能线程没有执行完毕)**



- 关闭线程池:
  1. shutdown : 拒绝新任务,任务队列执行完毕后结束
     - isShutdown=true; isTerminaed=false -> true
  2. shutdownNow : 立即关闭线程池 (逐个调用线程的interrupt方法中断线程)
     - isShutdown=true; isTerminaed=true



## 2.3 WorkerThread

1. 读取输入缓冲区, 提取出rpcRequest
2. 根据传递的服务service与rpcRequest确定要执行的方法
3. 根据方法与rpcRequest中的参数获取执行结果
4. 将执行结果写入输出缓冲区中, flush