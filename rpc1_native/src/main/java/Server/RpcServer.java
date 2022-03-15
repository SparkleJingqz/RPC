package Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 反射调用 - ServerSocket监听端口，循环接受连接请求
 */
public class RpcServer {

    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //初始化线程池参数
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    //service对象为所获取的实例待执行对象
    public void register(Object service, int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动....");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端成功连接, IP为: " + socket.getInetAddress() + ", 端口号为: " + socket.getPort());
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
