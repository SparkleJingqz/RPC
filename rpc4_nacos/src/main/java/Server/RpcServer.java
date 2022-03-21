package Server;

import Server.Registry.DefaultServiceRegistry;
import Server.Registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 反射调用 - ServerSocket监听端口，循环接受连接请求
 */
public interface RpcServer {

    //改进后的rpcServer不需要注册服务,只需要开启服务即可,注册服务通过传入的registry
    public void start();

    //向Nacos注册服务
    <T> void publishService(Object service, Class<T> serviceClass);
}
