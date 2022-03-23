package ByeServiceImpl;

import Annotation.Service;
import ByeService.ByeService;

@Service
public class Bye2Impl implements ByeService {
    @Override
    public String Bye() {
        return "New GoodBye";
    }
}
