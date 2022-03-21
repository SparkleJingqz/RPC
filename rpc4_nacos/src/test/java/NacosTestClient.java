import Client.NettyClient;
import Client.RpcClient;
import Client.RpcClientProxy;
import Server.Serializer.KryoSerializer;
import rpc_api.Impl.HelloObject;
import rpc_api.Interface.HelloService;

public class NacosTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject obj = new HelloObject(122, "Netty Test");
        String res = helloService.hello(obj, 200);
        System.out.println(res);
    }
}
