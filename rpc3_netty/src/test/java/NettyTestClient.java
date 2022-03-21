import Client.NettyClient;
import Client.RpcClient;
import Client.RpcClientProxy;
import rpc_api.Impl.HelloObject;
import rpc_api.Interface.HelloService;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject obj = new HelloObject(122, "Netty Test");
        String res = helloService.hello(obj, 12);
        System.out.println(res);
    }
}
