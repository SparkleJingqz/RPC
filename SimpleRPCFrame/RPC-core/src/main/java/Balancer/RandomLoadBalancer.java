package Balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 随机数负载均衡 - 随机选取一个可用界定啊
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance select(List<Instance> instances) {
        int size = instances.size();
        return instances.get((int) (Math.random() * size));
    }
}
