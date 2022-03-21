import Server.NettyServer;
import Server.Registry.DefaultServiceRegistry;
import Server.Registry.ServiceRegistry;
import rpc_api.Impl.HelloServiceImpl;
import rpc_api.Interface.HelloService;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
