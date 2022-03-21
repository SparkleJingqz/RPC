import Server.Enumeration.BalancerType;
import Server.Enumeration.SerializerCode;
import Server.NettyServer;
import Server.Serializer.KryoSerializer;
import rpc_api.Impl.HelloServiceImpl;
import rpc_api.Interface.HelloService;

public class NacosTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(SerializerCode.KRYO.getCode());
        server.setBalancer(BalancerType.ROUND.getCode());
        server.publishService(helloService, HelloService.class);
        server.start();
    }
}
