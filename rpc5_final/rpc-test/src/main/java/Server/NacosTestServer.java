package NettyTest;

import Annotation.ServiceScan;
import ByeService.ByeService;
import ByeServiceImpl.Bye2Impl;
import Enumeration.BalancerType;
import Enumeration.SerializerCode;
import HelloService.HelloService;
import HelloServiceImpl.HelloServiceImpl;
import Transport.Netty.Server.NettyServer;

@ServiceScan
public class NacosTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new Bye2Impl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(SerializerCode.KRYO.getCode());
        server.setBalancer(BalancerType.ROUND.getCode());
//        server.publishService(helloService, HelloService.class);
        server.publishService(byeService, ByeService.class);

//        NettyServer server1 = new NettyServer("127.0.0.1", 9998);
//        server1.setSerializer(SerializerCode.KRYO.getCode());
//        server1.setBalancer(BalancerType.ROUND.getCode());
//        server1.publishService(helloService, helloService.getClass());
//        server1.start();
        server.start();
    }
}
