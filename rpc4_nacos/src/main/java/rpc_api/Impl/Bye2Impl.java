package rpc_api.Impl;

import rpc_api.Interface.ByeService;

public class Bye2Impl implements ByeService {
    @Override
    public String Bye() {
        return "New GoodBye";
    }
}
