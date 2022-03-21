RPC3_netty

- *将DefaultServiceRegistry中的map与set集合用static修饰全局化了，保证全局唯一的注册信息(也不用传参了)*



## 1. NIO方式与Netty

Q1: NIO与Netty待持续完善

http://tutorials.jenkov.com/java-nio/index.html

https://www.zhihu.com/question/29005375

- IO步骤（以read为例子）
  1. 应用程序系统调用read接口
  2. 系统等待数据准备(内核空间数据)；将数据从内核空间拷贝到用户空间(进程)
  3. 应用程序读取用户空间数据

- 传统IO: Blocking IO -> **系统调用过程中在数据包到达并被复制到应用进程缓冲区/发生错误时才返回 - 在此期间一直等待- 进程阻塞与recvfrom调用**
  
- 面向流的处理 - 系统一次一个字节地处理数据。
  
- NIO: Non-Blocking IO -> 系统调用过程若没有数据就直接返回EWOULDBLOCK错误，轮循检查该状态检查内核是否有数据到来 - **进程反复调用recvfrom等待成功**。

  - 面向缓冲区的处理 - 以块的形式处理数据(**Channel管道运输**着**存储数据的Buffer缓冲区**来实现对数据的处理)。

  - Buffer缓冲区：

    - 0<=mart<=position<=limit<=capacity

    - | get()            | put()          | Capacity                                               | Limit                                  | Position                  | Mark                           |
      | ---------------- | -------------- | ------------------------------------------------------ | -------------------------------------- | ------------------------- | ------------------------------ |
      | 读取缓冲区的数据 | 写数据到缓冲区 | 缓冲区能容纳的数据元素的最大数量(*创建时限定且不再变*) | 缓冲区里的数据总数(可以操作的数据大小) | 下一个要被读/写的元素位置 | 记录上一次读写的位置(Position) |

    - flip()方法(切换读模式) -> 写入过程读取数据 （改变Position与Limit位置）
    - clear()方法 - ‘清空缓冲区’ - 遗忘

  - Channel管道：只负责传输数据不负责操作数据

  - Selector选择器：一个线程能管理多个channel状态，选择已就绪的channel

- IO多路复用 - 对文件的操作通过文件描述符(file descriptor)实现  -  **能处理多个连接。**

  - 调用select/poll/epoll/pselect时传入多个文件描述符，若有一个就绪就返回，否则阻塞直至超时。
  - <u>epoll??</u>

## 2. 自定义协议

- 在发送的数据上加上必要数据形成自定义协议 (编码器将原始数据附加说明数据，解码器将原始数据提取出来)

  | Magic Number                | Package Type              | Serializer Type                                              | Data Length               | Data                   |
  | --------------------------- | ------------------------- | ------------------------------------------------------------ | ------------------------- | ---------------------- |
  | 4字节魔数，标识一个协议包。 | 表明当前为请求/相应调用。 | 表明实际数据使用的序列化器（客户端与服务器应对应同一种序列化方式） | 实际数据长度 - 避免粘包。 | 经序列化后的实际数据。 |

- 根据CommonEncoder与CommonDecoder分别编码、解码对应数据，进行附加/提取工作。
  - CommonEncoder， 继承自MessageToByteEncoder，将Message转化为Byte数组，根据如上协议包装成协议包。
  - CommonDecoder，继承自ReplayingDecoder，将接收到的字节序列还原为实际对象 （字段校验、得到序列化器编号以获得正确的序列化方式、获取length字段得知数据包的长度避免粘包->读入正确大小的字节数组->反序列化为正确的对象）





### 3. 自定义序列化器

- CommonSerializer通用接口方法：序列化、反序列化、获取序列化器编号、根据已知序列化器编号获取序列化器。



### 3.1 Json序列化器

- **ObjectMapper方法：**model与JSON之间转换的框架。
- 额外实现方法handleRequest()方法 - 保证反序列化获取的对象为正确的原始对象
  - 使用Json序列化和反序列化Object数组**无法保证反序列化后获取原始对象**
    - JSON数据类似于Key-Value类型，反序列化时可能会出现异常
    - *踩坑： 调用helloService方法传入HelloObject参数，其中HelloObject参数包含两个属性id,String ;然而反序列化后得到的helloService中的parameter并非HelloObject而是LinkedHashMap的两个key-value int, String*
    - 解决方案：引用fastJSON包中的JSON方法parseObject将LinkedHashMap对象转化为对应的Class对象(HelloObject)
- 缺点：基于JSON字符串，占用空间较大且慢；反序列化时易得到不符要求对象。





### 3.2 Kryo序列化器

