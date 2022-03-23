package Transport;

import Annotation.Service;
import Exception.*;
import Annotation.ServiceScan;
import Enumeration.*;
import Utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 反射调用 - ServerSocket监听端口，循环接受连接请求
 */
public interface RpcServer {

    Logger logger = LoggerFactory.getLogger(RpcServer.class);

    //改进后的rpcServer不需要注册服务,只需要开启服务即可,注册服务通过传入的registry
    public void start();

    //向Nacos注册服务
    <T> void publishService(Object service, Class<T> serviceClass);

    /**
     * scanServices方法实现扫描服务
     */
    default void scanServices() {
        String mainClassName = ReflectUtils.getStackTrace();
        Class<?> startClass; //获取启动类

        /*
        获取包扫描范围-  ServiceScan注解的值 -> 通过方法调用栈,main方法在栈的最底端
        ReflectUtils中还有getStackTrace方法获取main方法所在的类
        通过Class对象的isAnnotationPresent方法判断该类是否有@ServiceScan注解
            若有,通过startClass.getAnnotation(ServiceScan.class).value()获取注解的值

        获得包扫描范围后,通过ReflectUtil.getClasses(basePackage)获取到所有Class并逐个判断是否含有Service注解
         */
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误", e);
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        //这里添加的这些补充解释说明的意义在哪？ 可能有多个ServiceScan注解的实现？
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("RpcStarter".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        Set<Class<?>> classSet = ReflectUtils.getClasses(basePackage);
        //获取包结构下所有实现类
        for (Class<?> clazz: classSet) {
            //若当前类含有Service注解
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("RpcService".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> one: interfaces) {
                        publishService(obj, one);
                    }
                } else {
                    //为什么没有@Service注解也要进行一个注册操作？
                    publishService(obj, clazz.getInterfaces()[0]);
                    //publishService(obj, serviceName);
                }
            }
        }
    }
}
