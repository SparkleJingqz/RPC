import rpc_api.Common.ByeService;
import rpc_api.Common.HelloService;
import rpc_api.Impl.Bye2Impl;
import rpc_api.Impl.ByeImpl;
import rpc_api.Impl.HelloServiceImpl;
import Server.Registry.DefaultServiceRegistry;
import Server.Registry.ServiceRegistry;
import Server.RpcServer;

/**
 * 注册实现类以及对应接受端口信息
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new ByeImpl();
        ByeService byeService1 = new Bye2Impl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(byeService);
        serviceRegistry.register(byeService1);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
