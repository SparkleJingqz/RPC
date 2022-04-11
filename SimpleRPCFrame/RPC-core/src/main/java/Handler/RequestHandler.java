package Handler;


import Entity.RpcRequest;
import Entity.RpcResponse;
import Enumeration.RpcError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 处理请求,通过反射方法对注册表中的服务进行调用
 */
@Slf4j
public class RequestHandler {

    //根据请求与对应注册表中的服务进行处理调用
    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务:{} 成功调用方法: {}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log.error("调用时有错误发生:{}", e.getMessage());
            return RpcResponse.fail(null);
        }
        return RpcResponse.success(result);
    }
}