import ByeService.ByeService;
import ByeService.*;
import Enumeration.BalancerType;
import Enumeration.SerializerCode;
import HelloService.HelloService;
import HelloService.HelloServiceImpl;
import Transport.Netty.Server.NettyServer;

public class NacosTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new Bye2Impl()
                ;
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(SerializerCode.KRYO.getCode());
        server.setBalancer(BalancerType.ROUND.getCode());
        server.publishService(helloService, HelloService.class);
        server.publishService(byeService, ByeService.class);
        server.start();
    }
}
