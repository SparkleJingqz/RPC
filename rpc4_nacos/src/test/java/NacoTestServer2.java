import Server.Enumeration.BalancerType;
import Server.Enumeration.SerializerCode;
import Server.NettyServer;
import rpc_api.Impl.Bye2Impl;
import rpc_api.Impl.HelloServiceImpl;
import rpc_api.Interface.ByeService;
import rpc_api.Interface.HelloService;

public class NacoTestServer2 {
    public static void main(String[] args) {
        ByeService byeService = new Bye2Impl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(SerializerCode.KRYO.getCode());
        server.setBalancer(BalancerType.ROUND.getCode());
        server.publishService(byeService, ByeService.class);
        server.start();
    }
}
