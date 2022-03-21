package ProxyTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class StaticProxy implements Adder{
    public static void main(String[] args) {
        Adder adder = new AdderImpl();
        Cut cut = new CutImpl();
        StaticProxy staticProxy = new StaticProxy(adder);
        Cut cut1 = (Cut) new DynamicProxy(cut).getProxy1();

        Adder adder2 = (Adder) Proxy.newProxyInstance(adder.getClass().getClassLoader(), adder.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("动态代理");
                return method.invoke(adder, args);
            }
        });

        cut1.cut();
//        System.out.println(staticProxy.add1(2));
//        System.out.println(adder2.add1(2));
    }
    private Adder adder;

    public StaticProxy (Adder adder) {
        this.adder = adder;
    }

    public int add1(int a) {
        System.out.println("加1前");
        int i = adder.add1(a);
        System.out.println("加1后");
        return i;
    }
}
