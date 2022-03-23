package Transport.Socket.Server;

import Handler.RequestHandler;
import Registry.Local.ServiceRegistry;
import Transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;
    //服务表注册, 传入的是已经初测好的registry
    private final ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private int port;

    public SocketServer(int port, ServiceRegistry serviceRegistry) {
        this.port = port;
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //初始化线程池参数
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
        this.serviceRegistry = serviceRegistry;
        requestHandler = new RequestHandler();
        scanServices();
    }

    //改进后的rpcServer不需要注册服务,只需要开启服务即可,注册服务通过传入的registry
    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动....");
            Socket socket;
            //accept方法不会返回直至ServerSocket中的q队列有套接口, accept方法会返回q队列中的套接口引用,socket引用
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端成功连接, IP为: " + socket.getInetAddress() + ", 端口号为: " + socket.getPort());
                //调用注册表与处理线程完成服务处理
                threadPool.execute(new SocketRequestHandlerThread(socket, serviceRegistry, requestHandler));
            }
            threadPool.shutdown(); //这里shutDown?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        serviceRegistry.register(service);
    }
}
