package Balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮转调度算法 - 全局变量index记录当前应选取的调度
 */
public class RoundRobinLoadBalancer implements LoadBalancer{
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        int size = instances.size();
        if (index >= size) {
            index %= size;
        }
        return instances.get(index++);
    }
}
