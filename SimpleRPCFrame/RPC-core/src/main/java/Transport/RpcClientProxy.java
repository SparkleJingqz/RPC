package Transport;


import Entity.RpcRequest;
import Entity.RpcResponse;
import Enumeration.StatusCode;
import Exception.RpcException;
import Transport.Socket.Client.SocketClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端处没有接口HelloService的实现类,通过动态代理生成代理对象
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private RpcClient rpcClient;


    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    //传递host,port指明服务端位置,生成代理对象
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RpcException {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        //根据所传递的request获取response中的data
        RpcResponse rpcResponse = (RpcResponse) rpcClient.sendRequest(request);
        if (rpcResponse.getStatusCode() == StatusCode.FAIL.getCode()) {
            log.error("客户端获取响应结果失败，错误码:{}", rpcResponse.getStatusCode());
            return null;
        }
        return  rpcResponse.getData();
    }
}
