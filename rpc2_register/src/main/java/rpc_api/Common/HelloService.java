package rpc_api.Common;

import Client.HelloObject;

public interface HelloService {
    String hello(HelloObject object);
    String hello(HelloObject object, Integer i);
}
