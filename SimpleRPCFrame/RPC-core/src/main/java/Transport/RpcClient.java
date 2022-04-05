package Transport;

import Entity.RpcRequest;

public interface RpcClient {

    public Object sendRequest(RpcRequest rpcRequest);
}
