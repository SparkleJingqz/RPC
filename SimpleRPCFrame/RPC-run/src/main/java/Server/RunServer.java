package Server;

import Annotation.ServiceScan;
import Enumeration.BalancerType;
import Enumeration.SerializerCode;
import Registry.Local.DefaultServiceRegistry;
import Registry.Local.ServiceRegistry;
import Transport.RpcServer;
import Transport.Socket.Server.SocketServer;

@ServiceScan
public class RunServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9999,
                SerializerCode.OBJECT.getCode(), BalancerType.ROUND.getCode());
        server.start();
    }
}
