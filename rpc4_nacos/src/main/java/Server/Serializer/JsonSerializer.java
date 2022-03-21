package Server.Serializer;

import Client.RpcRequest;
import Server.Enumeration.SerializerCode;
import Server.Exception.SerializerException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生!: {}", e.getMessage());
            throw new SerializerException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            //readValue会将参数中的数据转化为LinkedHashMap ? Why
//            Object obj = objectMapper.convertValue(bytes, clazz);
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            throw new SerializerException("反序列化时有错误发生");
        }
    }

    /**
     * 使用JSON序列化,反序列化Object,无法保证反序列化后恢复原Object,需进行判断<br>
     * 反序列化时序列化器根据字段类型进行反序列化,但Object较为模糊可能出现反序列化失败<br>
     * <b>这里遇到的问题，转化为rpcRequest时对应的参数类型为LinkedHashMap而非参数实体类型，这里的handleRequest方法巧妙地解决了该问题</b><br>
     * 根据rpcRequest中的paramTypes获取Object数组每个实例,辅助反序列化
     * @param obj
     * @return
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            //isAssignableFrom?
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
//                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
//                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
//                rpcRequest.getParameters()[i] = objectMapper.convertValue(rpcRequest.getParameters()[i], clazz);
                rpcRequest.getParameters()[i] = JSON.parseObject(JSON.toJSONString(rpcRequest.getParameters()[i]), clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
