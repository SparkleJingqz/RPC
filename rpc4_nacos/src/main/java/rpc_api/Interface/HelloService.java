package rpc_api.Interface;

import rpc_api.Impl.HelloObject;

public interface HelloService {
    String hello(HelloObject object);
    String hello(HelloObject object, Integer i);
}
