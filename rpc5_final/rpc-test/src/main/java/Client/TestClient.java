package Client;


import ByeService.ByeService;
import HelloService.*;
import Transport.RpcClient;
import Transport.RpcClientProxy;
import Transport.Socket.Client.SocketClient;

public class TestClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient("127.0.0.1", 9999);
        HelloService service = new RpcClientProxy(client).getProxy(HelloService.class);
        ByeService byeService = new RpcClientProxy(client).getProxy(ByeService.class);
        final String ff = service.hello(new HelloObject(2, "ff"));
        final String bye = byeService.Bye();
        System.out.println(ff + " " + bye);
    }
}
