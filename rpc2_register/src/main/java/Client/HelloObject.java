package Client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
/**
 * hello方法传递的对象,需要向服务器端发送因此需要序列化
 */
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
