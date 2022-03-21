package Transport;

/**
 * 反射调用 - ServerSocket监听端口，循环接受连接请求
 */
public interface RpcServer {

    //改进后的rpcServer不需要注册服务,只需要开启服务即可,注册服务通过传入的registry
    public void start();

    //向Nacos注册服务
    <T> void publishService(Object service, Class<T> serviceClass);
}
