package Client;


import ByeService.ByeService;
import Enumeration.SerializerCode;
import HelloService.*;
import Transport.RpcClient;
import Transport.RpcClientProxy;
import Transport.Socket.Client.SocketClient;

public class RunClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient(SerializerCode.OBJECT.getCode());
        HelloService service = new RpcClientProxy(client).getProxy(HelloService.class);
        ByeService byeService = new RpcClientProxy(client).getProxy(ByeService.class);
        final String ff = service.hello(new HelloObject(233, "ff"));
        final String bye = byeService.Bye();
        System.out.println(ff + " " + bye);
    }
}
