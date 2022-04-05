package Transport.Socket.Client;

import Coder.SocketDecoder;
import Coder.SocketEncoder;
import Enumeration.SerializerCode;
import Registry.Remote.NacosRemoteRegistry;
import Serializer.ObjectSerializer;
import Entity.RpcRequest;
import Registry.Remote.RemoteRegistry;
import Transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = remoteRegistry.lookupService(rpcRequest.getInterfaceName());
        this.host = inetSocketAddress.getHostName();
        this.port = inetSocketAddress.getPort();

        try (Socket socket = new Socket(host, port)) {
            SocketEncoder.writeObject(socket.getOutputStream(), rpcRequest, serializerCode);
            return SocketDecoder.readObject(socket.getInputStream());
        } catch (IOException | SerialException e) {
            logger.error("传输过程有错误发生: ", e);
            return null;
        }
    }
}
