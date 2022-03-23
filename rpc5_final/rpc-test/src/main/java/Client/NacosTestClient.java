package NettyTest;

import ByeService.ByeService;
import HelloService.HelloService;
import Serializer.KryoSerializer;
import Transport.Netty.Client.NettyClient;
import Transport.RpcClientProxy;
import Transport.RpcClient;
import HelloService.HelloObject;

public class NacosTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);

        HelloObject obj = new HelloObject(122, "Netty Test");
        String res = helloService.hello(obj, 200);
        String bye = byeService.Bye();
        System.out.println(res);
        System.out.println(bye);
    }
}