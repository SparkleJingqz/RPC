package Transport.Socket.Server;

import Coder.SocketDecoder;
import Coder.SocketEncoder;
import Enumeration.SerializerCode;
import Serializer.ObjectSerializer;
import Entity.RpcRequest;
import Entity.RpcResponse;
import Handler.RequestHandler;
import Registry.Local.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class SocketRequestHandlerThread implements Runnable {

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
            SocketEncoder.writeObject(socket.getOutputStream(), result, serializerCode);
        } catch (IOException | SerialException e) {
            log.error("服务器读写过程有错误发生: {}", e.getMessage());
        }
    }
}