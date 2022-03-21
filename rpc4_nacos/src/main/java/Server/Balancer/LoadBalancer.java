package Server.Balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {
    Instance select(List<Instance> instances);

    public static LoadBalancer getInstance(int i) {
        switch (i) {
            case 0:
                return new RandomLoadBalancer();
            case 1:
                return new RoundRobinLoadBalancer();
            default:
                return null;
        }
    }
}
