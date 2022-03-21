package Server.Registry;

import Server.Balancer.LoadBalancer;
import Server.Error.RpcError;
import Server.Exception.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 使用Nacos实现的远程注册中心<br>
 * 通过NamingFactory创建NamingService连接Nacos(为什么不需要登陆验证?)<br>
 * registerInstance向Nacos注册服务; getAllInstances获得提供某个服务的所有提供者列表
 */
public class NacosRemoteRegistry implements RemoteRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosRemoteRegistry.class);

    private static final String SERVER_ADDER = "127.0.0.1:8848";
    private static final NamingService ns;
    private LoadBalancer loadBalancer;


    static {
        try {
            ns = NamingFactory.createNamingService(SERVER_ADDER);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    //默认获取的时随机均衡策略
    public NacosRemoteRegistry() {
        this.loadBalancer = LoadBalancer.getInstance(0);
    }

    public NacosRemoteRegistry(int code) {
        this.loadBalancer = LoadBalancer.getInstance(code);
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            ns.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生: ", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            //获取所有能提供serviceName服务的提供者列表,首先获取第0个,具体可通过负载均衡策略实现最优化
            List<Instance> instances = ns.getAllInstances(serviceName);
            Instance ins = loadBalancer.select(instances); //选择负载均衡策略选取服务提供方
            return new InetSocketAddress(ins.getIp(), ins.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生: ", e);
            return null;
        }
    }

    public void setLoadBalancer(int code) {
        this.loadBalancer = LoadBalancer.getInstance(code);
    }
}
