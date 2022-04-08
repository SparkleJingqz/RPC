package Transport.Socket.Server;

import Enumeration.BalancerType;
import Enumeration.SerializerCode;
import Factory.ThreadPoolFactory;
import Handler.RequestHandler;
import Registry.Local.DefaultServiceRegistry;
import Registry.Local.ServiceRegistry;
import Registry.Remote.NacosRemoteRegistry;
import Registry.Remote.RemoteRegistry;
import Transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;
    //服务表注册, 传入的是已经初测好的registry
    private final ServiceRegistry serviceRegistry;
    private final RemoteRegistry remoteRegistry;
    private int port;
    private int serializerCode;
    private int balancerCode;
    private String host;

    {
        serviceRegistry = new DefaultServiceRegistry();
        //使用线程池工厂创建线程
        threadPool = ThreadPoolFactory.createDefaultThreadPool("Thread-");
    }

    //默认序列化方式为kryo，负载均衡方式为随机数
    public SocketServer(String host, int port) {
        this.port = port;
        this.host = host;
        serializerCode = SerializerCode.KRYO.getCode();
        balancerCode = BalancerType.ROUND.getCode();
        remoteRegistry = new NacosRemoteRegistry(balancerCode);
        scanServices(); //扫描添加方法
    }

    //构造函数手动选择服务端序列化方式与负载均衡方式
    public SocketServer(String host, int port, int serializerCode, int balancerCode) {
        this.port = port;
        this.host = host;
        this.serializerCode = serializerCode;
        remoteRegistry = new NacosRemoteRegistry(balancerCode);
        scanServices(); //扫描添加方法
    }

    //改进后的rpcServer不需要注册服务,只需要开启服务即可,注册服务通过传入的registry
    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动....");
            Socket socket;
            //accept方法不会返回直至ServerSocket中的q队列有套接口, accept方法会返回q队列中的套接口引用,socket引用
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端成功连接, IP为:{}, 端口号为:{} ", socket.getInetAddress(), socket.getPort());
                //调用注册表与处理线程完成服务处理
                threadPool.execute(new SocketRequestHandlerThread(serializerCode, socket, serviceRegistry, new RequestHandler()));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("监听套接口发生错误:{}", e.getMessage());
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        serviceRegistry.register(service);
        remoteRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
    }
}
