package Serializer;


import javax.sql.rowset.serial.SerialException;

/**
 * 序列化接口<br>
 * 1.序列化<br>
 * 2.反序列化<br>
 * 3.获取序列化器编号<br>
 * 4.根据已知编号获取序列化器<br>
 *
 * */
public interface CommonSerializer {

    byte[] serialize(Object obj) throws SerialException;

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch(code) {
            case 1:
                return new JsonSerializer();
            case 0:
                return new KryoSerializer();
            default:
                return null;
        }
    }
}
