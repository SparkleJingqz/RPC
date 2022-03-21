package Server.Registry;

import Server.Error.RpcError;
import Server.Exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认注册表实现类
 */
public class DefaultServiceRegistry implements ServiceRegistry{
    private static Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    //key-服务名， value-提供服务的对象， set存储当前已被保存的服务
    //服务名->对象实现的接口的完整类名 （一个接口只能由一个对象提供服务）
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    @Override
    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            //抛出类无实现接口异常
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        //一个接口对应一个实现服务 - 实现类中的实现
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口: {} 注册服务 {}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            //抛出未发现对应服务异常
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
