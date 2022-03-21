package Transport.Socket.Server;

import Entity.RpcRequest;
import Entity.RpcResponse;
import Handler.RequestHandler;
import Registry.Local.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketRequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;

    public SocketRequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            //获取服务注册表中map 接口-实现类service
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            oos.writeObject(RpcResponse.success(result));
            oos.flush(); //刷新输出缓冲区,保证数据写入输出buffer
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生: ", e);
        }

    }
}
