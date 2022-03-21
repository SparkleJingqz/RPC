package rpc_api.Impl;

import rpc_api.Common.ByeService;

public class ByeImpl implements ByeService {
    @Override
    public String Bye() {
        return "Goodbye my friend!";
    }
}
