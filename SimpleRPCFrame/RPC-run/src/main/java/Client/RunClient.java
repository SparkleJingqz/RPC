package Client;


import ByeService.ByeService;
import Enumeration.SerializerCode;
import HelloService.*;
import Transport.RpcClient;
import Transport.RpcClientProxy;
import Transport.Socket.Client.SocketClient;

public class RunClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient(SerializerCode.KRYO.getCode());
        HelloService service = new RpcClientProxy(client).getProxy(HelloService.class);
        ByeService byeService = new RpcClientProxy(client).getProxy(ByeService.class);
        test test = new RpcClientProxy(client).getProxy(test.class);
        final String ff = service.hello(new HelloObject(233, "ff"));
        final String bye = byeService.Bye();
        test.get();
        System.out.println(ff + " " + bye);
    }

    interface test {
        int get();

    }
}
