package Client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * rpc请求,服务端想要调用客户端的请求需至少知道以下四点信息<br>
 * 1.接口名; 2.方法名; 3.方法参数; 4.方法参数类型
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    //方法参数类型字符串实现?
    private Class<?>[] paramTypes;
}
