package rpc_api.Impl;

import rpc_api.Interface.ByeService;

public class ByeImpl implements ByeService {
    @Override
    public String Bye() {
        return "Goodbye my friend!";
    }
}
