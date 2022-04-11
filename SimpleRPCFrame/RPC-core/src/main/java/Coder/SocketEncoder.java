package Coder;

import Entity.RpcResponse;
import Enumeration.RpcError;
import Enumeration.SerializerCode;
import Exception.*;
import Entity.RpcRequest;
import Enumeration.PackageType;
import Serializer.CommonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.reflections.serializers.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Serializer.*;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class SocketEncoder {
    private static final int MAGIC_NUMBER=0xCAFEBABE;

    public static void writeObject(OutputStream outputStream, Object object, int serializerCode) throws IOException, SerialException{
        outputStream.write(intToBytes(MAGIC_NUMBER));
        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(intToBytes(serializerCode));
        if (serializerCode == SerializerCode.OBJECT.getCode()) {
            ObjectSerializer.write(outputStream, object);
            return;
        }
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            log.error(RpcError.SERIALIZER_NOT_FOUND + ":{}", serializerCode);
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        byte[] bytes = serializer.serialize(object);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();
    }

    private static byte[] intToBytes(int value){
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
