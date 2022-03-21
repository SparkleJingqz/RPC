package ProxyTest;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicProxy implements InvocationHandler {
    private Object object;

    public DynamicProxy (Object o) {
        object = o;
    }

    //获取代理类实例
    public Object getProxy() {
        return Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), object.getClass().getInterfaces(), this);
    }

    public Object getProxy1() {
        Class proxyClass = Proxy.getProxyClass(DynamicProxy.class.getClassLoader(), object.getClass().getInterfaces());
        try {
            //为什么必须是这个class对象
//            Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
//            Object o = constructor.newInstance(this);
            Object obj = proxyClass.getConstructor(InvocationHandler.class).newInstance(this);
            return obj;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //增强被代理类功能
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("动态代理");
        Object o = method.invoke(object, args);
        return o;
    }
}
