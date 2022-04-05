package ReflectTest;

import ByeService.*;
import Server.ByeServiceImpl.Bye2Impl;

import java.lang.reflect.*;

public class Reflect1 {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        ByeService byeService = new Bye2Impl();
//        final Class<?> proxyClass = Proxy.getProxyClass(ByeService.class.getClassLoader(), Bye2Impl.class.getInterfaces());
//        final Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
//        final ByeService ff = (ByeService) constructor.newInstance(new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println("ff");
//                return method.invoke(byeService, args);
//            }
//        });
//        ff.Bye();
//
//        final ByeService byeService1 = (ByeService) Proxy.newProxyInstance(ByeService.class.getClassLoader(), Bye2Impl.class.getInterfaces(), new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                System.out.println("new");
//                return null;
//            }
//        });
//        byeService1.Bye();
        Proxy.getProxyClass(ByeService.class.getClassLoader(), ByeService.class.getInterfaces()).getConstructor(InvocationHandler.class).newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });

        String s = "ff";
        try {
            final Field value = s.getClass().getDeclaredField("value");
            value.setAccessible(true);
            value.set(s, "hello".toCharArray());
            System.out.println(s);
//            cs[]
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public int get() {
        return 1;
    }
}
