package Entity;

import Enumeration.StatusCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务端接收到客户端的请求,返回服务端执行后的信息<br>
 * 因需回传,需实现序列化接口
 */
@Data
public class RpcResponse<T> implements Serializable {
    //响应码
    private Integer statusCode;
    //相应信息
    private String message;
    //相应数据
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(StatusCode.SUCCESS.getCode());
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(T data) {
        RpcResponse<T> response = new RpcResponse<T>();
        response.setStatusCode(StatusCode.FAIL.getCode());
        response.setMessage("fail");
        return response;
    }
}
