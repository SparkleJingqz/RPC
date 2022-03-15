import Client.HelloObject;
import Client.RpcClientProxy;
import Common.HelloService;

/**
 * 1.根据动态代理获取实现类<br>
 * 2.向实现类传参<br>
 * 3.得到结果
 */
public class TestClient {
    public static void main(String[] args) {
        //传递服务器ip与端口号
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        //由动态代理获取调用的服务端实例
        HelloService helloService = proxy.getProxy(HelloService.class);
        //传递服务端所需参数
        HelloObject obj = new HelloObject(12, "Test Message Jing");
        //根据参数获取执行结果
        String res = helloService.hello(obj);
        System.out.println(res);
    }
}
