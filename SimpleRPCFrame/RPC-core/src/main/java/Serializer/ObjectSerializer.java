package Serializer;

import Entity.RpcResponse;
import Enumeration.SerializerCode;
import Transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.net.Socket;

public class ObjectSerializer{

    private static final Logger logger = LoggerFactory.getLogger(ObjectSerializer.class);

    /*
    Socket套接口 write方法执行完try块后释放
     */
    public static void write(OutputStream outputStream, Object obj) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(obj);
            oos.flush();
            //服务端先接受后发送，故写入内容为RpcResponse时关闭套接口
            if (obj instanceof RpcResponse) {
                outputStream.close();
            }
        } catch (IOException e) {
            logger.error("写入过程有错误发生: ", e);
        }
    }

    public static Object read(InputStream inputStream) {
        try {
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Object obj = ois.readObject();
            //客户端先发送再接受，故读取到RpcResponse时关闭套接口
            if (obj instanceof RpcResponse) {
                inputStream.close();
            }
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("读取过程有错误发生: ", e);
            return null;
        }
    }
}
