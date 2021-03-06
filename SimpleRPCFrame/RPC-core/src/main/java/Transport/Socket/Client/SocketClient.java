package Transport.Socket.Client;

import Coder.SocketDecoder;
import Coder.SocketEncoder;
import Entity.RpcResponse;
import Enumeration.RpcError;
import Enumeration.SerializerCode;
import Registry.Remote.NacosRemoteRegistry;
import Serializer.ObjectSerializer;
import Entity.RpcRequest;
import Registry.Remote.RemoteRegistry;
import Transport.RpcClient;
import lombok.extern.slf4j.Slf4j;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketClient implements RpcClient {
    //默认为kryo序列化方式
    private int serializerCode = SerializerCode.KRYO.getCode();
    private int port;
    private String host;
    private static RemoteRegistry remoteRegistry;

    static {
        remoteRegistry = new NacosRemoteRegistry();
    }

    public SocketClient(){}

    public SocketClient(int serializerCode) {
        this.serializerCode = serializerCode;
    }

    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = remoteRegistry.lookupService(rpcRequest.getInterfaceName());
        this.host = inetSocketAddress.getHostName();
        this.port = inetSocketAddress.getPort();

        try (Socket socket = new Socket(host, port)) {
            SocketEncoder.writeObject(socket.getOutputStream(), rpcRequest, serializerCode);
            return SocketDecoder.readObject(socket.getInputStream());
        } catch (IOException | SerialException e) {
            log.error("客户端读写过程有错误发生: {}", e.getMessage());
            return RpcResponse.fail(null);
        }
    }
}
