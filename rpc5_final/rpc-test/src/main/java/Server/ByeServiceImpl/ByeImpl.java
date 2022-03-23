package ByeServiceImpl;

import Annotation.Service;
import ByeService.ByeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ByeImpl implements ByeService {
    private static final Logger logger = LoggerFactory.getLogger(ByeImpl.class);
    @Override
    public String Bye() {
        return "Goodbye my friend!";
    }
}
