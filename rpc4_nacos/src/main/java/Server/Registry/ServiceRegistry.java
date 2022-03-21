package Server.Registry;

/**
 * 注册表接口
 */
public interface ServiceRegistry {
    <T> void register(T service);
    Object getService(String serviceName);
}
