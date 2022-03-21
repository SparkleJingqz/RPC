package ByeService;

import HelloService.HelloServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByeImpl implements ByeService {
    private static final Logger logger = LoggerFactory.getLogger(ByeImpl.class);
    @Override
    public String Bye() {
        return "Goodbye my friend!";
    }
}
