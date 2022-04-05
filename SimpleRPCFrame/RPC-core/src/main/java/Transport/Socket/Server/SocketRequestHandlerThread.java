package Transport.Socket.Server;

import Coder.SocketDecoder;
import Coder.SocketEncoder;
import Enumeration.SerializerCode;
import Serializer.ObjectSerializer;
import Entity.RpcRequest;
import Entity.RpcResponse;
import Handler.RequestHandler;
import Registry.Local.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.net.Socket;

public class SocketRequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private int serializerCode = SerializerCode.KRYO.getCode();

    public SocketRequestHandlerThread(int serializerCode, Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.serializerCode = serializerCode;
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try {
            RpcRequest rpcRequest = (RpcRequest) SocketDecoder.readObject(socket.getInputStream());
            String interfaceName = rpcRequest.getInterfaceName();
            //获取服务注册表中map 接口-实现类service
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            SocketEncoder.writeObject(socket.getOutputStream(), RpcResponse.success(result), serializerCode);
        } catch (Exception e) {
            logger.error("调用时有错误发生: ", e);
        }

    }
}
