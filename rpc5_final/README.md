# RPC5_final

## 1. 基于注解进行服务自动注册

1. @Service：注解于服务实现类，表示该类提供一个服务
2. @ServiceScan：注解于启动入口类(main方法)，标识服务扫描的包范围(默认值为入口类所在包，扫描所有该包及其子包下所有类，标记@Service类注册)
   - Java方法执行对应入栈到出栈的过程，Main方法在栈底，通过ReflectUtils中*getStackTrace*方法获取main所在类，通过Class对象的*isAnnotationPresent*方法判断该类是否含有ServiceScan注解，通过*startClass.getAnnotation.value()*获取注解的值。 
   - 获取扫描范围后，通过ReflectUtils中的*getClasses*获取所有Class，逐个判断是否含有Service注解，通过反射创建对象并publishService



## 2. ReflectUtil - 反射工具类

- 