package rpc_api.Impl;

import rpc_api.Common.ByeService;

public class Bye2Impl implements ByeService {
    @Override
    public String Bye() {
        return "New GoodBye";
    }
}