- 基于字节的序列化，可以记录属性信息，高性能，高效且易用
- 缺点: 可能存在线程安全问题，推荐一个线程一个kryo （ThreadLocal???）



- 踩坑: 调用HelloServiceImpl时需传入HelloObject参数，而HelloObject未设置无参构造函数而报错 (设置无参构造函数后就对了?)





#### Q1: ThreadLocal (InheritableThreadLocal问题仍待解决)

1. ThreadLocal - 对于一个变量，每个线程对其访问都是访问线程自身的变量 -> 避免共享变量访问的线程不安全问题。

2. 结构： Thread类内置两个变量threadLocals与inheritableThreadLocals都是ThreadLocal内部类ThreadLocalMap(类似HashMap)的变量，线程第一次调用set方法时才会创建实例。

   - threadLocals: ThreadLocal.ThtreadLocalMap (key-当前定义ThreadLocal变量this引用，value-set方法设置的值)
   - 每个线程的本地变量存放在自己的本地内存变量threadLocals中
- **InheritableThreadLocal类可以实现子线程访问父线程本地变量，继承自ThreadLocal 仍待解决？**
   
   1. ```java
      public void set(T value) {
          //(1)获取当前线程（调用者线程）
          Thread t = Thread.currentThread();
          //(2)以当前线程作为key值，去查找对应的线程变量，找到对应的map
          ThreadLocalMap map = getMap(t);
          //(3)如果map不为null，就直接添加本地变量，key为当前定义的ThreadLocal变量的this引用，值为添加的本地变量值
          if (map != null)
              map.set(this, value);
          //(4)如果map为null，说明首次添加，需要首先创建出对应的map
          else
              createMap(t, value);
      }
      
      ThreadLocalMap getMap(Thread t) {
          return t.threadLocals; //获取线程自己的变量threadLocals，并绑定到当前调用线程的成员变量threadLocals上？？？
      }
      
      void createMap(Thread t, T firstValue) {
          t.threadLocals = new ThreadLocalMap(this, firstValue);
      }
   ```
   
    2. ```java
       public T get() {
           //(1)获取当前线程
           Thread t = Thread.currentThread();
           //(2)获取当前线程的threadLocals变量
           ThreadLocalMap map = getMap(t);
           //(3)如果threadLocals变量不为null，就可以在map中查找到本地变量的值
           if (map != null) {
               ThreadLocalMap.Entry e = map.getEntry(this);
               if (e != null) {
                   @SuppressWarnings("unchecked")
                   T result = (T)e.value;
                   return result;
               }
           }
           //(4)执行到此处，threadLocals为null，调用该更改初始化当前线程的threadLocals变量
           return setInitialValue();
       }
       
       private T setInitialValue() {
           //protected T initialValue() {return null;}
           T value = initialValue();
           //获取当前线程
           Thread t = Thread.currentThread();
           //以当前线程作为key值，去查找对应的线程变量，找到对应的map
           ThreadLocalMap map = getMap(t);
           //如果map不为null，就直接添加本地变量，key为当前线程，值为添加的本地变量值
           if (map != null)
               map.set(this, value);
           //如果map为null，说明首次添加，需要首先创建出对应的map
           else
               createMap(t, value);
           return value;
       }
    ```
   
   3. ```java
     public void remove() {
      //获取当前线程绑定的threadLocals
       ThreadLocalMap m = getMap(Thread.currentThread());
       //如果map不为null，就移除当前线程中指定ThreadLocal实例的本地变量
       if (m != null)
           m.remove(this);
      }
  ```



- 为什么这里要对Kyro序列化器使用ThreadLocal:？
  - Kryo序列化线程不安全(全局使用一个Kryo可能会不安全 - 多个写入的原因?) - 避免频繁创建Kryo对象且保证线程安全，使用ThreadLocal保证一个线程一个Kryo对象
  - Synchronized同步也可以保证Kryo线程安全，但效率过低

- ThreadLocal内存泄漏的原因： TheadLocalMap使用ThreadLocal的弱引用作为key，当ThreadLocal不存在外部强引用时key在下一次GC时会被回收 -> ThreadLocalMap中的key为null，但value还存在强引用Thread Ref - Thread - ThreadLocalMap - Entry - Value  (线程不shutdown的话key为null的Entry的value一直存在)

  - 为什么key弱引用：key强引用时对应的ThreadLocal不会被回收，弱引用的ThreadLocal不会倒置内存泄漏，*其对应的value在下一次调用get/set/remove方法时会被清楚*

  1. 每次使用ThreadLocal都调用其remove方法清除数据
  2. 将ThreadLocal变量定义为static，一直存在ThreadLocal的强引用，保证任何时候都能通过ThreadLocal弱引用访问到Entry的value进而清除。


