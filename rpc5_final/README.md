# RPC5_final



Q1： 为什么本地实现的匿名类无法注册成功?

Q2： logger详细理解

Q3： Server.scanService()方法多加理解

Q4:    ReflectUtils反射工具类理解



## 1. 基于注解进行服务自动注册

1. @Service：注解于服务实现类，表示该类提供一个服务
2. @ServiceScan：注解于启动入口类(main方法)，标识服务扫描的包范围(默认值为入口类所在包，扫描所有该包及其子包下所有类，标记@Service类注册)
   - Java方法执行对应入栈到出栈的过程，Main方法在栈底，通过ReflectUtils中*getStackTrace*方法获取main所在类，通过Class对象的*isAnnotationPresent*方法判断该类是否含有ServiceScan注解，通过*startClass.getAnnotation.value()*获取注解的值。 
   - 获取扫描范围后，通过ReflectUtils中的*getClasses*获取所有Class，逐个判断是否含有Service注解，通过反射创建对象并publishService



## 2. ReflectUtil - 反射工具类

Q: Java反射知识点补充



- getStackTrace() : 根据方法栈底获取启动类

- getClass()方法：根据启动类包获取所有实现类 （项目中启动类与服务实现类置于同一包中）。
- findAndAddClassesInPackageByFile：以文件的方式扫描整个包下的文件 并添加到集合中。





## 3. 优化目录结构

- rpc-api: 存储服务接口，供客户端调用/服务端实现-注册
- rpc-common: 存储服务的枚举/异常/工具类/工厂提供/请求相应
- rpc-core: RPC服务的核心实现逻辑，包含负载均衡策略Balancer，根据自定义协议附加/提取的编码解码类Coder，接受服务处理类Handler，钩子-实现服务端关闭时对应服务自动下线Hook，服务端地址&方法注册类Registry，序列化方法Serializer，RPC客户端/服务端以及不同IO的实现方式(Netty/Socket)Transport。





## 4. 添加注解

1. @Service: 置于实现类，标识该类提供一个服务。
2. @ServiceScan: 置于启动入口类，标识服务扫描的包范围。





### Question - 注解详述

- @interface - 声明一个Annotation

1. 作用于代码的注解：@Overrider, @Deprecated
   - @SuppressWarnings: 忽略某些警告 通过传入value={}来对指定警告保持默认
     - *eg(values={"deprecation", "unchecked"})*
   - @FunctionalInterface: 函数式接口
   - @Repeatable: 标识某注解可在同一声明上使用多次
2. 元注解：
   - @Retention: 保留策略 (只在代码中/编入Class文件中/运行时通过反射访问)
     - RetentionPolicy.SOURCE/CLASS/RUNTIME
     - 存活周期 - Java源文件(.java文件) ---> .class文件 ---> 内存中的字节码
     - **一个注解类对应一个Retention，默认为CLASS**
   - @Documented: 标记该注解可出现在javadoc中
   - @Target: 标记该注解应该为哪种Java成员 -> **指定注解修饰的类型**
     - ElementType.TYPE/ANNOTATION_TYPE_METHOD/PACKAGE/METHOD...
     - 一个注解类可对应多个Target， 若无@Target则表明该注解可修饰于任何地方
   - @Inherited: 标记该注解可被继承



- 注解的一些作用

  1. 编译检查/查看结构 : @Override @Deprecated @SuppressWarnings

  2. 根据Annotation生成文档：@Documented使该Annotation出现在javadoc中

  3. 反射调用： 需标注RetentionType为RUNTIME

     eg. 本题将启动类与服务实现类记录于一个server包下，首先通过方法栈结构获取栈底(启动类)方法所属类；

     ```java
     StackTraceElement[] stack = new Throwable().getStackTrace();
     return stack[stack.length - 1].getClassName();
     ```

     通过RPCserver中的默认方法scanService标记启动类所属的包结构

     ```java
     String basePackage = startClass.getAnnotation(ServiceScan.class).value();
     basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
     ```

     而后根据工具类获取该包结构下的所有的类，为含有@Service注解的类进行注册，达到实现类自动注册的目标。

