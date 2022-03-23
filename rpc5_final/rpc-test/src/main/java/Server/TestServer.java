package Server;

import Annotation.ServiceScan;
import Registry.Local.DefaultServiceRegistry;
import Registry.Local.ServiceRegistry;
import Transport.RpcServer;
import Transport.Socket.Server.SocketServer;

@ServiceScan
public class TestServer {
    public static void main(String[] args) {
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
//        HelloService helloService = new Server.HelloServiceImpl();
        /*
        为什么不能注册新
         */
//        ByeService byeService = new ByeService() {
//            @Override
//            public String Bye() {
//                return "这里我自己定义的Bye方法哦！";
//            }
//        };
//        ByeService byeService = new Bye2Impl();
//        serviceRegistry.register(helloService);
//        serviceRegistry.register(byeService);
        RpcServer server = new SocketServer(9999, serviceRegistry);
        server.start();
    }
}
