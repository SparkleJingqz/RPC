package Utils;

import Enumeration.RpcError;
import Exception.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 与Nacos有关的工具类
 * Question : address静态变量的设置原因?
 */
public class NacosUtils {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    private static final NamingService ns;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;


    private static final String SERVER_ADDER = "127.0.0.1:8848";

    static {
        ns = getNacosNamingService();
    }

    /**
     * 向Nacos中注册服务
     */
    public static void registerService(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        ns.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        address = inetSocketAddress; //这里绑定静态变量address的原因？
        serviceNames.add(serviceName);
    }

    /**
     * 获取一个服务的所有解决方
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return ns.getAllInstances(serviceName);
    }

    /**
     * 注销Nacos注册服务
     */
    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    ns.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务{}失败", e);
                }
            }
        }
    }

    /**
     * 获取NamingService
     * @return
     */
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDER);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }
}
