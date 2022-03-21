import rpc_api.Common.HelloService;
import Server.HelloServiceImpl;
import Server.RpcServer;

/**
 * 注册实现类以及对应接受端口信息
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
