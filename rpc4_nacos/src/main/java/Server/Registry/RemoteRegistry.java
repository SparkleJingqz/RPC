package Server.Registry;

import java.net.InetSocketAddress;

/**
 * 远程注册表RemoteRegistry<br>
 * register:注册serviceName与对应的远程服务地址<br>
 * lookupService:根据服务名寻找注册地址
 */
public interface RemoteRegistry {
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    InetSocketAddress lookupService(String serviceName);
}
